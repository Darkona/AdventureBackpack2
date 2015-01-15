package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class ContainerJetpack extends Container implements IWearableContainer
{
    InventorySteamJetpack inv;
    EntityPlayer player;
    private final int
    PLAYER_HOT_START = 0,
    PLAYER_HOT_END = PLAYER_HOT_START + 8,
    PLAYER_INV_START = PLAYER_HOT_END + 1,
    PLAYER_INV_END = PLAYER_INV_START + 26,
    JETPACK_INV_START = PLAYER_INV_END + 1;

    public ContainerJetpack(EntityPlayer player, InventorySteamJetpack inv)
    {
        this.player = player;
        this.inv = inv;
        makeSlots(player.inventory);
        inv.openInventory();
    }

    private void bindPlayerInventory(InventoryPlayer invPlayer)
    {
        int startX = 8;
        int startY = 84;

        // Player's Hotbar
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(invPlayer, x, startX + 18 * x, 142));
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
        addSlotToContainer(new SlotFluid(inv, slot++, 29, 21));
        // bucket out
        addSlotToContainer(new SlotFluid(inv, slot++, 29, 51));

        addSlotToContainer(new Slot(inv,slot++, 76, 63));

    }
    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }



}
