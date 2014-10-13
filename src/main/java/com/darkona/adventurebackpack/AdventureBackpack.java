package com.darkona.adventurebackpack;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.handlers.GuiHandler;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModEventListeners;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.proxy.CommonProxy;
import com.darkona.adventurebackpack.references.ModInfo;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Javier Darkona on 10/10/2014.
 */

@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION, guiFactory = ModInfo.GUI_FACTORY_CLASS)
public class AdventureBackpack {

    @Mod.Instance(ModInfo.MOD_ID)
    public static AdventureBackpack instance;

    //Static things
    public static CreativeTabAB creativeTab;
    public static SimpleNetworkWrapper networkWrapper;
    public static GuiHandler guiHandler;


    @SidedProxy(clientSide = ModInfo.MOD_CLIENT_PROXY, serverSide = ModInfo.MOD_SERVER_PROXY)
    public static CommonProxy proxy;

    ModEventListeners eventlistener = new ModEventListeners();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MOD_ID + "_netChannel");

        FMLCommonHandler.instance().bus().register(new ConfigHandler());
        ConfigHandler.init(event.getSuggestedConfigurationFile());

        ModItems.init();
        ModBlocks.init();

        proxy.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {


        MinecraftForge.EVENT_BUS.register(eventlistener);
        FMLCommonHandler.instance().bus().register(eventlistener);
        guiHandler = new GuiHandler();

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

}
