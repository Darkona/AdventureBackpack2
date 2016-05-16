package com.darkona.adventurebackpack.proxy;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.relauncher.Side;
public abstract class CommonProxy implements IProxy {
    private EntityPlayerMP player;
	@Override
    public void registerHandlers() {
        
        Object eventHandler = null;
		FMLCommonHandler.instance().bus().register(eventHandler);
        MinecraftForge.EVENT_BUS.register(eventHandler);
    }
        public void serverTick(PlayerTickEvent event) {
             {
                if(event.side == Side.SERVER) {
                    setPlayer((EntityPlayerMP) event.player);
                }
    }
    
 }
		public EntityPlayerMP getPlayer() {
			return player;
		}
		public void setPlayer(EntityPlayerMP player) {
			this.player = player;
		}
		  public void cape() {}
}