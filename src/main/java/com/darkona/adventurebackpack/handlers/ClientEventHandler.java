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
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.SlotTool;
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
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (event.dwheel == 0 || player == null || player.isDead || !player.isSneaking())
            return;

        ItemStack backpack = Wearing.getWearingBackpack(player);
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (backpack == null || heldItem == null)
            return;

        boolean isWheelUp = event.dwheel > 0;
        Item theItem = heldItem.getItem();

        if (ConfigHandler.enableToolsCycling && !Wearing.getWearingBackpackInv(player).getDisableCycling() && SlotTool.isValidTool(heldItem)
                || BackpackTypes.getType(backpack) == BackpackTypes.SKELETON && theItem.equals(Items.bow)) //TODO add bow case to server
        {
            ModNetwork.net.sendToServer(new CycleToolPacket.CycleToolMessage(isWheelUp, CycleToolPacket.CYCLE_TOOL_ACTION));
            ServerActions.cycleTool(player, isWheelUp);
            event.setCanceled(true);
        }
        else if (theItem == ModItems.hose)
        {
            ModNetwork.net.sendToServer(new CycleToolPacket.CycleToolMessage(isWheelUp, CycleToolPacket.SWITCH_HOSE_ACTION));
            ServerActions.switchHose(player, isWheelUp, ServerActions.HOSE_SWITCH);
            event.setCanceled(true);
        }
    }
}