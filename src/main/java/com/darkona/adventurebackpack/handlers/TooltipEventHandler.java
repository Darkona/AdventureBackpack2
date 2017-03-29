package com.darkona.adventurebackpack.handlers;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
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
    public void wearableTooltips(ItemTooltipEvent event)
    {
        if (!ConfigHandler.enableTooltips)
            return;

        if (event.itemStack.getItem() instanceof ItemAdventureBackpack)
        {
            FluidTank tank = new FluidTank(Constants.BASIC_TANK_CAPACITY);
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            NBTTagCompound backpackTag = compound.getCompoundTag(Constants.COMPOUND_TAG);

            /*if (ConfigHandler.IS_DEVENV && GuiScreen.isCtrlKeyDown())
            {
                tank.readFromNBT(backpackTag.getCompoundTag(Constants.LEFT_TANK));
                event.toolTip.add(tank.getFluidAmount() == 0 ? "" : tank.getFluid().getUnlocalizedName());
                tank.readFromNBT(backpackTag.getCompoundTag(Constants.RIGHT_TANK));
                event.toolTip.add(tank.getFluidAmount() == 0 ? "" : tank.getFluid().getUnlocalizedName());
            }*/

            if (GuiScreen.isShiftKeyDown())
            {
                NBTTagList itemList = backpackTag.getTagList(Constants.INVENTORY, NBT.TAG_COMPOUND);
                event.toolTip.add("Slots used: " + backpackTooltip(itemList));

                tank.readFromNBT(backpackTag.getCompoundTag(Constants.LEFT_TANK));
                event.toolTip.add("Left Tank: " + tankTooltip(tank, true));

                tank.readFromNBT(backpackTag.getCompoundTag(Constants.RIGHT_TANK));
                event.toolTip.add("Right Tank: " + tankTooltip(tank, true));

                if (!GuiScreen.isCtrlKeyDown())
                    event.toolTip.add(holdThe(false));

            } else if (!GuiScreen.isCtrlKeyDown())
            {
                event.toolTip.add(holdThe(true));
            }

            if (GuiScreen.isCtrlKeyDown())
            {
                String actionKey = GameSettings.getKeyDisplayString(Keybindings.toggleActions.getKeyCode());
                boolean cycling = !backpackTag.getBoolean("disableCycling");
                event.toolTip.add("Tool Cycling: " + switchTooltip(cycling, true));
                event.toolTip.add("Press '" + actionKey + "' while wearing");
                event.toolTip.add("backpack, for turn cycling " + switchTooltip(!cycling, false));

                String color = backpackTag.getString("colorName");
                for (String valid : Constants.NIGHTVISION_BACKPACKS)
                {
                    if (color.equals(valid))
                    {
                        boolean vision = !backpackTag.getBoolean("disableNVision");
                        event.toolTip.add("Night Vision: " + switchTooltip(vision, true));
                        event.toolTip.add("Press Shift+'" + actionKey + "' while wearing");
                        event.toolTip.add("backpack, for turn nightvision " + switchTooltip(!vision, false));
                    }
                }
            }

        } else if (event.itemStack.getItem() instanceof ItemCoalJetpack)
        {
            FluidTank waterTank = new FluidTank(Constants.JETPACK_WATER_CAPACITY);
            FluidTank steamTank = new FluidTank(Constants.JETPACK_STEAM_CAPACITY);
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            NBTTagCompound jetpackTag = compound.getCompoundTag(Constants.JETPACK_COMPOUND_TAG);

            if (GuiScreen.isShiftKeyDown())
            {
                //TODO add temperature, help note with: max height, keys
                NBTTagList itemList = jetpackTag.getTagList(Constants.JETPACK_INVENTORY, NBT.TAG_COMPOUND);
                event.toolTip.add("Fuel: " + slotStackTooltip(itemList, Constants.JETPACK_FUEL_SLOT));

                waterTank.readFromNBT(jetpackTag.getCompoundTag(Constants.JETPACK_WATER_TANK));
                event.toolTip.add("Left Tank: " + tankTooltip(waterTank, true));

                steamTank.readFromNBT(jetpackTag.getCompoundTag(Constants.JETPACK_STEAM_TANK));
                // special case for steam, have to set displayed fluid name manually
                String theSteam = steamTank.getFluidAmount() > 0 ? EnumChatFormatting.AQUA + "Steam" : "";
                event.toolTip.add("Right Tank: " + tankTooltip(steamTank, false) + theSteam);
            } else
            {
                event.toolTip.add(holdThe(true));
            }

        } else if (event.itemStack.getItem() instanceof ItemCopterPack)
        {
            FluidTank fuelTank = new FluidTank(Constants.COPTER_FUEL_CAPACITY);
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            fuelTank.readFromNBT(compound.getCompoundTag(Constants.COPTER_FUEL_TANK));
            if (GuiScreen.isShiftKeyDown())
            {
                //TODO add fuel consumption rate, help note with: max height, keys
                event.toolTip.add("Fuel Tank: " + tankTooltip(fuelTank, true));
            } else
            {
                event.toolTip.add(holdThe(true));
            }
        }
    }

    private String holdThe(boolean button)
    {
        return EnumChatFormatting.WHITE + "" + EnumChatFormatting.ITALIC + (button ? "<Hold Shift>" : "<Hold Control>");
    }

    private String backpackTooltip(NBTTagList itemList)
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
        return (isTool ? EnumChatFormatting.WHITE/*GREEN*/ : EnumChatFormatting.DARK_GRAY) + "[]";
    }

    private String mainSlotsFormat(int slotsUsed)
    {
        String sFormatted = String.valueOf(slotsUsed);
        if (slotsUsed == 0)
            sFormatted = EnumChatFormatting.DARK_GRAY + sFormatted;
        else if (slotsUsed == Constants.INVENTORY_MAIN_SIZE)
            sFormatted = EnumChatFormatting.WHITE + sFormatted;
        else
            sFormatted = EnumChatFormatting.GRAY + sFormatted;
        return sFormatted + "/" + Constants.INVENTORY_MAIN_SIZE;
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

    private String switchTooltip(boolean status, boolean doFormat)
    {
        return doFormat ? formatSwitch(status) : status ? "ON" : "OFF";
    }

    private String formatSwitch(boolean status)
    {
        String sFormatted = status ? EnumChatFormatting.WHITE + "ON" : EnumChatFormatting.DARK_GRAY + "OFF";
        return "[" + sFormatted + EnumChatFormatting.GRAY + "]";
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

        String dFormatted;
        try
        {
            ItemStack fuelStack = new ItemStack(GameData.getItemRegistry().getObjectById(id), 0, meta);
            dFormatted = fuelStack.getDisplayName() + " (" + stackSizeFormat(fuelStack, count) + ")";
        } catch (Exception e)
        {
            dFormatted = EnumChatFormatting.RED + "Error";
            //e.printStackTrace();
        }
        return dFormatted;
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
