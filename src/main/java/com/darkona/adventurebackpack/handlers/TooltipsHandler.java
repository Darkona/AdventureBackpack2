package com.darkona.adventurebackpack.handlers;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemCoalJetpack;
import com.darkona.adventurebackpack.item.ItemCopterPack;

/**
 * Created by Ugachaga on 24.03.2017.
 */
public class TooltipsHandler
{
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void toolTips(ItemTooltipEvent event)
    {
        if (!ConfigHandler.enableTooltips)
            return;

        String holdTheShift = String.format("%s%s<Hold Shift>", EnumChatFormatting.WHITE, EnumChatFormatting.ITALIC);

        if (event.itemStack.getItem() instanceof ItemAdventureBackpack)
        {
            FluidTank tank = new FluidTank(Constants.basicTankCapacity);
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            NBTTagCompound backpackData = null;
            if (compound != null && compound.hasKey("backpackData"))
                backpackData = compound.getCompoundTag("backpackData");

            if (backpackData != null && GuiScreen.isShiftKeyDown())
            {
                if (backpackData.hasKey("ABPItems"))
                {
                    int itemCount = backpackData.getTagList("ABPItems", NBT.TAG_COMPOUND).tagCount();
                    event.toolTip.add("Slots used: " + itemCount + "/41"); //TODO format, add toolslots info
                }
                if (backpackData.hasKey("leftTank"))
                {
                    tank.readFromNBT(backpackData.getCompoundTag("leftTank"));
                    event.toolTip.add("Left Tank: " + tankToolTip(tank));
                }
                if (backpackData.hasKey("rightTank"))
                {
                    tank.readFromNBT(backpackData.getCompoundTag("rightTank"));
                    event.toolTip.add("Right Tank: " + tankToolTip(tank));
                }
            } else if (backpackData != null && backpackData.hasKey("ABPItems"))
            {
                event.toolTip.add(holdTheShift);
            }
        } else if (event.itemStack.getItem() instanceof ItemCoalJetpack)
        {
            FluidTank waterTank = new FluidTank(Constants.jetpackWaterTankCapacity);
            FluidTank steamTank = new FluidTank(Constants.jetpackSteamTankCapacity);
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            NBTTagCompound jetpackData = null;
            if (compound != null && compound.hasKey("jetpackData"))
                jetpackData = compound.getCompoundTag("jetpackData");

            if (jetpackData != null && GuiScreen.isShiftKeyDown())
            {
                if (jetpackData.hasKey("waterTank"))
                {
                    waterTank.readFromNBT(jetpackData.getCompoundTag("waterTank"));
                    event.toolTip.add("Left Tank: " + tankToolTip(waterTank));
                }
                if (jetpackData.hasKey("steamTank"))
                {
                    steamTank.readFromNBT(jetpackData.getCompoundTag("steamTank"));
                    event.toolTip.add("Right Tank: " + tankToolTip(steamTank)); //TODO water->steam
                }
                //TODO add temperature, fuel, max height, keys maybe?
            } else if (jetpackData != null && jetpackData.hasKey("waterTank"))
            {
                event.toolTip.add(holdTheShift);
            }
        } else if (event.itemStack.getItem() instanceof ItemCopterPack)
        {
            FluidTank fuelTank = new FluidTank(Constants.copterTankCapacity);
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            if (compound != null && compound.hasKey("fuelTank"))
                fuelTank.readFromNBT(compound.getCompoundTag("fuelTank"));

            if (GuiScreen.isShiftKeyDown())
            {
                //TODO add max height, keys maybe?,
                event.toolTip.add("Fuel Tank: " + tankToolTip(fuelTank));
            } else
            {
                event.toolTip.add(holdTheShift);
            }
        }
    }

    private String tankToolTip(FluidTank tank)
    {
        String fluidAmount = amountFormat(tank.getFluidAmount(), tank.getCapacity());
        String fluidName = tank.getFluid() == null ? "" : fluidFormat(tank.getFluid());
        return fluidAmount + (tank.getFluidAmount() > 0 ? "/" + tank.getCapacity() : "") + fluidName;
    }

    private String amountFormat(Integer fluidAmount, int tankCapacity)
    {
        String amountFormatted = fluidAmount.toString();
        if (fluidAmount == tankCapacity)
            amountFormatted = EnumChatFormatting.WHITE + amountFormatted;
        else if (fluidAmount == 0)
            amountFormatted = EnumChatFormatting.ITALIC + "Empty";
        return amountFormatted;
    }

    private String fluidFormat(FluidStack fluid)
    {
        String fluidUnlocName = fluid.getUnlocalizedName().toLowerCase();
        String fluidLocName = fluid.getLocalizedName();
        String fluidFormat = " ";
        if (fluidUnlocName.contains("lava") || fluidUnlocName.contains("fire"))
            fluidFormat += EnumChatFormatting.RED;
        else if (fluidUnlocName.contains("water"))
            fluidFormat += EnumChatFormatting.BLUE;
        else if (fluidUnlocName.contains("oil"))
            fluidFormat += EnumChatFormatting.BLACK;
        else if (fluidUnlocName.contains("fuel"))
            fluidFormat += EnumChatFormatting.YELLOW;
        else if (fluidUnlocName.contains("milk"))
            fluidFormat += EnumChatFormatting.WHITE;
        return fluidFormat + fluidLocName;
    }

}
