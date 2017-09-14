package com.darkona.adventurebackpack.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.network.CycleToolPacket;
import com.darkona.adventurebackpack.reference.BackpackTypes;
import com.darkona.adventurebackpack.util.Wearing;

/**
 * Created on 17/10/2014
 *
 * @author Darkona
 */
public class ClientEventHandler
{
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

                        if ((ConfigHandler.enableToolsCycling && !Wearing.getWearingBackpackInv(player).getDisableCycling() && SlotTool.isValidTool(heldItem))
                                || (BackpackTypes.getBackpackColorName(backpack).equals("Skeleton") && theItem.equals(Items.bow)))
                        {
                            ModNetwork.net.sendToServer(new CycleToolPacket.CycleToolMessage(dWheel, slot, CycleToolPacket.CYCLE_TOOL_ACTION));
                            ServerActions.cycleTool(player, dWheel, slot);
                            event.setCanceled(true);
                        }

                        if (theItem instanceof ItemHose)
                        {
                            ModNetwork.net.sendToServer(new CycleToolPacket.CycleToolMessage(dWheel, slot, CycleToolPacket.SWITCH_HOSE_ACTION));
                            ServerActions.switchHose(player, dWheel, ServerActions.HOSE_SWITCH);
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }
}