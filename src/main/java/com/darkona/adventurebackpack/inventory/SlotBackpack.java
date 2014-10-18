package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Darkona on 12/10/2014.
 */
public class SlotBackpack extends Slot
{
    public SlotBackpack(IInventory inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return (!(stack.getItem() instanceof ItemAdventureBackpack) && !(stack.getItem() == Item.getItemFromBlock(ModBlocks.blockBackpack)));
    }

    @Override
    public void onPickupFromSlot(EntityPlayer p_82870_1_, ItemStack p_82870_2_)
    {
        super.onPickupFromSlot(p_82870_1_, p_82870_2_);
    }
}
