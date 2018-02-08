package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Created on 04.02.2018
 *
 * @author Ugachaga
 */
public class SlotCraftMatrix extends SlotAdventureBackpack
{
    SlotCraftMatrix(IInventory craftMatrix, int slotIndex, int posX, int posY)
    {
        super(craftMatrix, slotIndex, posX, posY);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player)
    {
        return false;
    }
}