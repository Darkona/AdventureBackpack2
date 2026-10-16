package com.darkona.adventurebackpack.config;

import com.darkona.adventurebackpack.reference.Names;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class Keybindings
{
    public static KeyBinding gear;
    public static KeyBinding ability;
    public static KeyBinding mode;
    public static KeyBinding state;

    public static void init()
    {
        gear = new KeyBinding(Names.keys.GEAR, ConfigHandler.KEY_GEAR, Names.keys.CATEGORY);
        ability = new KeyBinding(Names.keys.ABILITY, ConfigHandler.KEY_ABILITY, Names.keys.CATEGORY);
        mode = new KeyBinding(Names.keys.MODE, ConfigHandler.KEY_MODE, Names.keys.CATEGORY);
        state = new KeyBinding(Names.keys.STATE, ConfigHandler.KEY_STATE, Names.keys.CATEGORY);
    }

    public static String keyName(KeyBinding key)
    {
        return GameSettings.getKeyDisplayString(key.getKeyCode());
    }
}
