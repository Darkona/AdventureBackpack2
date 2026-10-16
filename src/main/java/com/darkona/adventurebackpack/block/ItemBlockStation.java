package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.common.TankTagMemo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class ItemBlockStation extends ItemBlock
{
    private static final TankTagMemo TANK_TIP = new TankTagMemo();

    public ItemBlockStation(Block block)
    {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced)
    {
        if (stack == null || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("Tank")) return;
        FluidStack fluid = TANK_TIP.fluid(stack.getTagCompound(), "Tank");
        if (fluid == null || fluid.amount <= 0) return;
        tooltip.add(StatCollector.translateToLocalFormatted("adventurebackpack:tooltip.station.tank", fluid.amount, fluid.getLocalizedName()));
    }
}
