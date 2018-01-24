package com.darkona.adventurebackpack.handlers;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
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
import com.darkona.adventurebackpack.reference.BackpackTypes;
import com.darkona.adventurebackpack.reference.GeneralReference;

/**
 * Created on 24.03.2017
 *
 * @author Ugachaga
 */
public class TooltipEventHandler
{
    private List<String> eventTip;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unused")
    public void itemsTooltips(ItemTooltipEvent event)
    {
        if (!ConfigHandler.enableTooltips)
            return;

        eventTip = event.toolTip;
        Item theItem = event.itemStack.getItem();

        if (theItem instanceof ItemAdventureBackpack)
        {
            FluidTank tank = new FluidTank(Constants.BASIC_TANK_CAPACITY);
            NBTTagCompound backpackTag = event.itemStack.stackTagCompound.getCompoundTag(Constants.WEARABLE_TAG);

            if (GuiScreen.isShiftKeyDown())
            {
                NBTTagList itemList = backpackTag.getTagList(Constants.INVENTORY, NBT.TAG_COMPOUND);
                makeTip(local("backpack.slots.used") + ": " + inventoryTooltip(itemList));

                tank.readFromNBT(backpackTag.getCompoundTag(Constants.LEFT_TANK));
                makeTip(local("backpack.tank.left") + ": " + tankTooltip(tank));

                tank.readFromNBT(backpackTag.getCompoundTag(Constants.RIGHT_TANK));
                makeTip(local("backpack.tank.right") + ": " + tankTooltip(tank));

                shiftFooter();
            }
            else if (!GuiScreen.isCtrlKeyDown())
            {
                makeTip(holdShift());
            }

            if (GuiScreen.isCtrlKeyDown())
            {
                boolean cycling = !backpackTag.getBoolean("disableCycling");
                makeTip(local("backpack.cycling") + ": " + switchTooltip(cycling, true));
                makeTip(pressKeyFormat(actionKeyFormat()), locals("backpack.cycling.key"),
                        " " + switchTooltip(!cycling, false));

                if (BackpackTypes.isNightVision(BackpackTypes.getType(backpackTag.getByte("type"))))
                {
                    boolean vision = !backpackTag.getBoolean("disableNVision");
                    makeTip(local("backpack.vision") + ": " + switchTooltip(vision, true));
                    makeTip(pressShiftKeyFormat(actionKeyFormat()), locals("backpack.vision.key"),
                            " " + switchTooltip(!vision, false));
                }
            }
        }
        else if (theItem instanceof ItemCoalJetpack)
        {
            FluidTank waterTank = new FluidTank(Constants.JETPACK_WATER_CAPACITY);
            FluidTank steamTank = new FluidTank(Constants.JETPACK_STEAM_CAPACITY);
            NBTTagCompound jetpackTag = event.itemStack.stackTagCompound.getCompoundTag(Constants.WEARABLE_TAG);

            if (GuiScreen.isShiftKeyDown())
            {
                NBTTagList itemList = jetpackTag.getTagList(Constants.INVENTORY, NBT.TAG_COMPOUND);
                makeTip(local("jetpack.fuel") + ": " + slotStackTooltip(itemList, Constants.JETPACK_FUEL_SLOT));

                waterTank.readFromNBT(jetpackTag.getCompoundTag(Constants.JETPACK_WATER_TANK));
                makeTip(local("jetpack.tank.water") + ": " + tankTooltip(waterTank));

                steamTank.readFromNBT(jetpackTag.getCompoundTag(Constants.JETPACK_STEAM_TANK));
                // special case for steam, have to set displayed fluid name manually, cuz technically it's water
                String theSteam = steamTank.getFluidAmount() > 0 ? EnumChatFormatting.AQUA + local("steam") : "";
                makeTip(local("jetpack.tank.steam") + ": " + tankTooltip(steamTank, false) + theSteam);

                shiftFooter();
            }
            else if (!GuiScreen.isCtrlKeyDown())
            {
                makeTip(holdShift());
            }

            if (GuiScreen.isCtrlKeyDown())
            {
                makeTip(local("max.altitude") + ": " + whiteFormat("185 "), locals("meters"));
                makeTip(pressShiftKeyFormat(actionKeyFormat()), locals("jetpack.key.onoff"), " " + local("on"));
            }
        }
        else if (theItem instanceof ItemCopterPack)
        {
            FluidTank fuelTank = new FluidTank(Constants.COPTER_FUEL_CAPACITY);

            if (GuiScreen.isShiftKeyDown())
            {
                fuelTank.readFromNBT(event.itemStack.stackTagCompound.getCompoundTag(Constants.COPTER_FUEL_TANK));
                makeTip(local("copter.tank.fuel") + ": " + tankTooltip(fuelTank));
                makeTip(local("copter.rate.fuel") + ": " + fuelConsumptionTooltip(fuelTank));

                shiftFooter();
            }
            else if (!GuiScreen.isCtrlKeyDown())
            {
                makeTip(holdShift());
            }

            if (GuiScreen.isCtrlKeyDown())
            {
                makeTip(local("max.altitude") + ": " + whiteFormat("250 "), locals("meters"));
                makeTip(pressShiftKeyFormat(actionKeyFormat()), locals("copter.key.onoff"), " " + local("on"));
                makeTip(pressKeyFormat(actionKeyFormat()), locals("copter.key.hover"));
            }
        }
        else if (theItem instanceof ItemHose)
        {
            if (GuiScreen.isCtrlKeyDown())
            {
                makeTip(local("hose.key.header") + ":");
                makeTip("- " + pressKeyFormat(actionKeyFormat()), locals("hose.key.tank"));
                makeTip("- " + pressShiftKeyFormat(whiteFormat(local("mouse.wheel"))), locals("hose.key.mode"));
                makeTip(locals("hose.dump"));
                makeTip(EnumChatFormatting.RED.toString() + local("hose.dump.warn"));
            }
            else
            {
                makeTip(holdCtrl());
            }
        }
    }

