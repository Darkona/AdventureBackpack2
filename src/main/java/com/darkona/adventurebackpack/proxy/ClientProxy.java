package com.darkona.adventurebackpack.proxy;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.client.render.RendererAdventureBackpackBlock;
import com.darkona.adventurebackpack.client.render.RendererHose;
import com.darkona.adventurebackpack.client.render.RendererItemAdventureBackpack;
import com.darkona.adventurebackpack.client.render.RendererItemAdventureHat;

import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.handlers.KeybindHandler;

import com.darkona.adventurebackpack.init.ModItems;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraftforge.client.MinecraftForgeClient;

/**
 * Created by Darkona on 10/10/2014.
 */
public class ClientProxy implements IProxy
{

    public static RendererItemAdventureBackpack rendererItemAdventureBackpack;
    public static RendererItemAdventureHat rendererItemAdventureHat;
    public static RendererHose rendererHose;
    public static KeybindHandler keybindHandler;

    public void init()
    {
        initRenderers();
        registerKeybindings();
    }


    public void initRenderers()
    {
        rendererItemAdventureBackpack = new RendererItemAdventureBackpack();
        rendererItemAdventureHat = new RendererItemAdventureHat();
        rendererHose = new RendererHose();


        MinecraftForgeClient.registerItemRenderer(ModItems.adventureHat, rendererItemAdventureHat);
        MinecraftForgeClient.registerItemRenderer(ModItems.adventureBackpack, rendererItemAdventureBackpack);
        MinecraftForgeClient.registerItemRenderer(ModItems.hose, rendererHose);

        ClientRegistry.bindTileEntitySpecialRenderer(TileAdventureBackpack.class, new RendererAdventureBackpackBlock());
    }

    public void registerKeybindings()
    {
        ClientRegistry.registerKeyBinding(Keybindings.openBackpack);
        ClientRegistry.registerKeyBinding(Keybindings.toggleHose);
        FMLCommonHandler.instance().bus().register(new KeybindHandler());

    }

}
