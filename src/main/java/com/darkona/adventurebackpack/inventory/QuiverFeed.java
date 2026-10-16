package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.AbilityToggle;
import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.common.QuiverFeedRules;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemBoltCartridge;
import com.darkona.adventurebackpack.item.ItemCrossbow;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class QuiverFeed
{
    private final int[] ids = new int[ConfigHandler.QUIVER_MAX_SLOTS];
    private final int[] left = new int[ConfigHandler.QUIVER_MAX_SLOTS];
    private final int[] spent = new int[ConfigHandler.QUIVER_MAX_SLOTS];
    private final int[] rank = new int[ConfigHandler.QUIVER_MAX_SLOTS];
    private int slots;
    private ItemStack backpack;

    private boolean bursting;
    private ItemStack crossbow;
    private int burstMode;
    private long heldUntil;
    private long nextShotAt;
    private int sinceFlush;

    public static QuiverFeed of(EntityPlayer player)
    {
        if (!ConfigHandler.CROSSBOW_QUIVER_ENABLED || player == null || player.worldObj.isRemote) return null;
        BackpackProperty props = BackpackProperty.get(player);
        if (props == null) return null;
        QuiverFeed feed = props.getQuiverFeed();
        ItemStack worn = props.getWearable();
        if (worn == null || !(worn.getItem() instanceof ItemAdventureBackpack) || !AbilityToggle.isActive(player))
        {
            if (feed != null) feed.finish(player);
            return null;
        }
        if (feed == null) feed = props.createQuiverFeed();
        if (!(feed.bursting && feed.backpack == worn))
        {
            feed.finish(player);
            feed.load(worn);
        }
        return feed.usable() > 0 ? feed : null;
    }

    public static void flush(EntityPlayer player)
    {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) return;
        BackpackProperty props = BackpackProperty.get(player);
        QuiverFeed feed = props == null ? null : props.getQuiverFeed();
        if (feed != null) feed.finish(player);
    }

    public static void tick(EntityPlayer player, BackpackProperty props)
    {
        QuiverFeed feed = props.getQuiverFeed();
        if (feed == null || !feed.bursting) return;
        feed.tickBurst(player, props);
    }

    private void load(ItemStack worn)
    {
        backpack = worn;
        NBTTagCompound data = worn.hasTagCompound() ? worn.getTagCompound().getCompoundTag("backpackData") : null;
        slots = data == null ? 0 : Math.min(BackpackLayouts.quiver(data.getString("colorName")), ConfigHandler.QUIVER_MAX_SLOTS);
        BackpackNbt.readQuiverCounts(data, slots, ids, left);
        for (int q = 0; q < slots; q++)
        {
            spent[q] = 0;
            rank[q] = left[q] > 0 ? ItemBoltCartridge.ammoRank(Item.getItemById(ids[q])) : QuiverFeedRules.NOT_AMMO;
        }
        sinceFlush = 0;
    }

    public int usable()
    {
        return QuiverFeedRules.usable(rank, left, slots);
    }

    public String take(int count, boolean creative)
    {
        if (creative)
        {
            int q = QuiverFeedRules.pick(rank, left, slots);
            return q < 0 ? null : ItemBoltCartridge.ammoNameAt(rank[q]);
        }
        int q = QuiverFeedRules.take(rank, left, spent, slots, count);
        if (q < 0) return null;
        sinceFlush += count;
        return ItemBoltCartridge.ammoNameAt(rank[q]);
    }

    public void pull(ItemStack held, int mode, long now, long firstShotAt)
    {
        heldUntil = now + ConfigHandler.QUIVER_TRIGGER_HOLD_TICKS;
        if (bursting && crossbow == held) return;
        bursting = true;
        crossbow = held;
        burstMode = mode;
        nextShotAt = firstShotAt;
    }

    public boolean isBursting()
    {
        return bursting;
    }

    private void tickBurst(EntityPlayer player, BackpackProperty props)
    {
        long now = player.worldObj.getTotalWorldTime();
        if (!ConfigHandler.CROSSBOW_ENABLED || !ConfigHandler.CROSSBOW_QUIVER_ENABLED
                || !QuiverFeedRules.triggerHeld(now, heldUntil)
                || player.getHeldItem() != crossbow || props.getWearable() != backpack
                || player.openContainer != player.inventoryContainer || !AbilityToggle.isActive(player)
                || (burstMode == CrossbowLogic.MODE_SNIPER && ItemCrossbow.isBipodDeployed(player)))
        {
            finish(player);
            return;
        }
        if (!QuiverFeedRules.shotDue(now, nextShotAt)) return;
        String ammo = take(1, player.capabilities.isCreativeMode);
        if (ammo == null)
        {
            finish(player);
            return;
        }
        ItemCrossbow.fireFromQuiver(crossbow, player, ammo, now);
        nextShotAt = now + ConfigHandler.QUIVER_FIRE_INTERVAL_TICKS;
        if (crossbow.stackSize <= 0 || usable() <= 0)
        {
            finish(player);
        } else if (QuiverFeedRules.flushDue(sinceFlush, ConfigHandler.QUIVER_FLUSH_EVERY_SHOTS))
        {
            writeBack();
        }
    }

    public void finish(EntityPlayer player)
    {
        bursting = false;
        crossbow = null;
        writeBack();
    }

    public void writeBack()
    {
        if (sinceFlush <= 0 || backpack == null)
        {
            sinceFlush = 0;
            return;
        }
        NBTTagCompound data = backpack.hasTagCompound() ? backpack.getTagCompound().getCompoundTag("backpackData") : null;
        BackpackNbt.takeFromQuiver(data, slots, ids, spent);
        for (int q = 0; q < slots; q++) spent[q] = 0;
        sinceFlush = 0;
    }
}
