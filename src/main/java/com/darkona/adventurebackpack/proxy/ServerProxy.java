package com.darkona.adventurebackpack.proxy;

import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 22/12/2014
 *
 * @author Darkona
 */
public class ServerProxy implements IProxy
{
    private static final Map<String, NBTTagCompound> extendedEntityData = new HashMap<String, NBTTagCompound>();

    @Override
    public void init()
    {

    }

    @Override
    public void registerKeybindings()
    {

    }

    @Override
    public void initNetwork()
    {

    }

    public static void storePlayerProps(String name, NBTTagCompound compound)
    {
        extendedEntityData.put(name, compound);
    }


    public static NBTTagCompound extractPlayerProps(String name)
    {
        return extendedEntityData.remove(name);
    }
}
