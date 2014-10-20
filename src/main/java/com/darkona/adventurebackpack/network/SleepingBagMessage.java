package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.common.Actions;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Created on 19/10/2014
 *
 * @author Darkona
 */
public class SleepingBagMessage implements IMessage
{

    public int x;
    public int y;
    public int z;

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    public SleepingBagMessage()
    {
    }

    public SleepingBagMessage(int X, int Y, int Z)
    {
        this.x = X;
        this.y = Y;
        this.z = Z;
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public static class SleepingBagMessageServerHandler implements IMessageHandler<SleepingBagMessage, IMessage>
    {


        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(SleepingBagMessage message, MessageContext ctx)
        {
            Actions.toggleSleepingBag(ctx.getServerHandler().playerEntity, message.x, message.y, message.z);
            return null;
        }
    }
}
