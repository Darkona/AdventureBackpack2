package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.common.ServerActions;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

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
        if(ctx.side.isServer())
        {
            ServerActions.toggleSleepingBag(ctx.getServerHandler().playerEntity, message.x, message.y, message.z);
        }
        return null;
    }



    public static class SleepingBagMessage implements IMessage
    {

        public int x;
        public int y;
        public int z;

        public SleepingBagMessage()
        {
        }

        public SleepingBagMessage(int X, int Y, int Z)
        {
            this.x = X;
            this.y = Y;
            this.z = Z;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            x = buf.readInt();
            y = buf.readInt();
            z = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
        }

    }
}
