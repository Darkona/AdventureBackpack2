package com.darkona.adventurebackpack;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.fluids.FluidEffectRegistry;
import com.darkona.adventurebackpack.handlers.ClientEventHandler;
import com.darkona.adventurebackpack.handlers.GeneralEventHandler;
import com.darkona.adventurebackpack.handlers.GuiHandler;
import com.darkona.adventurebackpack.handlers.PlayerEventHandler;
import com.darkona.adventurebackpack.handlers.TooltipEventHandler;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModEntities;
import com.darkona.adventurebackpack.init.ModFluids;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.init.ModRecipes;
import com.darkona.adventurebackpack.init.ModWorldGen;
import com.darkona.adventurebackpack.proxy.IProxy;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Utils;

/**
 * Created on 10/10/2014
 *
 * @author Javier Darkona
 */
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION, guiFactory = ModInfo.GUI_FACTORY_CLASS,
        dependencies = "required-after:CodeChickenCore@[1.0.7.47,)")
public class AdventureBackpack
{
    @SidedProxy(clientSide = ModInfo.MOD_CLIENT_PROXY, serverSide = ModInfo.MOD_SERVER_PROXY)
    public static IProxy proxy;
    @Mod.Instance(ModInfo.MOD_ID)
    public static AdventureBackpack instance;

    //Static things
    public static CreativeTabAB creativeTab = new CreativeTabAB();

    //public boolean chineseNewYear;
    //public boolean hannukah;
    public String Holiday;
    PlayerEventHandler playerEventHandler;
    ClientEventHandler clientEventHandler;
    GeneralEventHandler generalEventHandler;
    TooltipEventHandler tooltipEventHandler;

    GuiHandler guiHandler;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //int year = Calendar.getInstance().get(Calendar.YEAR), month = Calendar.getInstance().get(Calendar.MONTH) + 1, day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        //Configuration
        FMLCommonHandler.instance().bus().register(new ConfigHandler());
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        //chineseNewYear = ChineseCalendar.isChineseNewYear(year, month, day);
        //hannukah = JewishCalendar.isHannukah(year, month, day);
        Holiday = Utils.getHoliday();

        //ModStuff
        ModItems.init();
        ModBlocks.init();
        ModFluids.init();
        FluidEffectRegistry.init();
        ModEntities.init();
        ModNetwork.init();
        proxy.initNetwork();

        //EVENTS
        playerEventHandler = new PlayerEventHandler();
        generalEventHandler = new GeneralEventHandler();
        clientEventHandler = new ClientEventHandler();
        tooltipEventHandler = new TooltipEventHandler();

        MinecraftForge.EVENT_BUS.register(generalEventHandler);
        MinecraftForge.EVENT_BUS.register(clientEventHandler);
        MinecraftForge.EVENT_BUS.register(playerEventHandler);
        MinecraftForge.EVENT_BUS.register(tooltipEventHandler);

        FMLCommonHandler.instance().bus().register(playerEventHandler);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
        ModRecipes.init();
        ModWorldGen.init();

        //GUIs
        guiHandler = new GuiHandler();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        ConfigHandler.IS_DEVENV = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        if (ConfigHandler.IS_DEVENV)
            LogHelper.info("Dev environment detected. All hail the creator");

        ConfigHandler.IS_BUILDCRAFT = Loader.isModLoaded("BuildCraft|Core");
        ConfigHandler.IS_ENDERIO = Loader.isModLoaded("EnderIO");

        if (ConfigHandler.IS_BUILDCRAFT)
            LogHelper.info("Buildcraft is present. Acting accordingly");
        if (ConfigHandler.IS_ENDERIO)
            LogHelper.info("EnderIO is present. Acting accordingly");

        //ConditionalFluidEffect.init();
        //ModItems.conditionalInit();
        ModRecipes.conditionalInit();

        /*
        LogHelper.info("DUMPING FLUID INFORMATION");
        LogHelper.info("-------------------------------------------------------------------------");
        for(Fluid fluid : FluidRegistry.getRegisteredFluids().values())
        {

            LogHelper.info("Unlocalized name: " + fluid.getUnlocalizedName());
            LogHelper.info("Name: " + fluid.getName());
            LogHelper.info("");
        }
        LogHelper.info("-------------------------------------------------------------------------");
        */
        /*
        LogHelper.info("DUMPING TILE INFORMATION");
        LogHelper.info("-------------------------------------------------------------------------");
        for (Block block : GameData.getBlockRegistry().typeSafeIterable())
        {
            LogHelper.info("Block= " + block.getUnlocalizedName());
        }
        LogHelper.info("-------------------------------------------------------------------------");
        */
    }
}
