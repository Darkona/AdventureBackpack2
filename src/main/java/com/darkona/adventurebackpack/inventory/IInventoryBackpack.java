package com.darkona.adventurebackpack.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IInventoryBackpack
{
    NBTTagCompound getExtendedProperties();

    void dirtyExtended();

    ItemStack getParentItem();
}
