package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.network.CopterPacket;
import com.darkona.adventurebackpack.network.CycleToolPacket;
import com.darkona.adventurebackpack.network.GUIPacket;
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
            return Key.OPEN_BACKPACK_INVENTORY;
        }
        if (Keybindings.toggleHose.isPressed())
        {
            return Key.TOGGLE_HOSE_TANK;
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

        if (keypressed == Key.OPEN_BACKPACK_INVENTORY)
        {
            if (player != null && mc.inGameHasFocus)
            {
                if (Wearing.isWearingBackpack(player))
                {
                    ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.BACKPACK_GUI, GUIPacket.FROM_KEYBIND));
                }
                if (Wearing.isWearingCopter(player))
                {
                    if (!player.isSneaking())
                    {
                        ModNetwork.net.sendToServer(new CopterPacket.CopterMessage(CopterPacket.TOGGLE, ""));
                        ServerActions.toggleCopterPack(player, Wearing.getWearingCopter(player), CopterPacket.TOGGLE);
                    } else
                    {
                        ModNetwork.net.sendToServer(new CopterPacket.CopterMessage(CopterPacket.ON_OFF, ""));
                        ServerActions.toggleCopterPack(player, Wearing.getWearingCopter(player), CopterPacket.ON_OFF);
                    }

                }
            }
        }

        if (keypressed == Key.TOGGLE_HOSE_TANK)
        {
            ModNetwork.net.sendToServer(new CycleToolPacket.CycleToolMessage(0, (player).inventory.currentItem, CycleToolPacket.TOGGLE_HOSE_TANK));
            ServerActions.switchHose(player, ServerActions.HOSE_TOGGLE, 0, (player).inventory.currentItem);
        }

        /*if(keypressed == Key.JUMP && player!=null && Wearing.isWearingCopter(player))
        {

        }*/
    }
}
