package com.darkona.adventurebackpack.config;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

/**
 * Created on 14/10/2014
 *
 * @author Darkona
 */
public class Keybindings
{
    private static final String CATEGORY = "keys.adventureBackpack.category";
    private static final String OPEN_INVENTORY = "keys.adventureBackpack.openInventory";
    private static final String TOGGLE_ACTIONS = "keys.adventureBackpack.toggleActions";

    public static KeyBinding openInventory = new KeyBinding(OPEN_INVENTORY, Keyboard.KEY_B, CATEGORY);
    public static KeyBinding toggleActions = new KeyBinding(TOGGLE_ACTIONS, Keyboard.KEY_N, CATEGORY);

    public static String getInventoryKeyName()
    {
        return GameSettings.getKeyDisplayString(openInventory.getKeyCode());
    }

    public static String getActionKeyName()
    {
        return GameSettings.getKeyDisplayString(toggleActions.getKeyCode());
    }
}