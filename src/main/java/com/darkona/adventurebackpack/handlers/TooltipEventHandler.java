package com.darkona.adventurebackpack.handlers;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;
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
public class TooltipEventHandler
{
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unused")
    public void wearableTooltips(ItemTooltipEvent event)
    {
        if (!ConfigHandler.enableTooltips)
            return;

        String holdTheShift = String.format("%s%s<Hold Shift>", EnumChatFormatting.WHITE, EnumChatFormatting.ITALIC);

        if (event.itemStack.getItem() instanceof ItemAdventureBackpack)
        {
            FluidTank tank = new FluidTank(Constants.basicTankCapacity);
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            NBTTagCompound backpackTag = compound.getCompoundTag(Constants.compoundTag);

            if (ConfigHandler.IS_DEVENV && GuiScreen.isCtrlKeyDown())
            {
                tank.readFromNBT(backpackTag.getCompoundTag(Constants.leftTankName));
                event.toolTip.add(tank.getFluidAmount() == 0 ? "" : tank.getFluid().getUnlocalizedName());
                tank.readFromNBT(backpackTag.getCompoundTag(Constants.rightTankName));
                event.toolTip.add(tank.getFluidAmount() == 0 ? "" : tank.getFluid().getUnlocalizedName());
            }

            if (GuiScreen.isShiftKeyDown())
            {
                //TODO add cycling status (on_of), nightvision status (if any), keys
                NBTTagList itemList = backpackTag.getTagList(Constants.inventoryName, NBT.TAG_COMPOUND);
                event.toolTip.add("Slots used: " + backpackTooltip(itemList));

                tank.readFromNBT(backpackTag.getCompoundTag(Constants.leftTankName));
                event.toolTip.add("Left Tank: " + tankTooltip(tank, true));

                tank.readFromNBT(backpackTag.getCompoundTag(Constants.rightTankName));
                event.toolTip.add("Right Tank: " + tankTooltip(tank, true));
            } else
            {
                event.toolTip.add(holdTheShift);
            }

        } else if (event.itemStack.getItem() instanceof ItemCoalJetpack)
        {
            FluidTank waterTank = new FluidTank(Constants.jetpackWaterTankCapacity);
            FluidTank steamTank = new FluidTank(Constants.jetpackSteamTankCapacity);
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            NBTTagCompound jetpackTag = compound.getCompoundTag(Constants.jetpackCompoundTag);

            if (GuiScreen.isShiftKeyDown())
            {
                //TODO add temperature, help note with: max height, keys
                NBTTagList itemList = jetpackTag.getTagList(Constants.jetpackInventoryName, NBT.TAG_COMPOUND);
                event.toolTip.add("Fuel: " + fuelTooltip(itemList, Constants.jetpackFuelSlot));

                waterTank.readFromNBT(jetpackTag.getCompoundTag(Constants.jetpackWaterTankName));
                event.toolTip.add("Left Tank: " + tankTooltip(waterTank, true));

                steamTank.readFromNBT(jetpackTag.getCompoundTag(Constants.jetpackSteamTankName));
                // special case for steam, have to set displayed fluid name manually
                String theSteam = steamTank.getFluidAmount() > 0 ? EnumChatFormatting.AQUA + "Steam" : "";
                event.toolTip.add("Right Tank: " + tankTooltip(steamTank, false) + theSteam);
            } else
            {
                event.toolTip.add(holdTheShift);
            }

        } else if (event.itemStack.getItem() instanceof ItemCopterPack)
        {
            FluidTank fuelTank = new FluidTank(Constants.copterTankCapacity);
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            fuelTank.readFromNBT(compound.getCompoundTag(Constants.copterTankName));
            if (GuiScreen.isShiftKeyDown())
            {
                //TODO add fuel consumption rate, help note with: max height, keys
                event.toolTip.add("Fuel Tank: " + tankTooltip(fuelTank, true));
            } else
            {
                event.toolTip.add(holdTheShift);
            }
        }
    }

    private String backpackTooltip(NBTTagList itemList)
    {
        int itemCount = itemList.tagCount();
        boolean toolSlotU = false;
        boolean toolSlotL = false;
        for (int i = itemCount - 1; i >= 0; --i)
        {
            int slotAtI = itemList.getCompoundTagAt(i).getInteger("Slot");
            if (slotAtI < Constants.upperTool)
                break;
            else if (slotAtI == Constants.upperTool)
                toolSlotU = true;
            else if (slotAtI == Constants.lowerTool)
                toolSlotL = true;
        }
        itemCount -= (toolSlotU ? 1 : 0) + (toolSlotL ? 1 : 0);
        return toolSlotFormat(toolSlotU) + toolSlotFormat(toolSlotL) + " " + mainSlotsFormat(itemCount);
    }