    private String holdShift()
    {
        return holdThe(true);
    }

    private void shiftFooter()
    {
        if (GuiScreen.isCtrlKeyDown())
            makeEmptyTip();
        else
            makeTip(holdCtrl());
    }

    private String holdCtrl()
    {
        return holdThe(false);
    }

    private String holdThe(boolean button)
    {
        return whiteFormat(EnumChatFormatting.ITALIC + "<" + (button ? local("hold.shift")
                                                                     : local("hold.ctrl")) + ">");
    }

    private String whiteFormat(String theString)
    {
        return EnumChatFormatting.WHITE + theString + EnumChatFormatting.GRAY;
    }

    private String actionKeyFormat()
    {
        return whiteFormat(Keybindings.getActionKeyName());
    }

    private String pressKeyFormat(String button)
    {
        return local("press") + " '" + button + "' ";
    }

    private String pressShiftKeyFormat(String button)
    {
        return local("press") + " Shift+'" + button + "' ";
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
            else
                itemCount--; // this need for correct count while GUI is open and bucket slots may be occupied
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

    private String switchTooltip(boolean status, boolean doFormat)
    {
        return doFormat ? switchFormat(status) : status ? local("on") : local("off");
    }

    private String switchFormat(boolean status)
    {
        String switchFormatted = status ? EnumChatFormatting.WHITE + local("on")
                                        : EnumChatFormatting.DARK_GRAY + local("off");
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
        }
        catch (Exception e)
        {
            dataFormatted = EnumChatFormatting.RED + local("error");
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
        return EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.ITALIC + local("empty");
    }

    private String local(String tip)
    {
        return StatCollector.translateToLocal("adventurebackpack:tooltips." + tip);
    }

    private String[] locals(String tips)
    {
        return local(tips).split("@", 5);
    }

    private void makeTip(String tooltip)
    {
        eventTip.add(tooltip);
    }

    private void makeTip(String[] tooltips)
    {
        makeTip(null, tooltips, null);
    }

    private void makeTip(String before, String[] tooltips)
    {
        makeTip(before, tooltips, null);
    }

    private void makeTip(String before, String[] tooltips, String after)
    {
        for (int i = 0; i < tooltips.length; i++)
        {
            String tip = "";

            if (i == 0 && before != null)
                tip += before;

            tip += tooltips[i];

            if (i == tooltips.length - 1 && after != null)
                tip += after;

            eventTip.add(tip);
        }
    }

    private void makeEmptyTip()
    {
        makeTip("");
    }
}
