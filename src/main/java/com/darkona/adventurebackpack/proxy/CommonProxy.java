package com.darkona.adventurebackpack.proxy;

import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;

public abstract class CommonProxy implements IProxy
{
    private EntityPlayerMP player;

    public void serverTick(PlayerTickEvent event)
    {
        {
            if (event.side == Side.SERVER)
            {
                setPlayer((EntityPlayerMP) event.player);
            }
        }

    }

    public EntityPlayerMP getPlayer()
    {
        return player;
    }

    public void setPlayer(EntityPlayerMP player)
    {
        this.player = player;
    }
}