    private String toolSlotFormat(boolean isTool)
    {
        return (isTool ? EnumChatFormatting.WHITE/*GREEN*/ : EnumChatFormatting.DARK_GRAY) + "[]";
    }

    private String mainSlotsFormat(int slotsUsed)
    {
        String sFormatted = String.valueOf(slotsUsed);
        if (slotsUsed == 0)
            sFormatted = EnumChatFormatting.DARK_GRAY + sFormatted;
        else if (slotsUsed == Constants.inventoryMainSize)
            sFormatted = EnumChatFormatting.WHITE + sFormatted;
        else
            sFormatted = EnumChatFormatting.GRAY + sFormatted;
        return sFormatted + "/" + Constants.inventoryMainSize;
    }

    private String tankTooltip(FluidTank tank, boolean attachName)
    {
        String fluidAmount = fluidAmountFormat(tank.getFluidAmount(), tank.getCapacity());
        String fluidName = tank.getFluid() == null ? "" : attachName ? fluidNameFormat(tank.getFluid()) : " ";
        return fluidAmount + (tank.getFluidAmount() > 0 ? "/" + tank.getCapacity() : "") + fluidName;
    }

    private String fluidAmountFormat(int fluidAmount, int tankCapacity)
    {
        String aFormatted = String.valueOf(fluidAmount);
        if (fluidAmount == tankCapacity)
            aFormatted = EnumChatFormatting.WHITE + aFormatted;
        else if (fluidAmount == 0)
            aFormatted = emptyFormat();
        return aFormatted;
    }

    private String fluidNameFormat(FluidStack fluid)
    {
        String fUnlocalized = fluid.getUnlocalizedName().toLowerCase();
        String fLocalized = fluid.getLocalizedName();
        String nFormatted = " ";
        if (fUnlocalized.contains("lava") || fUnlocalized.contains("fire"))
            nFormatted += EnumChatFormatting.RED;
        else if (fUnlocalized.contains("water"))
            nFormatted += EnumChatFormatting.BLUE;
        else if (fUnlocalized.contains("oil"))
            nFormatted += EnumChatFormatting.BLACK;
        else if (fUnlocalized.contains("fuel"))
            nFormatted += EnumChatFormatting.YELLOW;
        else if (fUnlocalized.contains("milk"))
            nFormatted += EnumChatFormatting.WHITE;
        else if (fUnlocalized.contains("xpjuice"))
            nFormatted += EnumChatFormatting.GREEN;
        else
            nFormatted += EnumChatFormatting.GRAY;
        return nFormatted + fLocalized;
    }

    private String fuelTooltip(NBTTagList itemList, int slot)
    {
        int fuelID, fuelMeta, fuelCount = fuelID = fuelMeta = 0;
        for (int i = 0; i <= slot; i++)
        {
            int slotAtI = itemList.getCompoundTagAt(i).getInteger("Slot");
            if (slotAtI == slot)
            {
                fuelID = itemList.getCompoundTagAt(i).getInteger("id");
                fuelMeta = itemList.getCompoundTagAt(i).getInteger("Damage");
                fuelCount = itemList.getCompoundTagAt(i).getInteger("Count");
                break;
            }
        }
        return stackDataFormat(fuelID, fuelMeta, fuelCount);
    }

    private String stackDataFormat(int id, int meta, int count)
    {
        if (count == 0)
            return emptyFormat();

        String fStackData;
        try
        {
            ItemStack fuelStack = new ItemStack(GameData.getItemRegistry().getObjectById(id), 0, meta);
            fStackData = fuelStack.getDisplayName() + " (" +  stackSizeFormat(fuelStack, count) + ")";
        } catch (Exception e)
        {
            fStackData = EnumChatFormatting.RED + "Error";
            //e.printStackTrace();
        }
        return fStackData;
    }

    private String stackSizeFormat(ItemStack stack, int count)
    {
        String sCount = String.valueOf(count);
        return stack.getMaxStackSize() == count ? EnumChatFormatting.WHITE + sCount + EnumChatFormatting.GRAY : sCount;
    }

    private String emptyFormat()
    {
        return String.format("%s%sEmpty", EnumChatFormatting.DARK_GRAY, EnumChatFormatting.ITALIC);
    }

}
