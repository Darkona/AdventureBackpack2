package com.darkona.adventurebackpack.config;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import com.darkona.adventurebackpack.reference.Names;

/**
 * Created on 14/10/2014
 *
 * @author Darkona
 */
public class Keybindings
{
    public static KeyBinding openInventory = new KeyBinding(Names.keys.OPEN_INVENTORY, Keyboard.KEY_B, Names.keys.CATEGORY);
    public static KeyBinding toggleActions = new KeyBinding(Names.keys.TOGGLE_ACTIONS, Keyboard.KEY_N, Names.keys.CATEGORY);

    public static String getInventoryKeyName()
    {
        return GameSettings.getKeyDisplayString(openInventory.getKeyCode());
    }

    public static String getActionKeyName()
    {
        return GameSettings.getKeyDisplayString(toggleActions.getKeyCode());
    }
}