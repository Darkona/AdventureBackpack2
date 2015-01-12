package com.darkona.adventurebackpack.proxy;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Created on 14/10/2014
 *
 * @author Darkona
 */
public interface IProxy
{

    public void init();

    public void registerKeybindings();

    void initNetwork();

    public void joinPlayer(EntityPlayer player);

}
