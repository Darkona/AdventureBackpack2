package com.darkona.adventurebackpack.inventory;

import adventurebackpack.api.IInventoryTanks;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.item.ItemFlamethrowerPack;
import com.darkona.adventurebackpack.item.ItemScubaGear;
import com.darkona.adventurebackpack.item.ItemSteamJetpack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public enum GearType
{
    JETPACK("jetpackData", new String[]{"status", "inUse", "boiling", "leaking"}, null),
    COPTER(null, new String[0], "status"),
    SCUBA("scubaData", new String[0], null),
    FLAMETHROWER("flamethrowerData", new String[]{"on"}, null);

    private static final GearType[] VALUES = values();

    public final String dataKey;
    public final String[] runningFlags;
    public final String runningByte;

    GearType(String dataKey, String[] runningFlags, String runningByte)
    {
        this.dataKey = dataKey;
        this.runningFlags = runningFlags;
        this.runningByte = runningByte;
    }

    public static GearType byOrdinal(int ordinal)
    {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }

    public static GearType of(Item item)
    {
        if (item instanceof ItemSteamJetpack) return JETPACK;
        if (item instanceof ItemCopterPack) return COPTER;
        if (item instanceof ItemScubaGear) return SCUBA;
        if (item instanceof ItemFlamethrowerPack) return FLAMETHROWER;
        return null;
    }

    public static GearType of(ItemStack stack)
    {
        return stack == null ? null : of(stack.getItem());
    }

    public boolean placeable()
    {
        return ConfigHandler.PLACEDGEAR_ENABLED && placeableFlag();
    }

    private boolean placeableFlag()
    {
        switch (this)
        {
            case JETPACK:
                return ConfigHandler.PLACEDGEAR_JETPACK;
            case COPTER:
                return ConfigHandler.PLACEDGEAR_COPTER;
            case SCUBA:
                return ConfigHandler.PLACEDGEAR_SCUBA;
            default:
                return ConfigHandler.PLACEDGEAR_FLAMETHROWER;
        }
    }

    public boolean stopRunning(ItemStack stack)
    {
        NBTTagCompound root = stack == null ? null : stack.stackTagCompound;
        if (root == null) return false;
        NBTTagCompound data = root;
        if (dataKey != null)
        {
            if (!root.hasKey(dataKey, Constants.NBT.TAG_COMPOUND)) return false;
            data = root.getCompoundTag(dataKey);
        }
        boolean changed = false;
        for (String flag : runningFlags)
        {
            if (data.getBoolean(flag))
            {
                data.setBoolean(flag, false);
                changed = true;
            }
        }
        if (runningByte != null && data.getByte(runningByte) != 0)
        {
            data.setByte(runningByte, (byte) 0);
            changed = true;
        }
        return changed;
    }

    public IInventoryTanks newInventory(ItemStack stack)
    {
        switch (this)
        {
            case JETPACK:
                return new InventorySteamJetpack(stack);
            case COPTER:
                return new InventoryCopterPack(stack);
            case SCUBA:
                return new InventoryScubaGear(stack);
            default:
                return new InventoryFlamethrowerPack(stack);
        }
    }
}
