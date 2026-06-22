package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityBolt;
import com.darkona.adventurebackpack.init.ModItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
    /** int: type of the loaded bolts, one of the {@link CrossbowLogic} types. */
    public static final String TAG_AMMO_TYPE = "AmmoType";
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
        int capacity = ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE;
        int ammo = CrossbowLogic.clampAmmo(tag.getInteger(TAG_AMMO), capacity);
        long now = world.getTotalWorldTime();
        boolean sneaking = player.isSneaking();
        boolean creative = player.capabilities.isCreativeMode;
        int slot = sneaking || ammo == 0 ? findCartridge(player) : -1;
        int maxWait = Math.max(ConfigHandler.CROSSBOW_SHOT_DELAY_TICKS, ConfigHandler.CROSSBOW_RELOAD_TICKS);

        switch (CrossbowLogic.decide(ammo, capacity, now, tag.getLong(TAG_READY_AT), maxWait, sneaking, slot >= 0 || creative))
        {
            case FIRE:
                fire(stack, tag, world, player, ammo, now, creative);
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

    private static void fire(ItemStack stack, NBTTagCompound tag, World world, EntityPlayer player, int ammo, long now, boolean creative)
    {
        float speed = ConfigHandler.CROSSBOW_BOLT_VELOCITY;
        EntityBolt bolt = new EntityBolt(world, player, speed, tag.getInteger(TAG_AMMO_TYPE));
        bolt.setDamage(CrossbowLogic.damageFactor(ConfigHandler.CROSSBOW_BOLT_DAMAGE, speed));
        bolt.canBePickedUp = creative ? 2 : (ConfigHandler.CROSSBOW_BOLTS_RECOVERABLE ? 1 : 0);
        world.spawnEntityInWorld(bolt);
        world.playSoundAtEntity(player, "adventurebackpack:crossbowshot", 0.5F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);

        tag.setInteger(TAG_AMMO, ammo - 1);
        tag.setLong(TAG_LAST_SHOT, now);
        tag.setLong(TAG_READY_AT, now + ConfigHandler.CROSSBOW_SHOT_DELAY_TICKS);

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
     * Loads the cartridge in {@code slot} (in creative without one, a full normal magazine). A partly used magazine
     * goes back to the player as a partly used cartridge of the same type.
     */
    private static void reload(NBTTagCompound tag, World world, EntityPlayer player, int slot, int ammo, int capacity, long now, boolean creative)
    {
        ItemStack cartridge = slot >= 0 ? player.inventory.mainInventory[slot] : null;
        int type = cartridge != null ? ItemBoltCartridge.getType(cartridge) : CrossbowLogic.NORMAL;
        int charges = cartridge != null ? ItemBoltCartridge.getCharges(cartridge, capacity) : capacity;
        int oldType = CrossbowLogic.clampType(tag.getInteger(TAG_AMMO_TYPE));

        if (cartridge != null && !creative)
        {
            player.inventory.decrStackSize(slot, 1);
        }
        if (ammo > 0 && !creative)
        {
            ItemStack ejected = ItemBoltCartridge.create(oldType, ammo, capacity);
            if (!player.inventory.addItemStackToInventory(ejected))
            {
                player.dropPlayerItemWithRandomChoice(ejected, false);
            }
        }

        tag.setInteger(TAG_AMMO, CrossbowLogic.clampAmmo(charges, capacity));
        tag.setInteger(TAG_AMMO_TYPE, type);
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
        int ammo = getAmmo(stack);
        tooltip.add(StatCollector.translateToLocalFormatted("adventurebackpack:tooltip.crossbow.loaded", ammo, ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE));
        if (ammo > 0)
        {
            tooltip.add(ItemBoltCartridge.typeName(stack.getTagCompound().getInteger(TAG_AMMO_TYPE)));
        }
        tooltip.add(StatCollector.translateToLocal("adventurebackpack:tooltip.crossbow.controls"));
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
