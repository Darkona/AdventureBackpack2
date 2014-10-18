package com.darkona.adventurebackpack;

import com.darkona.adventurebackpack.api.FluidEffectRegistry;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.handlers.BackpackEventHandler;
import com.darkona.adventurebackpack.handlers.ClientEventHandler;
import com.darkona.adventurebackpack.handlers.GuiHandler;
import com.darkona.adventurebackpack.handlers.PlayerEventHandler;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModFluids;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.proxy.IProxy;
import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Javier Darkona on 10/10/2014.
 */

@Mod(modid = ModInfo.MOD_ID,
        name = ModInfo.MOD_NAME,
        version = ModInfo.MOD_VERSION,
        guiFactory = ModInfo.GUI_FACTORY_CLASS)
public class AdventureBackpack
{

    @Mod.Instance(ModInfo.MOD_ID)
    public static AdventureBackpack instance;

    //Static things
    public static CreativeTabAB creativeTab;

    @SidedProxy(clientSide = ModInfo.MOD_CLIENT_PROXY, serverSide = ModInfo.MOD_SERVER_PROXY)
    public static IProxy proxy;

    PlayerEventHandler eventlistener;
    ClientEventHandler clientEventHandler;
    BackpackEventHandler backpackEventHandler;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {

        //Configuration
        FMLCommonHandler.instance().bus().register(new ConfigHandler());
        ConfigHandler.init(event.getSuggestedConfigurationFile());

        //NETWORK
        ModNetwork.init();

        //ModStuff
        ModItems.init();
        ModBlocks.init();
        ModFluids.init();
        FluidEffectRegistry.init();

        // EVENTS
        eventlistener = new PlayerEventHandler();
        backpackEventHandler = new BackpackEventHandler();
        clientEventHandler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(backpackEventHandler);
        MinecraftForge.EVENT_BUS.register(clientEventHandler);

        MinecraftForge.EVENT_BUS.register(eventlistener);
        FMLCommonHandler.instance().bus().register(eventlistener);

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
        //proxy.registerKeybindings();

        //GUIs
        new GuiHandler();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }

}
