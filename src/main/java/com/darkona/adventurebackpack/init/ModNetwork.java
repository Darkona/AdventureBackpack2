package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.network.*;
import com.darkona.adventurebackpack.network.messages.PlayerParticlePacket;
import com.darkona.adventurebackpack.network.messages.PlayerSoundPacket;
import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Darkona on 12/10/2014.
 */
public class ModNetwork
{
    public static SimpleNetworkWrapper net;
    public static int messages = 0;

    public static void init()
    {
        net = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MOD_CHANNEL);

        registerMessage(SyncPropertiesPacket.class, SyncPropertiesPacket.Message.class);
        registerMessage(PlayerParticlePacket.class, PlayerParticlePacket.Message.class);
        registerMessage(PlayerSoundPacket.class, PlayerSoundPacket.Message.class);

        registerMessage(CopterPacket.class, CopterPacket.CopterMessage.class);
        registerMessage(CycleToolPacket.class, CycleToolPacket.CycleToolMessage.class);
        registerMessage(GUIPacket.class, GUIPacket.GUImessage.class);
        registerMessage(SleepingBagPacket.class, SleepingBagPacket.SleepingBagMessage.class);
        registerMessage(CowAbilityPacket.class, CowAbilityPacket.CowAbilityMessage.class);
        registerMessage(PlayerActionPacket.class, PlayerActionPacket.ActionMessage.class);
        registerMessage(EquipUnequipBackWearablePacket.class, EquipUnequipBackWearablePacket.Message.class);

    }

    public static void registerClientSide(Class handler, Class message)
    {
        net.registerMessage(handler, message, messages, Side.CLIENT);
        messages++;
    }

    private static void registerMessage(Class handler, Class message)
    {
        net.registerMessage(handler, message, messages, Side.CLIENT);
        net.registerMessage(handler, message, messages, Side.SERVER);
        messages++;
    }

    public static void sendToNearby(IMessage message, EntityPlayer player)
    {
        net.sendToAllAround(message, new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 144.0D));
    }

    public static void sendToDimension(IMessage message, EntityPlayer player)
    {
        net.sendToDimension(message, player.dimension);
    }
}
