package com.darkona.adventurebackpack.handlers;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
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
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemCoalJetpack;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.reference.GeneralReference;

/**
 * Created on 24.03.2017
 *
 * @author Ugachaga
 */
public class TooltipEventHandler
{
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unused")
    public void itemsTooltips(ItemTooltipEvent event)
    {
        if (!ConfigHandler.enableTooltips)
            return;

        Item theItem = event.itemStack.getItem();

        if (theItem instanceof ItemAdventureBackpack)
        {
            FluidTank tank = new FluidTank(Constants.BASIC_TANK_CAPACITY);
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            NBTTagCompound backpackTag = compound.getCompoundTag(Constants.COMPOUND_TAG);

            /*if (ConfigHandler.IS_DEVENV && GuiScreen.isCtrlKeyDown())
            {
                tank.readFromNBT(backpackTag.getCompoundTag(Constants.LEFT_TANK));
                event.toolTip.add(tank.getFluidAmount() == 0 ? "" : tank.getFluid().getUnlocalizedName());
                event.toolTip.add(tank.getFluidAmount() == 0 ? "" : tank.getFluid().getFluid().getName());
                tank.readFromNBT(backpackTag.getCompoundTag(Constants.RIGHT_TANK));
                event.toolTip.add(tank.getFluidAmount() == 0 ? "" : tank.getFluid().getUnlocalizedName());
                event.toolTip.add(tank.getFluidAmount() == 0 ? "" : tank.getFluid().getFluid().getName());
            }*/

            if (GuiScreen.isShiftKeyDown())
            {
                NBTTagList itemList = backpackTag.getTagList(Constants.INVENTORY, NBT.TAG_COMPOUND);
                event.toolTip.add("Slots used: " + inventoryTooltip(itemList));

                tank.readFromNBT(backpackTag.getCompoundTag(Constants.LEFT_TANK));
                event.toolTip.add("Left Tank: " + tankTooltip(tank));

                tank.readFromNBT(backpackTag.getCompoundTag(Constants.RIGHT_TANK));
                event.toolTip.add("Right Tank: " + tankTooltip(tank));

                if (!GuiScreen.isCtrlKeyDown())
                    event.toolTip.add(holdCtrl());

            } else if (!GuiScreen.isCtrlKeyDown())
            {
                event.toolTip.add(holdShift());
            }

            if (GuiScreen.isCtrlKeyDown())
            {
                boolean cycling = !backpackTag.getBoolean("disableCycling");
                event.toolTip.add("Tool Cycling: " + switchTooltip(cycling, true));
                event.toolTip.add("Press '" + actionKeyFormat() + "' while wearing");
                event.toolTip.add("backpack, for turn cycling " + switchTooltip(!cycling, false));

                String color = backpackTag.getString("colorName");
                for (String valid : Constants.NIGHTVISION_BACKPACKS)
                {
                    if (color.equals(valid))
                    {
                        boolean vision = !backpackTag.getBoolean("disableNVision");
                        event.toolTip.add("Night Vision: " + switchTooltip(vision, true));
                        event.toolTip.add("Press Shift+'" + actionKeyFormat() + "' while wearing");
                        event.toolTip.add("backpack, for turn nightvision " + switchTooltip(!vision, false));
                    }
                }
            }

        } else if (theItem instanceof ItemCoalJetpack)
        {
            FluidTank waterTank = new FluidTank(Constants.JETPACK_WATER_CAPACITY);
            FluidTank steamTank = new FluidTank(Constants.JETPACK_STEAM_CAPACITY);
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            NBTTagCompound jetpackTag = compound.getCompoundTag(Constants.JETPACK_COMPOUND_TAG);

            if (GuiScreen.isShiftKeyDown())
            {
                NBTTagList itemList = jetpackTag.getTagList(Constants.JETPACK_INVENTORY, NBT.TAG_COMPOUND);
                event.toolTip.add("Fuel: " + slotStackTooltip(itemList, Constants.JETPACK_FUEL_SLOT));

                waterTank.readFromNBT(jetpackTag.getCompoundTag(Constants.JETPACK_WATER_TANK));
                event.toolTip.add("Left Tank: " + tankTooltip(waterTank));

                steamTank.readFromNBT(jetpackTag.getCompoundTag(Constants.JETPACK_STEAM_TANK));
                // special case for steam, have to set displayed fluid name manually, cuz technically it's water
                String theSteam = steamTank.getFluidAmount() > 0 ? EnumChatFormatting.AQUA + "Steam" : "";
                event.toolTip.add("Right Tank: " + tankTooltip(steamTank, false) + theSteam);

                if (!GuiScreen.isCtrlKeyDown())
                    event.toolTip.add(holdCtrl());

            } else if (!GuiScreen.isCtrlKeyDown())
            {
                event.toolTip.add(holdShift());
            }

            if (GuiScreen.isCtrlKeyDown())
            {
                event.toolTip.add("Maximum altitude: " + whiteFormat("185") + " meters");
                event.toolTip.add("Press Shift+'" + actionKeyFormat() + "' while wearing");
                event.toolTip.add("jetpack, for turn it ON");
            }

        } else if (theItem instanceof ItemCopterPack)
        {
            FluidTank fuelTank = new FluidTank(Constants.COPTER_FUEL_CAPACITY);
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            if (GuiScreen.isShiftKeyDown())
            {
                fuelTank.readFromNBT(compound.getCompoundTag(Constants.COPTER_FUEL_TANK));
                event.toolTip.add("Fuel Tank: " + tankTooltip(fuelTank));
                event.toolTip.add("Fuel consumption rate: " + fuelConsumptionTooltip(fuelTank));

                if (!GuiScreen.isCtrlKeyDown())
                    event.toolTip.add(holdCtrl());

            } else if (!GuiScreen.isCtrlKeyDown())
            {
                event.toolTip.add(holdShift());
            }

            if (GuiScreen.isCtrlKeyDown())
            {
                event.toolTip.add("Maximum altitude: " + whiteFormat("250") + " meters");
                event.toolTip.add("Press Shift+'" + actionKeyFormat() + "' while wearing");
                event.toolTip.add("copterpack, for turn it ON");
                event.toolTip.add("Press '" + actionKeyFormat() + "' during flight to");
                event.toolTip.add("switch hover mode");
            }

        } else if (theItem instanceof ItemHose)
        {
            if (GuiScreen.isCtrlKeyDown())
            {
                event.toolTip.add("While holding Hose:");
                event.toolTip.add("- press '" + actionKeyFormat() + "' to change active tank");
                event.toolTip.add("- press Shift+'" + whiteFormat("Wheel") + "' to change mode");
                event.toolTip.add("");
                event.toolTip.add("Put Hose into bucketOut slot of wearable pack");
                event.toolTip.add("to empty corresponded tank");
                event.toolTip.add(EnumChatFormatting.RED + "WARNING! Fluid will be dumped and lost. Forever.");
            } else
            {
                event.toolTip.add(holdCtrl());
            }
        }
    }

