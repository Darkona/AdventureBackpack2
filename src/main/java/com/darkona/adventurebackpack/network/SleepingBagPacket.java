package com.darkona.adventurebackpack.network;

import io.netty.buffer.ByteBuf;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import com.darkona.adventurebackpack.common.ServerActions;

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
            ServerActions.toggleSleepingBag(ctx.getServerHandler().playerEntity, message.isTile, message.x, message.y, message.z);
        }
        return null;
    }

    public static class SleepingBagMessage implements IMessage
    {
        public boolean isTile;
        public int x;
        public int y;
        public int z;

        public SleepingBagMessage()
        {

        }

        public SleepingBagMessage(boolean isTile, int X, int Y, int Z)
        {
            this.isTile = isTile;
            this.x = X;
            this.y = Y;
            this.z = Z;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            isTile = buf.readBoolean();
            x = buf.readInt();
            y = buf.readInt();
            z = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(isTile);
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
        }
    }
}
