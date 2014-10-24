package com.darkona.adventurebackpack.config;

import com.darkona.adventurebackpack.reference.Names;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Created on 14/10/2014
 *
 * @author Darkona
 */
public class Keybindings
{
    public static KeyBinding openBackpack = new KeyBinding(Names.keys.OPEN_BACKPACK_INVENTORY, Keyboard.KEY_B, Names.keys.CATEGORY);
    public static KeyBinding toggleHose = new KeyBinding(Names.keys.TOGGLE_HOSE_TANK, Keyboard.KEY_N, Names.keys.CATEGORY);
}


