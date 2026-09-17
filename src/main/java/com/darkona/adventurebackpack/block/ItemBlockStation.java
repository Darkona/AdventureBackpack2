package com.darkona.adventurebackpack.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/** Item of a station block: a broken station keeps its fluid in the item, and the tooltip shows it. */
public class ItemBlockStation extends ItemBlock
{
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
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("Tank"));
        if (fluid == null || fluid.amount <= 0) return;
        tooltip.add(StatCollector.translateToLocalFormatted("adventurebackpack:tooltip.station.tank", fluid.amount, fluid.getLocalizedName()));
    }
}
