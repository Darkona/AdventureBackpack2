package com.darkona.adventurebackpack.proxy;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created on 14/10/2014
 *
 * @author Darkona
 */
public interface IProxy
{
    void init();

    void registerKeybindings();

    void initNetwork();

    void synchronizePlayer(int id, NBTTagCompound compound);
}
