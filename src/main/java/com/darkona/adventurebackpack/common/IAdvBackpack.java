package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Darkona on 12/10/2014.
 */
public interface IAdvBackpack extends IInventory {

    public FluidTank getLeftTank();

    public FluidTank getRightTank();

    public void setLeftTank(FluidTank tank);

    public void setRightTank(FluidTank tank);

    void onInventoryChanged();

    public NBTTagCompound writeToNBT();

    public void updateTankSlots(FluidTank tank, int slotIN);

    public TileAdventureBackpack getTile();

    public ItemStack getInventoryItem();

    public String getColor();

    public String getColorName();

    public ItemStack[] getInventory();

    public boolean isSpecial();

    public void setInventorySlotContentsNoSave(int slot, ItemStack stack);

    public ItemStack decrStackSizeNoSave(int slot, int amount);

    public boolean hasItem(Item item);

    void consumeInventoryItem(Item item);
}
