package com.darkona.adventurebackpack.proxy;

import com.darkona.adventurebackpack.blocks.TileAdventureBackpack;
import com.darkona.adventurebackpack.client.render.RendererAdventureBackpackBlock;
import com.darkona.adventurebackpack.client.render.RendererItemAdventureBackpack;
import com.darkona.adventurebackpack.client.render.RendererItemAdventureHat;
import com.darkona.adventurebackpack.init.ModItems;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraftforge.client.MinecraftForgeClient;

/**
 * Created by Darkona on 10/10/2014.
 */
public class ClientProxy extends CommonProxy {

    public void initRenderers() {
        MinecraftForgeClient.registerItemRenderer(ModItems.adventureHat, new RendererItemAdventureHat());
        MinecraftForgeClient.registerItemRenderer(ModItems.adventureBackpack, new RendererItemAdventureBackpack());

        ClientRegistry.bindTileEntitySpecialRenderer(TileAdventureBackpack.class, new RendererAdventureBackpackBlock());
    }

}
