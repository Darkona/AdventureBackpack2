package com.darkona.adventurebackpack.util;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;

import com.darkona.adventurebackpack.reference.ModInfo;

/**
 * Created by Darkona on 12/10/2014.
 */
public class LogHelper
{
    public static void log(Level logLevel, Object object)
    {
        FMLLog.log(ModInfo.MOD_NAME, logLevel, String.valueOf(object));
    }

    public static void debug(Object object)
    {
        log(Level.DEBUG, object);
    }

    public static void error(Object object)
    {
        log(Level.ERROR, object);
    }

    public static void warn(Object object)
    {
        log(Level.WARN, object);
    }

    public static void info(Object object)
    {
        log(Level.INFO, object);
    }
}
