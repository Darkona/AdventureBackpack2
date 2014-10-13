package com.darkona.adventurebackpack.config;

import com.darkona.adventurebackpack.references.ModInfo;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Darkona on 10/10/2014.
 */
public class ConfigHandler {

    public static Configuration config;
    public static boolean testValue = false;

    public static void init(File configFile){
        if( config == null){
            config = new Configuration(configFile);
            loadConfiguration();
        }
    }


    private static void loadConfiguration(){
        testValue = config.getBoolean("testValue",config.CATEGORY_GENERAL, false, "This is a test configuration value");

        if (config.hasChanged()){
            config.save();
        }
    }

    @SubscribeEvent
    public void onConfigChangeEvent(ConfigChangedEvent.OnConfigChangedEvent event){
        if (event.modID.equalsIgnoreCase(ModInfo.MOD_ID)){
            loadConfiguration();
        }
    }



}
