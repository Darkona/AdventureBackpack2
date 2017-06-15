package com.darkona.adventurebackpack.config;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;

import com.darkona.adventurebackpack.reference.Names;

/**
 * Created on 14/10/2014
 *
 * @author Darkona
 */
public class Keybindings
{
    public static KeyBinding openBackpack = new KeyBinding(Names.keys.OPEN_BACKPACK_INVENTORY, Keyboard.KEY_B, Names.keys.CATEGORY);
    public static KeyBinding toggleActions = new KeyBinding(Names.keys.TOGGLE_BACKPACK_ACTIONS, Keyboard.KEY_N, Names.keys.CATEGORY);
    //public static KeyBinding jumpKey =
}