package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.inventory.WidgetLayouts;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class ClientConnectionHandler
{
    @SubscribeEvent
    public void disconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        WidgetLayouts.forgetServerSpecs();
    }
}
