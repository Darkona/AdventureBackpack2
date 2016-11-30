package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.network.CycleToolPacket;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.Wearing;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 17/10/2014
 *
 * @author Darkona
 */
public class ClientEventHandler
{
    /**
     * Makes the tool tips of the backpacks have the Tank information displayed below.
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void toolTips(ItemTooltipEvent event)
    {
        if (event.itemStack.getItem() instanceof ItemAdventureBackpack)
        {
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            FluidTank tank = new FluidTank(Constants.basicTankCapacity);
            String tankInfo = "";
            if (compound != null)
            {
                if (compound.hasKey("leftTank"))
                {
                    tank.readFromNBT(compound.getCompoundTag("leftTank"));
                    String name = tank.getFluid() == null ? "" : tank.getFluid().getLocalizedName();
                    tankInfo = EnumChatFormatting.BLUE + "Left Tank: " + tank.getFluidAmount() + "/" + tank.getCapacity() + " " + name;

                    event.toolTip.add(tankInfo);
                }
                if (compound.hasKey("rightTank"))
                {
                    tank.readFromNBT(compound.getCompoundTag("rightTank"));
                    String name = tank.getFluid() == null ? "" : tank.getFluid().getLocalizedName();
                    tankInfo = EnumChatFormatting.RED + "Right Tank: " + tank.getFluidAmount() + "/" + tank.getCapacity() + " " + name;

                    event.toolTip.add(tankInfo);
                }
            }
        }
    }
    /**
     * @param event
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void mouseWheelDetect(MouseEvent event)
    {
        /*Special thanks go to MachineMuse, both for inspiration and the event. God bless you girl.*/
        Minecraft mc = Minecraft.getMinecraft();
        int dWheel = event.dwheel;
        if (dWheel != 0)
        {
            //LogHelper.debug("Mouse Wheel moving");
            EntityClientPlayerMP player = mc.thePlayer;
            if (player != null && !player.isDead && player.isSneaking())
            {
                ItemStack backpack = Wearing.getWearingBackpack(player);
                if (backpack != null && backpack.getItem() instanceof ItemAdventureBackpack)
                {
                    if (player.getCurrentEquippedItem() != null)
                    {
                        int slot = player.inventory.currentItem;
                        ItemStack heldItem = player.inventory.getStackInSlot(slot);
                        Item theItem = heldItem.getItem();

                        if (ConfigHandler.enableToolsCycling && Wearing.getBackpackInv(player, true).getCyclingStatus() && SlotTool.isValidTool(heldItem) ||
                                (BackpackNames.getBackpackColorName(backpack).equals("Skeleton") && theItem.equals(Items.bow)))
                        {
                            ModNetwork.net.sendToServer(new CycleToolPacket.CycleToolMessage(dWheel, slot, CycleToolPacket.CYCLE_TOOL_ACTION));
                            ServerActions.cycleTool(player, dWheel, slot);
                            event.setCanceled(true);
                        }

                        if (theItem instanceof ItemHose)
                        {
                            ModNetwork.net.sendToServer(new CycleToolPacket.CycleToolMessage(dWheel, slot, CycleToolPacket.SWITCH_HOSE_ACTION));
                            ServerActions.switchHose(player, ServerActions.HOSE_SWITCH, dWheel, slot);
                            event.setCanceled(true);
                    }
                }
            }
        }
    }
  }
}