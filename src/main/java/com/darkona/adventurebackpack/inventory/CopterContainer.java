package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.Constants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

/**
 * Created on 03/01/2015
 *
 * @author Darkona
 */
public class CopterContainer extends Container
{

    public InventoryCopterPack inventory;
    private final int
            PLAYER_HOT_START = 0,
            PLAYER_HOT_END = PLAYER_HOT_START + 8,
            PLAYER_INV_START = PLAYER_HOT_END + 1,
            PLAYER_INV_END = PLAYER_INV_START + 26,
            COPTER_INV_START = PLAYER_INV_END + 1;

    public CopterContainer(EntityPlayer player,InventoryCopterPack copterPack)
    {
        this.inventory = copterPack;
        makeSlots(player.inventory);
    }

    private void bindPlayerInventory(InventoryPlayer invPlayer)
    {
        int startX = 44;
        int startY = 125;

        // Player's Hotbar
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(invPlayer, x, startX + 18 * x, 183));
        }

        // Player's Inventory
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlotToContainer(new Slot(invPlayer, (x + y * 9 + 9), (startX + 18 * x), (startY + y * 18)));
            }
        }
        //Total 36 slots
    }

    private void makeSlots(InventoryPlayer invPlayer)
    {

        bindPlayerInventory(invPlayer);
        int slot = 0;
        //Bucket Slots
        // bucket in
        addSlotToContainer(new SlotFluid(inventory, slot++, 99, 22));
        // bucket out
        addSlotToContainer(new SlotFluid(inventory, slot++, 99, 52));
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     *
     * @param p_82846_1_
     * @param p_82846_2_
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
    {
        return super.transferStackInSlot(p_82846_1_, p_82846_2_);
    }

    @Override
    public ItemStack slotClick(int p_75144_1_, int p_75144_2_, int p_75144_3_, EntityPlayer p_75144_4_)
    {
        return super.slotClick(p_75144_1_, p_75144_2_, p_75144_3_, p_75144_4_);
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }
}
