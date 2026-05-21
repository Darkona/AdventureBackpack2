package com.darkona.adventurebackpack.config;

import com.darkona.adventurebackpack.reference.Names;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created on 14/10/2014
 *
 * @author Darkona
 */
public class Keybindings
{
    public static KeyBinding openBackpack;
    public static KeyBinding toggleHose;
    /** Held while turning the wheel to cycle tools; unbound (0) means the sneak key, see ConfigHandler. */
    public static KeyBinding toolModifier;
    public static KeyBinding nextTool;
    public static KeyBinding previousTool;

    /** Builds the key bindings with the default keys from the config; called once, after the config is read. */
    public static void init()
    {
        openBackpack = new KeyBinding(Names.keys.OPEN_BACKPACK_INVENTORY, ConfigHandler.KEY_OPEN_BACKPACK, Names.keys.CATEGORY);
        toggleHose = new KeyBinding(Names.keys.TOGGLE_HOSE_TANK, ConfigHandler.KEY_TOGGLE, Names.keys.CATEGORY);
        toolModifier = new KeyBinding(Names.keys.TOOL_MODIFIER, ConfigHandler.KEY_TOOL_MODIFIER, Names.keys.CATEGORY);
        nextTool = new KeyBinding(Names.keys.NEXT_TOOL, ConfigHandler.KEY_NEXT_TOOL, Names.keys.CATEGORY);
        previousTool = new KeyBinding(Names.keys.PREVIOUS_TOOL, ConfigHandler.KEY_PREVIOUS_TOOL, Names.keys.CATEGORY);
    }

    /** True while the tool-cycle modifier is held: the bound key, or sneaking when it is not bound. */
    public static boolean isToolModifierDown(EntityPlayer player)
    {
        if (toolModifier.getKeyCode() == 0) return player.isSneaking();
        return GameSettings.isKeyDown(toolModifier);
    }
}
