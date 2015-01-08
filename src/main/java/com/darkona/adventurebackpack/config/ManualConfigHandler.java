package com.darkona.adventurebackpack.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created on 04/01/2015
 *
 * @author Darkona
 */
public class ManualConfigHandler
{

    public static Configuration config;

    public static void init(File configFile)
    {
        if (config == null)
        {
            config = new Configuration(configFile);
            loadConfiguration();
        }
    }

    private static void loadConfiguration()
    {

        if (config.hasChanged())
        {
            config.save();
        }
    }

}
