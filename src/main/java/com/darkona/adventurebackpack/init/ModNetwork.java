package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.network.*;
import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/**
 * Created by Darkona on 12/10/2014.
 */
public class ModNetwork
{
    public static SimpleNetworkWrapper net;
    public static int messages = 0;

    public static void init(){
        net = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MOD_CHANNEL);
        registerMessage(CycleToolPacket.class,CycleToolPacket.CycleToolMessage.class);
        registerMessage(GUIPacket.class, GUIPacket.GUImessage.class);
        registerMessage(NyanCatPacket.class, NyanCatPacket.NyanCatMessage.class);
        registerMessage(SleepingBagPacket.class, SleepingBagPacket.SleepingBagMessage.class );
        registerMessage(CowAbilityPacket.class, CowAbilityPacket.CowAbilityMessage.class);
    }

    private static void registerMessage(Class handler, Class message)
    {
        net.registerMessage(handler, message, messages, Side.CLIENT);
        net.registerMessage(handler, message, messages, Side.SERVER);
        messages++;
    }
}
