package com.darkona.adventurebackpack.inventory.widget;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public interface SlotHost
{
    IInventory hostInventory();

    EntityPlayer getPlayer();
}
