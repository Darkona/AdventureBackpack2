package com.darkona.adventurebackpack.network;

import io.netty.buffer.ByteBuf;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.config.ConfigHandler;

/**
 * Created on 19/10/2014
 *
 * @author Darkona
 */
public class SleepingBagPacket implements IMessageHandler<SleepingBagPacket.SleepingBagMessage, IMessage>
{
    @Override
    public IMessage onMessage(SleepingBagMessage message, MessageContext ctx)
    {
        if (ctx.side.isServer())
        {
            if (message.isTile || ConfigHandler.allowPortableSleepingBag) // serverside check
            {
                ServerActions.toggleSleepingBag(ctx.getServerHandler().playerEntity, message.isTile, message.cX, message.cY, message.cZ);
            }
        }
        return null;
    }

    public static class SleepingBagMessage implements IMessage
    {
        private boolean isTile;
        private int cX;
        private int cY;
        private int cZ;

        public SleepingBagMessage()
        {

        }

        public SleepingBagMessage(boolean isTile, int cX, int cY, int cZ)
        {
            this.isTile = isTile;
            this.cX = cX;
            this.cY = cY;
            this.cZ = cZ;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            isTile = buf.readBoolean();
            cX = buf.readInt();
            cY = buf.readInt();
            cZ = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(isTile);
            buf.writeInt(cX);
            buf.writeInt(cY);
            buf.writeInt(cZ);
        }
    }
}
