package com.darkona.adventurebackpack.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IProxy
{
    public void init();

    public void registerKeybindings();

    void initNetwork();

    public void joinPlayer(EntityPlayer player);

    public void synchronizePlayer(int id, NBTTagCompound compound);
}
