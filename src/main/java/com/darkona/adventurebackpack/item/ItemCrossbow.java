package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.common.GrappleLogic;
import com.darkona.adventurebackpack.common.MagazineLogic;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.client.TooltipLines;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.entity.EntityBolt;
import com.darkona.adventurebackpack.entity.EntityGrapple;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.inventory.QuiverFeed;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
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

public class ItemCrossbow extends ItemAB
{
    public static final String TAG_AMMO = "Ammo";
    static final String LEGACY_TAG_AMMO_TYPE = "AmmoType";
    public static final String TAG_MAGAZINE = "Magazine";
    public static final String TAG_AMMO_ITEM = "AmmoItem";
    public static final String TAG_READY_AT = "ReadyAt";
    public static final String TAG_LAST_SHOT = "LastShot";
    public static final String TAG_LAST_RELOAD = "LastReload";
    public static final String TAG_RELOAD_SWAP = "ReloadSwap";
    public static final String TAG_LAST_DRY = "LastDry";
    public static final String TAG_WEAR = "Wear";
    public static final String TAG_MODE = "Mode";
    public static final String TAG_GRAPPLE = "Grapple";
    public static final String TAG_FED = "Fed";
    public static final String TAG_QUIVER_SHOTS = "QuiverShots";

    private static final int EMPTY_MESSAGE_QUIET_TICKS = 20;
    private static final double[] LOOK = new double[3];

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
            if (EntityGrapple.find(world, tag.getInteger(TAG_GRAPPLE), player) != null) return stack;
        }
        int capacity = ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE;
        int ammo = CrossbowLogic.clampAmmo(tag.getInteger(TAG_AMMO), capacity);
        int cost = CrossbowLogic.shotCost(mode == CrossbowLogic.MODE_GRAPPLE ? ConfigHandler.GRAPPLE_CHARGES : 1, capacity);
        long now = world.getTotalWorldTime();
        boolean deployed = mode == CrossbowLogic.MODE_SNIPER && isBipodDeployed(player);
        boolean sneaking = player.isSneaking() && !deployed;
        boolean creative = player.capabilities.isCreativeMode;
        if (!sneaking && clickFromQuiver(stack, tag, world, player, mode, deployed, now, creative)) return stack;
        int slot = sneaking || ammo < cost ? ItemBoltCartridge.findLoaded(player) : -1;
        boolean magazine = MagazineLogic.hasMagazine(tag.hasKey(TAG_MAGAZINE), tag.getBoolean(TAG_MAGAZINE), ammo);

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
                reload(tag, world, player, slot, ammo, magazine, capacity, now, creative);
                break;
            case DRY:
                dryFire(tag, world, player, ammo, magazine, now);
                break;
            default:
                break;
        }
        return stack;
    }

    private static int maxWait()
    {
        int pause = Math.max(ConfigHandler.CROSSBOW_SHOT_DELAY_TICKS, ConfigHandler.CROSSBOW_RELOAD_TICKS);
        pause = Math.max(pause, ConfigHandler.QUIVER_FIRE_INTERVAL_TICKS);
        return Math.max(pause, Math.max(ConfigHandler.GRAPPLE_COOLDOWN_TICKS, ConfigHandler.SNIPER_FIRE_INTERVAL_TICKS));
    }

    private static NBTTagCompound tag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    private static void fireBolt(ItemStack stack, NBTTagCompound tag, World world, EntityPlayer player, int ammo, long now, boolean creative)
    {
        spawnBolt(world, player, ammoMultiplier(tag), creative);
        spend(stack, tag, player, ammo, 1, now, ConfigHandler.CROSSBOW_SHOT_DELAY_TICKS, creative);
    }

    private static void spawnBolt(World world, EntityPlayer player, double multiplier, boolean creative)
    {
        float speed = ConfigHandler.CROSSBOW_BOLT_VELOCITY;
        EntityBolt bolt = new EntityBolt(world, player, speed);
        bolt.setDamage(CrossbowLogic.damageFactor(ConfigHandler.CROSSBOW_BOLT_DAMAGE * multiplier, speed));
        bolt.canBePickedUp = creative ? 2 : (ConfigHandler.CROSSBOW_BOLTS_RECOVERABLE ? 1 : 0);
        world.spawnEntityInWorld(bolt);
        world.playSoundAtEntity(player, "adventurebackpack:crossbowshot", 0.5F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
    }

    private static boolean clickFromQuiver(ItemStack stack, NBTTagCompound tag, World world, EntityPlayer player, int mode, boolean deployed, long now, boolean creative)
    {
        QuiverFeed feed = QuiverFeed.of(player);
        if (feed == null) return false;
        long readyAt = tag.getLong(TAG_READY_AT);
        boolean waiting = CrossbowLogic.isWaiting(now, readyAt, maxWait());
        if (mode == CrossbowLogic.MODE_GRAPPLE)
        {
            int cost = CrossbowLogic.shotCost(ConfigHandler.GRAPPLE_CHARGES, ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE);
            if (cost <= 0 || feed.usable() < cost) return false;
            if (waiting) return true;
            if (feed.take(cost, creative) == null) return false;
            launchGrapple(tag, world, player);
            spendFeed(stack, tag, player, now, ConfigHandler.GRAPPLE_COOLDOWN_TICKS, creative);
            feed.writeBack();
            return true;
        }
        if (deployed)
        {
            if (waiting) return true;
            String ammo = feed.take(1, creative);
            if (ammo == null) return false;
            spawnSniper(world, player, MagazineLogic.damageMultiplier(ammo, ConfigHandler.CROSSBOW_BOLT_DAMAGE_MULTIPLIER), creative);
            spendFeed(stack, tag, player, now, ConfigHandler.SNIPER_FIRE_INTERVAL_TICKS, creative);
            feed.writeBack();
            return true;
        }
        feed.pull(stack, mode, now, waiting ? readyAt : now);
        QuiverFeed.tick(player, BackpackProperty.get(player));
        return true;
    }

    public static void fireFromQuiver(ItemStack stack, EntityPlayer player, String ammo, long now, int pause)
    {
        boolean creative = player.capabilities.isCreativeMode;
        spawnBolt(player.worldObj, player, MagazineLogic.damageMultiplier(ammo, ConfigHandler.CROSSBOW_BOLT_DAMAGE_MULTIPLIER), creative);
        spendFeed(stack, tag(stack), player, now, pause, creative);
    }

    private static void spendFeed(ItemStack stack, NBTTagCompound tag, EntityPlayer player, long now, int pause, boolean creative)
    {
        tag.setLong(TAG_LAST_SHOT, now);
        tag.setLong(TAG_READY_AT, now + pause);
        tag.setInteger(TAG_QUIVER_SHOTS, tag.getInteger(TAG_QUIVER_SHOTS) + 1);
        if (!tag.getBoolean(TAG_FED)) tag.setBoolean(TAG_FED, true);
        int ammo = CrossbowLogic.clampAmmo(tag.getInteger(TAG_AMMO), ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE);
        wear(stack, tag, player, ammo, ammo, creative);
    }

    private static void unfeed(NBTTagCompound tag)
    {
        if (tag.hasKey(TAG_FED)) tag.removeTag(TAG_FED);
    }

    private static void fireSniper(ItemStack stack, NBTTagCompound tag, World world, EntityPlayer player, int ammo, long now, boolean creative)
    {
        spawnSniper(world, player, ammoMultiplier(tag), creative);
        spend(stack, tag, player, ammo, 1, now, ConfigHandler.SNIPER_FIRE_INTERVAL_TICKS, creative);
    }

    private static void spawnSniper(World world, EntityPlayer player, double multiplier, boolean creative)
    {
        float speed = (float) (ConfigHandler.CROSSBOW_BOLT_VELOCITY * ConfigHandler.SNIPER_SPEED_MULTIPLIER);
        EntityBolt bolt = new EntityBolt(world, player, speed);
        GrappleLogic.look(player.rotationYaw, player.rotationPitch, LOOK);
        bolt.setThrowableHeading(LOOK[0], LOOK[1], LOOK[2], speed, 0F);
        bolt.setFlight(ConfigHandler.SNIPER_DRAG, ConfigHandler.SNIPER_GRAVITY, ConfigHandler.SNIPER_PIERCING);
        bolt.setDamage(CrossbowLogic.damageFactor(ConfigHandler.CROSSBOW_BOLT_DAMAGE * ConfigHandler.SNIPER_DAMAGE_MULTIPLIER * multiplier, speed));
        bolt.canBePickedUp = creative ? 2 : (ConfigHandler.CROSSBOW_BOLTS_RECOVERABLE ? 1 : 0);
        world.spawnEntityInWorld(bolt);
        world.playSoundAtEntity(player, "adventurebackpack:crossbowshot", 0.8F, 0.7F);
    }

    private static double ammoMultiplier(NBTTagCompound tag)
    {
        return MagazineLogic.damageMultiplier(tag.getString(TAG_AMMO_ITEM), ConfigHandler.CROSSBOW_BOLT_DAMAGE_MULTIPLIER);
    }

    public static boolean isBipodDeployed(EntityPlayer player)
    {
        return CrossbowLogic.bipodDeployed(player.isSneaking(), player.onGround, player.ridingEntity != null,
                player.posX - player.prevPosX, player.posZ - player.prevPosZ, ConfigHandler.SNIPER_STILL_TOLERANCE);
    }

    private static void fireGrapple(ItemStack stack, NBTTagCompound tag, World world, EntityPlayer player, int ammo, int cost, long now, boolean creative)
    {
        launchGrapple(tag, world, player);
        spend(stack, tag, player, ammo, cost, now, ConfigHandler.GRAPPLE_COOLDOWN_TICKS, creative);
    }

    private static void launchGrapple(NBTTagCompound tag, World world, EntityPlayer player)
    {
        EntityGrapple hook = new EntityGrapple(world, player);
        world.spawnEntityInWorld(hook);
        tag.setInteger(TAG_GRAPPLE, hook.getEntityId());
        world.playSoundAtEntity(player, "adventurebackpack:crossbowshot", 0.5F, 0.6F);
        world.playSoundAtEntity(player, "random.bow", 0.4F, 0.5F);
    }

    private static void spend(ItemStack stack, NBTTagCompound tag, EntityPlayer player, int ammo, int cost, long now, int pause, boolean creative)
    {
        tag.setInteger(TAG_AMMO, ammo - cost);
        tag.setLong(TAG_LAST_SHOT, now);
        tag.setLong(TAG_READY_AT, now + pause);
        unfeed(tag);
        wear(stack, tag, player, ammo, ammo - cost, creative);
    }

    private static void wear(ItemStack stack, NBTTagCompound tag, EntityPlayer player, int before, int charges, boolean creative)
    {
        if (ConfigHandler.CROSSBOW_DURABILITY > 0 && !creative)
        {
            int wear = tag.getInteger(TAG_WEAR) + 1;
            tag.setInteger(TAG_WEAR, wear);
            if (CrossbowLogic.breaks(wear, ConfigHandler.CROSSBOW_DURABILITY))
            {
                player.renderBrokenItemStack(stack);
                stack.stackSize--;
                if (MagazineLogic.hasMagazine(tag.hasKey(TAG_MAGAZINE), tag.getBoolean(TAG_MAGAZINE), before))
                {
                    ItemBoltCartridge.giveBack(player, ItemBoltCartridge.create(charges, tag.getString(TAG_AMMO_ITEM)));
                }
            }
        }
    }

    private static void reload(NBTTagCompound tag, World world, EntityPlayer player, int slot, int ammo, boolean magazine, int capacity, long now, boolean creative)
    {
        ItemStack loaded = slot >= 0 ? player.inventory.mainInventory[slot] : null;
        int charges;
        String type;
        if (creative)
        {
            charges = capacity;
            type = loaded != null ? ItemBoltCartridge.getAmmo(loaded) : MagazineLogic.ammoType(tag.getString(TAG_AMMO_ITEM));
        } else
        {
            charges = ItemBoltCartridge.getCharges(loaded, capacity);
            type = ItemBoltCartridge.getAmmo(loaded);
            player.inventory.decrStackSize(slot, 1);
            if (magazine) ItemBoltCartridge.giveBack(player, ItemBoltCartridge.create(ammo, tag.getString(TAG_AMMO_ITEM)));
            tag.setBoolean(TAG_MAGAZINE, true);
        }

        tag.setInteger(TAG_AMMO, CrossbowLogic.clampAmmo(charges, capacity));
        tag.setString(TAG_AMMO_ITEM, type);
        unfeed(tag);
        tag.setLong(TAG_LAST_RELOAD, now);
        tag.setBoolean(TAG_RELOAD_SWAP, magazine);
        tag.setLong(TAG_READY_AT, now + ConfigHandler.CROSSBOW_RELOAD_TICKS);
        world.playSoundAtEntity(player, "random.click", 0.6F, 0.8F);
        world.playSoundAtEntity(player, "tile.piston.in", 0.3F, 1.6F);
    }

    private static void dryFire(NBTTagCompound tag, World world, EntityPlayer player, int ammo, boolean magazine, long now)
    {
        world.playSoundAtEntity(player, "random.click", 0.4F, 1.4F);
        if (MagazineLogic.ejectOnDry(ammo, magazine) && !player.capabilities.isCreativeMode)
        {
            ItemBoltCartridge.giveBack(player, ItemBoltCartridge.create(0, null));
            tag.setBoolean(TAG_MAGAZINE, false);
            tag.setInteger(TAG_AMMO, 0);
            tag.removeTag(TAG_AMMO_ITEM);
        }
        if (CrossbowLogic.shouldWarnEmpty(now, tag.getLong(TAG_LAST_DRY), EMPTY_MESSAGE_QUIET_TICKS))
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.crossbow.outofammo"));
        }
        tag.setLong(TAG_LAST_DRY, now);
    }

    public static void cycleMode(EntityPlayer player)
    {
        ItemStack stack = player.getHeldItem();
        if (!ConfigHandler.CROSSBOW_ENABLED || stack == null || stack.getItem() != ModItems.cwxbow) return;
        NBTTagCompound tag = tag(stack);
        int mode = CrossbowLogic.nextMode(tag.getInteger(TAG_MODE), ConfigHandler.GRAPPLE_ENABLED, ConfigHandler.SNIPER_ENABLED);
        if (mode == tag.getInteger(TAG_MODE)) return;
        QuiverFeed.flush(player);
        if (mode != CrossbowLogic.MODE_GRAPPLE) releaseGrapple(player);
        tag.setInteger(TAG_MODE, mode);
        player.worldObj.playSoundAtEntity(player, "random.click", 0.5F, 1.2F);
    }

    public static void releaseGrapple(EntityPlayer player)
    {
        ItemStack stack = player.getHeldItem();
        if (stack == null || stack.getItem() != ModItems.cwxbow || !stack.hasTagCompound()) return;
        EntityGrapple hook = EntityGrapple.find(player.worldObj, stack.getTagCompound().getInteger(TAG_GRAPPLE), player);
        if (hook != null) hook.release();
    }

    public static int getMode(ItemStack stack)
    {
        int stored = stack != null && stack.hasTagCompound() ? stack.getTagCompound().getInteger(TAG_MODE) : CrossbowLogic.MODE_REPEATING;
        return effectiveMode(stored);
    }

    public static int effectiveMode(int stored)
    {
        return CrossbowLogic.effectiveMode(stored, ConfigHandler.GRAPPLE_ENABLED, ConfigHandler.SNIPER_ENABLED);
    }

    public static String modeKey(int mode)
    {
        return MODE_KEYS[CrossbowLogic.effectiveMode(mode, true, true)];
    }

    private static final String[] MODE_KEYS = {"adventurebackpack:crossbow.mode.repeating", "adventurebackpack:crossbow.mode.grapple", "adventurebackpack:crossbow.mode.sniper"};
    private static final String[] CONTROLS_KEYS = {"adventurebackpack:tooltip.crossbow.controls", "adventurebackpack:tooltip.crossbow.controls.grapple", "adventurebackpack:tooltip.crossbow.controls.sniper"};

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        int mode = getMode(stack);
        String name = super.getItemStackDisplayName(stack);
        if (mode == CrossbowLogic.MODE_REPEATING) return name;
        return StatCollector.translateToLocalFormatted("adventurebackpack:crossbow.nameWithMode", name, StatCollector.translateToLocal(modeKey(mode)));
    }

    public static boolean isRecoiling(ItemStack stack, long worldTime)
    {
        return stack != null && stack.hasTagCompound() && CrossbowLogic.isRecoiling(worldTime, stack.getTagCompound().getLong(TAG_LAST_SHOT));
    }

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
        tooltip.add(StatCollector.translateToLocalFormatted("adventurebackpack:tooltip.crossbow.mode", StatCollector.translateToLocal(modeKey(mode)), Keybindings.keyName(Keybindings.state)));
        TooltipLines.details(tooltip, CONTROLS_KEYS[mode]);
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