    private String holdShift()
    {
        return holdThe(true);
    }

    private String holdCtrl()
    {
        return holdThe(false);
    }

    private String holdThe(boolean button)
    {
        return whiteFormat( EnumChatFormatting.ITALIC + (button ? "<Hold Shift>" : "<Hold Ctrl>"));
    }

    private String whiteFormat(String theString)
    {
        return EnumChatFormatting.WHITE + theString + EnumChatFormatting.GRAY;
    }

    private String actionKeyFormat()
    {
        return whiteFormat(Keybindings.getActionKeyName());
    }

    private String inventoryTooltip(NBTTagList itemList)
    {
        int itemCount = itemList.tagCount();
        boolean toolSlotU = false;
        boolean toolSlotL = false;
        for (int i = itemCount - 1; i >= 0; i--)
        {
            int slotAtI = itemList.getCompoundTagAt(i).getInteger("Slot");
            if (slotAtI < Constants.UPPER_TOOL)
                break;
            else if (slotAtI == Constants.UPPER_TOOL)
                toolSlotU = true;
            else if (slotAtI == Constants.LOWER_TOOL)
                toolSlotL = true;
        }
        itemCount -= (toolSlotU ? 1 : 0) + (toolSlotL ? 1 : 0);
        return toolSlotFormat(toolSlotU) + toolSlotFormat(toolSlotL) + " " + mainSlotsFormat(itemCount);
    }

    private String toolSlotFormat(boolean isTool)
    {
        return (isTool ? EnumChatFormatting.WHITE : EnumChatFormatting.DARK_GRAY) + "[]";
    }

    private String mainSlotsFormat(int slotsUsed)
    {
        String slotsFormatted = Integer.toString(slotsUsed);
        if (slotsUsed == 0)
            slotsFormatted = EnumChatFormatting.DARK_GRAY + slotsFormatted;
        else if (slotsUsed == Constants.INVENTORY_MAIN_SIZE)
            slotsFormatted = EnumChatFormatting.WHITE + slotsFormatted;
        else
            slotsFormatted = EnumChatFormatting.GRAY + slotsFormatted;
        return slotsFormatted + "/" + Constants.INVENTORY_MAIN_SIZE;
    }

