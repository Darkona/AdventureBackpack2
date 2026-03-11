package com.darkona.adventurebackpack.config;

import com.darkona.adventurebackpack.reference.Names;
import net.minecraft.client.settings.KeyBinding;

/**
 * Created on 14/10/2014
 *
 * @author Darkona
 */
public class Keybindings
{
    public static KeyBinding openBackpack;
    public static KeyBinding toggleHose;

    /** Builds the key bindings with the default keys from the config; called once, after the config is read. */
    public static void init()
    {
        openBackpack = new KeyBinding(Names.keys.OPEN_BACKPACK_INVENTORY, ConfigHandler.KEY_OPEN_BACKPACK, Names.keys.CATEGORY);
        toggleHose = new KeyBinding(Names.keys.TOGGLE_HOSE_TANK, ConfigHandler.KEY_TOGGLE, Names.keys.CATEGORY);
    }
}
