package com.darkona.adventurebackpack.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Created by Darkona on 12/10/2014.
 */
public class GuiBackpackMessage implements IMessage {

    public final int OPEN_GUI = 0;
    public final int CLOSE_GUI = 1;
    public final int BUTTON_CRAFT = 2;


    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public class GuiBackpackMessageServerHandler implements IMessageHandler<GuiBackpackMessage, IMessage> {

        @Override
        public IMessage onMessage(GuiBackpackMessage message, MessageContext ctx) {
            return null;
        }
    }

    public class GuiBackpackMessageClientHandler implements IMessageHandler<GuiBackpackMessage, IMessage> {

        @Override
        public IMessage onMessage(GuiBackpackMessage message, MessageContext ctx) {
            return null;
        }
    }
}