    private String tankTooltip(FluidTank tank)
    {
        return tankTooltip(tank, true);
    }

    private String tankTooltip(FluidTank tank, boolean attachName)
    {
        String fluidAmount = fluidAmountFormat(tank.getFluidAmount(), tank.getCapacity());
        String fluidName = tank.getFluid() == null ? "" : attachName ? fluidNameFormat(tank.getFluid()) : " ";
        return fluidAmount + (tank.getFluidAmount() > 0 ? "/" + tank.getCapacity() : "") + fluidName;
    }

    private String fluidAmountFormat(int fluidAmount, int tankCapacity)
    {
        String amountFormatted = Integer.toString(fluidAmount);
        if (fluidAmount == tankCapacity)
            amountFormatted = EnumChatFormatting.WHITE + amountFormatted;
        else if (fluidAmount == 0)
            amountFormatted = emptyFormat();
        return amountFormatted;
    }

    private String fluidNameFormat(FluidStack fluid)
    {
        String nameUnlocalized = fluid.getUnlocalizedName().toLowerCase();
        String nameLocalized = fluid.getLocalizedName();
        String nameFormatted = " ";
        if (nameUnlocalized.contains("lava") || nameUnlocalized.contains("fire"))
            nameFormatted += EnumChatFormatting.RED;
        else if (nameUnlocalized.contains("water"))
            nameFormatted += EnumChatFormatting.BLUE;
        else if (nameUnlocalized.contains("oil"))
            nameFormatted += EnumChatFormatting.DARK_GRAY;
        else if (nameUnlocalized.contains("fuel") || nameUnlocalized.contains("creosote"))
            nameFormatted += EnumChatFormatting.YELLOW;
        else if (nameUnlocalized.contains("milk"))
            nameFormatted += EnumChatFormatting.WHITE;
        else if (nameUnlocalized.contains("xpjuice"))
            nameFormatted += EnumChatFormatting.GREEN;
        else
            nameFormatted += EnumChatFormatting.GRAY;
        return nameFormatted + nameLocalized;
    }

    private String switchTooltip(boolean status, boolean doFormat)
    {
        return doFormat ? switchFormat(status) : status ? "ON" : "OFF";
    }

    private String switchFormat(boolean status)
    {
        String switchFormatted = status ? EnumChatFormatting.WHITE + "ON" : EnumChatFormatting.DARK_GRAY + "OFF";
        return "[" + switchFormatted + EnumChatFormatting.GRAY + "]";
    }

    private String slotStackTooltip(NBTTagList itemList, int slot)
    {
        int slotID, slotMeta, slotCount = slotID = slotMeta = 0;
        for (int i = 0; i <= slot; i++)
        {
            int slotAtI = itemList.getCompoundTagAt(i).getInteger("Slot");
            if (slotAtI == slot)
            {
                slotID = itemList.getCompoundTagAt(i).getInteger("id");
                slotMeta = itemList.getCompoundTagAt(i).getInteger("Damage");
                slotCount = itemList.getCompoundTagAt(i).getInteger("Count");
                break;
            }
        }
        return stackDataFormat(slotID, slotMeta, slotCount);
    }

    private String stackDataFormat(int id, int meta, int count)
    {
        if (count == 0)
            return emptyFormat();

        String dataFormatted;
        try
        {
            ItemStack iStack = new ItemStack(GameData.getItemRegistry().getObjectById(id), 0, meta);
            dataFormatted = iStack.getDisplayName() + " (" + stackSizeFormat(iStack, count) + ")";
        } catch (Exception e)
        {
            dataFormatted = EnumChatFormatting.RED + "Error";
            //e.printStackTrace();
        }
        return dataFormatted;
    }

    private String stackSizeFormat(ItemStack stack, int count)
    {
        return stack.getMaxStackSize() == count ? whiteFormat(Integer.toString(count)) : Integer.toString(count);
    }

    private String fuelConsumptionTooltip(FluidTank tank)
    {
        if (tank.getFluidAmount() > 0 && GeneralReference.isValidFuel(tank.getFluid().getFluid()))
        {
            return String.format("x%.2f", GeneralReference.liquidFuels.get(tank.getFluid().getFluid().getName()));
        }
        return EnumChatFormatting.DARK_GRAY + "-";
    }

    private String emptyFormat()
    {
        return EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.ITALIC + "Empty";
    }
}
