package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.common.GrappleLogic;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.entity.EntityBolt;
import com.darkona.adventurebackpack.entity.EntityGrapple;
import com.darkona.adventurebackpack.init.ModItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * The Clockwork Crossbow: a repeating crossbow with a magazine fed by {@link ItemBoltCartridge}.
 * <p>
 * Every action is a right click handled on the server (Tier 2). Holding right click repeats it every 4 ticks, so a
 * held click fires the magazine one bolt after another with {@code crossbow.shotDelayTicks} between bolts. The item
 * is never "in use", which keeps the client from dropping the use state each time the server updates the stack, and
 * the item has no per-tick work at all: pauses are world-tick timestamps in the stack NBT, compared on the next click.
 * The NBT keys are public so the renderer can read them; see {@code notes/ballesta-interfaz.md}.
 */
public class ItemCrossbow extends ItemAB
{
    /** int: bolts in the magazine. */
    public static final String TAG_AMMO = "Ammo";
    /** int: type of the loaded bolts in 1.0 (elemental bolts, removed); dropped from old stacks on the next click. */
    static final String LEGACY_TAG_AMMO_TYPE = "AmmoType";
    /** long: world tick from which the crossbow can fire or reload again. */
    public static final String TAG_READY_AT = "ReadyAt";
    /** long: world tick of the last shot (recoil animation). */
    public static final String TAG_LAST_SHOT = "LastShot";
    /** long: world tick when the last cartridge went in (reload animation; it lasts crossbow.reloadTicks). */
    public static final String TAG_LAST_RELOAD = "LastReload";
    /** long: world tick of the last dry click (limits the out-of-ammo message). */
    public static final String TAG_LAST_DRY = "LastDry";
    /** int: bolts fired, against crossbow.durability. */
    public static final String TAG_WEAR = "Wear";
    /** int: firing mode, one of the {@link CrossbowLogic} modes (0 = repeating). Read it with {@link #getMode}. */
    public static final String TAG_MODE = "Mode";
    /** int: entity id of the grappling hook this crossbow fired last (server only; a stale id is harmless). */
    public static final String TAG_GRAPPLE = "Grapple";

    private static final int EMPTY_MESSAGE_QUIET_TICKS = 20;

    public ItemCrossbow()
    {
        super();
        setFull3D();
        setUnlocalizedName("clockworkCrossbow");
        setMaxStackSize(1);
    }

    @Override
    public boolean canItemEditBlocks()
    {
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    /** Never "in use": see the class comment. The aiming pose is the renderer's job. */
    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.none;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 0;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote || !ConfigHandler.CROSSBOW_ENABLED) return stack;
        NBTTagCompound tag = tag(stack);
        if (tag.hasKey(LEGACY_TAG_AMMO_TYPE)) tag.removeTag(LEGACY_TAG_AMMO_TYPE);
        int mode = getMode(stack);
        if (mode == CrossbowLogic.MODE_GRAPPLE)
        {
            // While the hook is out a click does nothing: a held click repeats every 4 ticks and would pull it back.
            if (EntityGrapple.find(world, tag.getInteger(TAG_GRAPPLE), player) != null) return stack;
        }
        int capacity = ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE;
        int ammo = CrossbowLogic.clampAmmo(tag.getInteger(TAG_AMMO), capacity);
        int cost = CrossbowLogic.shotCost(mode == CrossbowLogic.MODE_GRAPPLE ? ConfigHandler.GRAPPLE_CHARGES : 1, capacity);
        long now = world.getTotalWorldTime();
        boolean deployed = mode == CrossbowLogic.MODE_SNIPER && isBipodDeployed(player);
        // With the bipod down sneaking is part of the aim, so sneak + click fires instead of swapping the cartridge.
        boolean sneaking = player.isSneaking() && !deployed;
        boolean creative = player.capabilities.isCreativeMode;
        int slot = sneaking || ammo < cost ? findCartridge(player) : -1;

