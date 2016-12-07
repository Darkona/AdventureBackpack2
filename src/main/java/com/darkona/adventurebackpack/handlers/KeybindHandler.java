package com.darkona.adventurebackpack.handlers;

import org.lwjgl.input.Keyboard;

import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.network.*;
import com.darkona.adventurebackpack.reference.Key;
import com.darkona.adventurebackpack.util.Wearing;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Darkona on 11/10/2014.
 */
public class KeybindHandler
{

    private static Key getPressedKeyBinding()
    {
        if (Keybindings.openBackpack.isPressed())
        {
            return Key.INVENTORY_KEY;
        }
        if (Keybindings.toggleHose.isPressed())
        {
            return Key.TOGGLE_KEY;
        }

        if (Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed())
        {
            return Key.JUMP;
        }
        return Key.UNKNOWN;
    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event)
    {
        Key keypressed = getPressedKeyBinding();
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;

        if(player != null)
        {
            if (keypressed == Key.INVENTORY_KEY)
            {
                if (mc.inGameHasFocus)
                {
                    ModNetwork.net.sendToServer(new SyncPropertiesPacket.Message());
                    if (Wearing.isWearingBackpack(player))
                    {
                    	if(player.isSneaking()) 
                    	{
                    		ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.UNEQUIP_WEARABLE,false));
                    	} else 
                    	{
                    		ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.BACKPACK_GUI, GUIPacket.FROM_KEYBIND));
                    	}
                    }
                    if (Wearing.isWearingCopter(player))
                    {
                        ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.COPTER_GUI, GUIPacket.FROM_KEYBIND));
                    }
                    if (Wearing.isWearingSteam(player))
                    {
                        ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.JETPACK_GUI, GUIPacket.FROM_KEYBIND));
                    }
                    if (Wearing.isHoldingBackpack(player))
                    {
                    	if(player.isSneaking())
                    	{
                    		ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.EQUIP_WEARABLE, false));
                    	}
                    }
                }
            }

            if (keypressed == Key.TOGGLE_KEY)
            {
                if(player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemHose)
                {
                    ModNetwork.net.sendToServer(new CycleToolPacket.CycleToolMessage(0, (player).inventory.currentItem, CycleToolPacket.TOGGLE_HOSE_TANK));
                    ServerActions.switchHose(player, ServerActions.HOSE_TOGGLE, 0, (player).inventory.currentItem);
                }
                if (Wearing.isWearingCopter(player))
                {
                    if (!player.isSneaking())
                    {
                        ModNetwork.net.sendToServer(new WearableModePacket.Message(WearableModePacket.COPTER_TOGGLE, ""));
                        ServerActions.toggleCopterPack(player, Wearing.getWearingCopter(player), WearableModePacket.COPTER_TOGGLE);
                    } else
                    {
                        ModNetwork.net.sendToServer(new WearableModePacket.Message(WearableModePacket.COPTER_ON_OFF, ""));
                        ServerActions.toggleCopterPack(player, Wearing.getWearingCopter(player), WearableModePacket.COPTER_ON_OFF);
                    }
                }
                if (Wearing.isWearingSteam(player))
                {
                    if (player.isSneaking())
                    {
                        ModNetwork.net.sendToServer(new WearableModePacket.Message(WearableModePacket.JETPACK_ON_OFF, ""));
                        ServerActions.toggleSteamJetpack(player, Wearing.getWearingSteam(player), WearableModePacket.JETPACK_ON_OFF);
                    }
                }
            }

            if(keypressed == Key.JUMP )
            {
                if(player.ridingEntity != null && player.ridingEntity instanceof EntityFriendlySpider)
                {
                    ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(PlayerActionPacket.spiderJump));
                    ((EntityFriendlySpider)player.ridingEntity).setJumping(true);
                }
            }
        }
    }
}
