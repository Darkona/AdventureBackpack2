package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.client.GrappleClient;
import com.darkona.adventurebackpack.common.KeyActions;
import com.darkona.adventurebackpack.common.KeyActions.Action;
import com.darkona.adventurebackpack.common.KeyActions.Key;
import com.darkona.adventurebackpack.common.KeyContext;
import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.common.ToolCycleInput;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.network.KeyActionPacket;
import com.darkona.adventurebackpack.network.PlayerActionPacket;
import com.darkona.adventurebackpack.network.SyncPropertiesPacket;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class KeybindHandler
{
    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        boolean gear = Keybindings.gear.isPressed();
        boolean ability = Keybindings.ability.isPressed();
        boolean mode = Keybindings.mode.isPressed();
        boolean state = Keybindings.state.isPressed();
        boolean jump = mc.gameSettings.keyBindJump.isPressed();

        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        if (jump)
        {
            GrappleClient.releaseOnJump(player);
            if (player.ridingEntity instanceof EntityFriendlySpider)
            {
                ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(PlayerActionPacket.spiderJump));
                ((EntityFriendlySpider) player.ridingEntity).setJumping(true);
            }
        }

        if (!mc.inGameHasFocus || player.isDead) return;
        if (gear) openGear(player);
        boolean toolKey = ToolCycleInput.keyAllowed(ConfigHandler.TOOL_CYCLE_ENABLED, ConfigHandler.TOOL_CYCLE_MODE);
        if (ability) press(player, Key.ABILITY, 1, toolKey);
        if (mode) press(player, Key.MODE, 1, toolKey);
        if (state) press(player, Key.STATE, KeyActions.direction(player.isSneaking()), toolKey);
    }

    private static void openGear(EntityPlayer player)
    {
        byte type;
        switch (KeyContext.resolve(Key.GEAR, player, false))
        {
            case OPEN_BACKPACK: type = GUIPacket.BACKPACK_GUI; break;
            case OPEN_COPTER: type = GUIPacket.COPTER_GUI; break;
            case OPEN_JETPACK: type = GUIPacket.JETPACK_GUI; break;
            case OPEN_SCUBA: type = GUIPacket.SCUBA_GUI; break;
            case OPEN_FLAMETHROWER: type = GUIPacket.FLAMETHROWER_GUI; break;
            default: return;
        }
        ModNetwork.net.sendToServer(new SyncPropertiesPacket.Message());
        ModNetwork.net.sendToServer(new GUIPacket.GUImessage(type, GUIPacket.FROM_KEYBIND));
    }

    public static boolean press(EntityPlayer player, Key key, int direction, boolean toolCycleAllowed)
    {
        return perform(player, key, direction, KeyContext.resolve(key, player, toolCycleAllowed));
    }

    public static boolean perform(EntityPlayer player, Key key, int direction, Action action)
    {
        if (action == Action.NONE) return false;
        ModNetwork.net.sendToServer(new KeyActionPacket.Message(key, direction));
        int slot = player.inventory.currentItem;
        switch (action)
        {
            case HOSE_TANK:
                ServerActions.switchHose(player, ServerActions.HOSE_TOGGLE, 0, slot);
                break;
            case HOSE_MODE:
                ServerActions.switchHose(player, ServerActions.HOSE_SWITCH, direction, slot);
                break;
            case CYCLE_TOOL:
                ServerActions.cycleTool(player, direction, slot);
                break;
            default:
                break;
        }
        return true;
    }
}
