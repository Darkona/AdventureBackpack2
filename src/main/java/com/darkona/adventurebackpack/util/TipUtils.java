package com.darkona.adventurebackpack.util;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import cpw.mods.fml.common.registry.GameData;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.reference.GeneralReference;

/**
 * Created on 24.03.2017
 *
 * @author Ugachaga
 */
public final class TipUtils
{
    private TipUtils() {}

    public static String l10n(String tip)
    {
        return StatCollector.translateToLocal("adventurebackpack:tooltips." + tip);
    }

    public static void shiftFooter(List<String> eventTips)
    {
        if (GuiScreen.isCtrlKeyDown())
            eventTips.add("");
        else
            eventTips.add(holdCtrl());
    }

    public static String holdShift()
    {
        return holdThe(true);
    }

    public static String holdCtrl()
    {
        return holdThe(false);
    }

    private static String holdThe(boolean button)
    {
        return whiteFormat(EnumChatFormatting.ITALIC + "<" + (button ? l10n("hold.shift")
                                                                     : l10n("hold.ctrl")) + ">");
    }

    public static String whiteFormat(String stringIn)
    {
        return EnumChatFormatting.WHITE + stringIn + EnumChatFormatting.GRAY;
    }

    public static String actionKeyFormat()
    {
        return whiteFormat(Keybindings.getActionKeyName());
    }

    public static String pressKeyFormat(String button)
    {
        return l10n("press") + " '" + button + "' ";
    }

    public static String pressShiftKeyFormat(String button)
    {
        return l10n("press") + " Shift+'" + button + "' ";
    }

    public static String inventoryTooltip(NBTTagList itemList)
    {
        int itemCount = itemList.tagCount();
        boolean toolSlotU = false;
        boolean toolSlotL = false;
        for (int i = itemCount - 1; i >= 0; i--)
        {
            int slotAtI = itemList.getCompoundTagAt(i).getInteger(Constants.TAG_SLOT);
            if (slotAtI < Constants.TOOL_UPPER)
                break;
            else if (slotAtI == Constants.TOOL_UPPER)
                toolSlotU = true;
            else if (slotAtI == Constants.TOOL_LOWER)
                toolSlotL = true;
            else
                itemCount--; // this need for correct count while GUI is open and bucket slots may be occupied
        }
        itemCount -= (toolSlotU ? 1 : 0) + (toolSlotL ? 1 : 0);
        return toolSlotFormat(toolSlotU) + toolSlotFormat(toolSlotL) + " " + mainSlotsFormat(itemCount);
    }

    private static String toolSlotFormat(boolean isTool)
    {
        return (isTool ? EnumChatFormatting.WHITE : EnumChatFormatting.DARK_GRAY) + "[]";
    }

    private static String mainSlotsFormat(int slotsUsed)
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

    public static String tankTooltip(FluidTank tank)
    {
        return tankTooltip(tank, true);
    }

    public static String tankTooltip(FluidTank tank, boolean attachName)
    {
        String fluidAmount = fluidAmountFormat(tank.getFluidAmount(), tank.getCapacity());
        String fluidName = tank.getFluid() == null ? "" : attachName ? fluidNameFormat(tank.getFluid()) : " ";
        return fluidAmount + (tank.getFluidAmount() > 0 ? "/" + tank.getCapacity() : "") + fluidName;
    }

    private static String fluidAmountFormat(int fluidAmount, int tankCapacity)
    {
        String amountFormatted = Integer.toString(fluidAmount);
        if (fluidAmount == tankCapacity)
            amountFormatted = EnumChatFormatting.WHITE + amountFormatted;
        else if (fluidAmount == 0)
            amountFormatted = emptyFormat();
        return amountFormatted;
    }

    private static String fluidNameFormat(FluidStack fluid)
    {
        String nameUnlocalized = fluid.getUnlocalizedName().toLowerCase();
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
        return nameFormatted + fluid.getLocalizedName();
    }

    public static String switchTooltip(boolean status, boolean doFormat)
    {
        return doFormat ? switchFormat(status) : status ? l10n("on") : l10n("off");
    }

    private static String switchFormat(boolean status)
    {
        String switchFormatted = status ? EnumChatFormatting.WHITE + l10n("on")
                                        : EnumChatFormatting.DARK_GRAY + l10n("off");
        return "[" + switchFormatted + EnumChatFormatting.GRAY + "]";
    }

    public static String slotStackTooltip(NBTTagList itemList, int slot)
    {
        int slotID, slotMeta, slotCount = slotID = slotMeta = 0;
        for (int i = 0; i <= slot; i++)
        {
            int slotAtI = itemList.getCompoundTagAt(i).getInteger(Constants.TAG_SLOT);
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

    private static String stackDataFormat(int id, int meta, int count)
    {
        if (count == 0)
            return emptyFormat();

        String dataFormatted;
        try
        {
            ItemStack iStack = new ItemStack(GameData.getItemRegistry().getObjectById(id), 0, meta);
            dataFormatted = iStack.getDisplayName() + " (" + stackSizeFormat(iStack, count) + ")";
        }
        catch (Exception e)
        {
            dataFormatted = EnumChatFormatting.RED + l10n("error");
        }
        return dataFormatted;
    }

    private static String stackSizeFormat(ItemStack stack, int count)
    {
        return stack.getMaxStackSize() == count ? whiteFormat(Integer.toString(count)) : Integer.toString(count);
    }

    public static String fuelConsumptionTooltip(FluidTank tank)
    {
        return (tank.getFluid() != null)
               ? String.format("x%.2f", GeneralReference.getFuelRate(tank.getFluid().getFluid().getName()))
               : EnumChatFormatting.DARK_GRAY + "-" ;
    }

    private static String emptyFormat()
    {
        return EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.ITALIC + l10n("empty");
    }

}