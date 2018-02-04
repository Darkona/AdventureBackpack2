package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

/**
 * Created on 04.02.2018
 *
 * @author Ugachaga
 */
public class SlotCraftResult extends SlotCrafting
{
    private ContainerBackpack eventHandler;

    public SlotCraftResult(ContainerBackpack container, EntityPlayer player, IInventory craftMatrix, IInventory inventory, int slotIndex, int posX, int posY)
    {
        super(player, craftMatrix, inventory, slotIndex, posX, posY);
        this.eventHandler = container;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack)
    {
        eventHandler.syncCraftMatrixToInventory(); // pre craft sync
        super.onPickupFromSlot(player, stack);
        eventHandler.syncInventoryToCraftMatrix(); // post craft sync
    }
}
