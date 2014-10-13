package com.darkona.adventurebackpack.handlers;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Created by Darkona on 11/10/2014.
 */
public class KeybindHandler {
    public static KeyBinding jump;

    public static void init() {
        // Define the "ping" binding, with (unlocalized) name "key.ping" and
        // the category with (unlocalized) name "key.categories.mymod" and
        // key code 24 ("O", LWJGL constant: Keyboard.KEY_O)
        jump = new KeyBinding("key.jump", Keyboard.KEY_SPACE, "key.categories" + "");
        // Register both KeyBindings to the ClientRegistry
        ClientRegistry.registerKeyBinding(jump);
    }
}
