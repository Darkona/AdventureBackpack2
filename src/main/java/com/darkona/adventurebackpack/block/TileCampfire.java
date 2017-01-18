package com.darkona.adventurebackpack.block;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * Created on 08/01/2015
 *
 * @author Darkona
 */
public class TileCampfire extends TileEntity
{
    public TileCampfire()
    {
    }

    @SuppressWarnings("unused")
    private int burnTicks;
    @SuppressWarnings("unused")
    private ItemStack[] foodCooking = new ItemStack[4];
}