        switch (CrossbowLogic.decide(ammo, cost, capacity, now, tag.getLong(TAG_READY_AT), maxWait(), sneaking, slot >= 0 || creative))
        {
            case FIRE:
                if (mode == CrossbowLogic.MODE_GRAPPLE)
                {
                    fireGrapple(stack, tag, world, player, ammo, cost, now, creative);
                } else if (deployed)
                {
                    fireSniper(stack, tag, world, player, ammo, now, creative);
                } else
                {
                    fireBolt(stack, tag, world, player, ammo, now, creative);
                }
                break;
            case RELOAD:
                reload(tag, world, player, slot, ammo, capacity, now, creative);
                break;
            case DRY:
                dryFire(tag, world, player, now);
                break;
            default:
                break;
        }
        return stack;
    }

    /** Longest pause any action can set; a {@code ReadyAt} further away is stale (the stack came from another world). */
    private static int maxWait()
    {
        int pause = Math.max(ConfigHandler.CROSSBOW_SHOT_DELAY_TICKS, ConfigHandler.CROSSBOW_RELOAD_TICKS);
        return Math.max(pause, Math.max(ConfigHandler.GRAPPLE_COOLDOWN_TICKS, ConfigHandler.SNIPER_SHOT_DELAY_TICKS));
    }

    private static NBTTagCompound tag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    /** First cartridge in the inventory, hotbar first, or -1. */
    private static int findCartridge(EntityPlayer player)
    {
        ItemStack[] inventory = player.inventory.mainInventory;
        for (int i = 0; i < inventory.length; i++)
        {
            if (inventory[i] != null && inventory[i].getItem() == ModItems.boltCartridge) return i;
        }
        return -1;
    }

    private static void fireBolt(ItemStack stack, NBTTagCompound tag, World world, EntityPlayer player, int ammo, long now, boolean creative)
    {
        float speed = ConfigHandler.CROSSBOW_BOLT_VELOCITY;
        EntityBolt bolt = new EntityBolt(world, player, speed);
        bolt.setDamage(CrossbowLogic.damageFactor(ConfigHandler.CROSSBOW_BOLT_DAMAGE, speed));
        bolt.canBePickedUp = creative ? 2 : (ConfigHandler.CROSSBOW_BOLTS_RECOVERABLE ? 1 : 0);
        world.spawnEntityInWorld(bolt);
        world.playSoundAtEntity(player, "adventurebackpack:crossbowshot", 0.5F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
        spend(stack, tag, player, ammo, 1, now, ConfigHandler.CROSSBOW_SHOT_DELAY_TICKS, creative);
    }

    /**
     * One sniper bolt: faster and harder than a normal one and with no spread (the arrow's random deviation is
     * replaced by the exact look direction). Slower rate: {@code crossbow.sniper.shotDelayTicks}.
     */
    private static void fireSniper(ItemStack stack, NBTTagCompound tag, World world, EntityPlayer player, int ammo, long now, boolean creative)
    {
        float speed = ConfigHandler.SNIPER_VELOCITY;
        EntityBolt bolt = new EntityBolt(world, player, speed);
        double[] look = new double[3];
        GrappleLogic.look(player.rotationYaw, player.rotationPitch, look);
        bolt.setThrowableHeading(look[0], look[1], look[2], speed, 0F);
        bolt.setDamage(CrossbowLogic.damageFactor(ConfigHandler.SNIPER_DAMAGE, speed));
        bolt.canBePickedUp = creative ? 2 : (ConfigHandler.CROSSBOW_BOLTS_RECOVERABLE ? 1 : 0);
        world.spawnEntityInWorld(bolt);
        world.playSoundAtEntity(player, "adventurebackpack:crossbowshot", 0.8F, 0.7F);
        spend(stack, tag, player, ammo, 1, now, ConfigHandler.SNIPER_SHOT_DELAY_TICKS, creative);
    }

    /** True while the player's bipod is down; the same rule as the client zoom, with the movement of the last tick. */
    public static boolean isBipodDeployed(EntityPlayer player)
    {
        return CrossbowLogic.bipodDeployed(player.isSneaking(), player.onGround, player.ridingEntity != null,
                player.posX - player.prevPosX, player.posZ - player.prevPosZ, ConfigHandler.SNIPER_STILL_TOLERANCE);
    }

    /** Fires the grappling hook; it costs {@code crossbow.grapple.chargesPerShot} bolts and starts the cooldown. */
    private static void fireGrapple(ItemStack stack, NBTTagCompound tag, World world, EntityPlayer player, int ammo, int cost, long now, boolean creative)
    {
        EntityGrapple hook = new EntityGrapple(world, player);
        world.spawnEntityInWorld(hook);
        tag.setInteger(TAG_GRAPPLE, hook.getEntityId());
        world.playSoundAtEntity(player, "adventurebackpack:crossbowshot", 0.5F, 0.6F);
        world.playSoundAtEntity(player, "random.bow", 0.4F, 0.5F);
        spend(stack, tag, player, ammo, cost, now, ConfigHandler.GRAPPLE_COOLDOWN_TICKS, creative);
    }

    /** Takes {@code cost} bolts from the magazine, starts the pause and wears the crossbow (one shot of wear). */
    private static void spend(ItemStack stack, NBTTagCompound tag, EntityPlayer player, int ammo, int cost, long now, int pause, boolean creative)
    {
        tag.setInteger(TAG_AMMO, ammo - cost);
        tag.setLong(TAG_LAST_SHOT, now);
        tag.setLong(TAG_READY_AT, now + pause);

        if (ConfigHandler.CROSSBOW_DURABILITY > 0 && !creative)
        {
            int wear = tag.getInteger(TAG_WEAR) + 1;
            tag.setInteger(TAG_WEAR, wear);
            if (CrossbowLogic.breaks(wear, ConfigHandler.CROSSBOW_DURABILITY))
            {
                player.renderBrokenItemStack(stack);
                stack.stackSize--;
            }
        }
    }

    /**
     * Loads the cartridge in {@code slot} (in creative without one, a full magazine). A partly used magazine goes
     * back to the player as a partly used cartridge.
     */
    private static void reload(NBTTagCompound tag, World world, EntityPlayer player, int slot, int ammo, int capacity, long now, boolean creative)
    {
        ItemStack cartridge = slot >= 0 ? player.inventory.mainInventory[slot] : null;
        int charges = cartridge != null ? ItemBoltCartridge.getCharges(cartridge, capacity) : capacity;

        if (cartridge != null && !creative)
        {
            player.inventory.decrStackSize(slot, 1);
        }
        if (ammo > 0 && !creative)
        {
            ItemStack ejected = ItemBoltCartridge.create(ammo, capacity);
            if (!player.inventory.addItemStackToInventory(ejected))
            {
                player.dropPlayerItemWithRandomChoice(ejected, false);
            }
        }

        tag.setInteger(TAG_AMMO, CrossbowLogic.clampAmmo(charges, capacity));
        tag.setLong(TAG_LAST_RELOAD, now);
        tag.setLong(TAG_READY_AT, now + ConfigHandler.CROSSBOW_RELOAD_TICKS);
        world.playSoundAtEntity(player, "random.click", 0.6F, 0.8F);
        world.playSoundAtEntity(player, "tile.piston.in", 0.3F, 1.6F);
    }

    private static void dryFire(NBTTagCompound tag, World world, EntityPlayer player, long now)
    {
        world.playSoundAtEntity(player, "random.click", 0.4F, 1.4F);
        if (CrossbowLogic.shouldWarnEmpty(now, tag.getLong(TAG_LAST_DRY), EMPTY_MESSAGE_QUIET_TICKS))
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.crossbow.outofammo"));
        }
        tag.setLong(TAG_LAST_DRY, now);
    }

    /**
     * Mode key (server side, from {@code CrossbowPacket}): moves the held crossbow to its next enabled mode. Leaving
     * the grappling mode lets go of a hook that is out.
     */
    public static void cycleMode(EntityPlayer player)
    {
        ItemStack stack = player.getHeldItem();
        if (!ConfigHandler.CROSSBOW_ENABLED || stack == null || stack.getItem() != ModItems.cwxbow) return;
        NBTTagCompound tag = tag(stack);
        int mode = CrossbowLogic.nextMode(tag.getInteger(TAG_MODE), ConfigHandler.GRAPPLE_ENABLED, ConfigHandler.SNIPER_ENABLED);
        if (mode == tag.getInteger(TAG_MODE)) return;
        if (mode != CrossbowLogic.MODE_GRAPPLE) releaseGrapple(player);
        tag.setInteger(TAG_MODE, mode);
        player.worldObj.playSoundAtEntity(player, "random.click", 0.5F, 1.2F);
    }

    /** Jump while hanging (server side, from {@code CrossbowPacket}): lets go of the held crossbow's hook. */
    public static void releaseGrapple(EntityPlayer player)
    {
        ItemStack stack = player.getHeldItem();
        if (stack == null || stack.getItem() != ModItems.cwxbow || !stack.hasTagCompound()) return;
        EntityGrapple hook = EntityGrapple.find(player.worldObj, stack.getTagCompound().getInteger(TAG_GRAPPLE), player);
        if (hook != null) hook.release();
    }

    /** The mode the crossbow fires in: the stored one, or repeating when it is unknown or switched off. */
    public static int getMode(ItemStack stack)
    {
        int stored = stack != null && stack.hasTagCompound() ? stack.getTagCompound().getInteger(TAG_MODE) : CrossbowLogic.MODE_REPEATING;
        return effectiveMode(stored);
    }

    /** A stored mode as the crossbow fires it: repeating when it is unknown or switched off. */
    public static int effectiveMode(int stored)
    {
        return CrossbowLogic.effectiveMode(stored, ConfigHandler.GRAPPLE_ENABLED, ConfigHandler.SNIPER_ENABLED);
    }

    /** Lang key of a mode's name. */
    public static String modeKey(int mode)
    {
        return MODE_KEYS[CrossbowLogic.effectiveMode(mode, true, true)];
    }

    private static final String[] MODE_KEYS = {"adventurebackpack:crossbow.mode.repeating", "adventurebackpack:crossbow.mode.grapple", "adventurebackpack:crossbow.mode.sniper"};
    private static final String[] CONTROLS_KEYS = {"adventurebackpack:tooltip.crossbow.controls", "adventurebackpack:tooltip.crossbow.controls.grapple", "adventurebackpack:tooltip.crossbow.controls.sniper"};

    /** "Clockwork Crossbow (Grappling Hook)": the mode shows in the name, also in the hotbar name popup. */
    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        int mode = getMode(stack);
        String name = super.getItemStackDisplayName(stack);
        if (mode == CrossbowLogic.MODE_REPEATING) return name;
        return StatCollector.translateToLocalFormatted("adventurebackpack:crossbow.nameWithMode", name, StatCollector.translateToLocal(modeKey(mode)));
    }

    /** For the renderer: true for a few ticks after a shot. */
    public static boolean isRecoiling(ItemStack stack, long worldTime)
    {
        return stack != null && stack.hasTagCompound() && CrossbowLogic.isRecoiling(worldTime, stack.getTagCompound().getLong(TAG_LAST_SHOT));
    }

    /** For the renderer: bolts in the magazine. */
    public static int getAmmo(ItemStack stack)
    {
        return stack != null && stack.hasTagCompound() ? CrossbowLogic.clampAmmo(stack.getTagCompound().getInteger(TAG_AMMO), ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE) : 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced)
    {
        int mode = getMode(stack);
        tooltip.add(StatCollector.translateToLocalFormatted("adventurebackpack:tooltip.crossbow.loaded", getAmmo(stack), ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE));
        tooltip.add(StatCollector.translateToLocalFormatted("adventurebackpack:tooltip.crossbow.mode", StatCollector.translateToLocal(modeKey(mode)), GameSettings.getKeyDisplayString(Keybindings.crossbowMode.getKeyCode())));
        tooltip.add(StatCollector.translateToLocal(CONTROLS_KEYS[mode]));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return ConfigHandler.CROSSBOW_DURABILITY > 0 && stack.hasTagCompound() && stack.getTagCompound().getInteger(TAG_WEAR) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        int durability = ConfigHandler.CROSSBOW_DURABILITY;
        return durability > 0 && stack.hasTagCompound() ? Math.min(1.0, (double) stack.getTagCompound().getInteger(TAG_WEAR) / durability) : 0.0;
    }
}
