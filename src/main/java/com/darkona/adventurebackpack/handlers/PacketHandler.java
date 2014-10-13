package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.network.EchoMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Darkona on 12/10/2014.
 */
public class PacketHandler implements IMessageHandler<EchoMessage, IMessage> {

    @Override
    public IMessage onMessage(EchoMessage message, MessageContext ctx) {
        System.out.println(message.salutation);
        return null;
    }
}
