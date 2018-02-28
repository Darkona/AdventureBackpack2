package com.darkona.adventurebackpack.reference;

import net.minecraft.launchwrapper.Launch;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

import com.darkona.adventurebackpack.util.LogHelper;

/**
 * Created on 24.02.2018
 *
 * @author Ugachaga
 */
public final class LoadedMods
{
    public static final boolean DEV_ENV = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    public static final boolean BUILDCRAFT = registerMod("BuildCraft|Core");
    public static final boolean ENDERIO = registerMod("EnderIO");
    public static final boolean GREGTECH = registerMod("gregtech");
    public static final boolean NEI = registerMod("NotEnoughItems");
    public static final boolean TCONSTRUCT = registerMod("TConstruct");
    public static final boolean THAUMCRAFT = registerMod("Thaumcraft");

    private LoadedMods() {}

    public static void init()
    {
        if (DEV_ENV)
        {
            LogHelper.info("Dev environment detected. All hail the creator");
        }
    }

    private static boolean registerMod(String modID)
    {
        if (!Loader.isModLoaded(modID))
            return false;

        String modName = modID;
        for (ModContainer mod : Loader.instance().getModList())
        {
            if (mod.getModId().equals(modID))
                modName = mod.getName();
        }
        LogHelper.info(modName + " is present. Acting accordingly");
        return true;
    }
}
