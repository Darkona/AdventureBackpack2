package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/**
 * Created by Darkona on 12/10/2014.
 */
public class JumpPacket implements IMessageHandler<JumpPacket.JumpMessage, IMessage>
{


    @Override
    public IMessage onMessage(JumpMessage message, MessageContext ctx)
    {
        if (ctx.side.isServer())
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if (player != null)
            {
                int playerX = (int) player.posX;
                int playerY = (int) player.posY;
                int playerZ = (int) player.posZ;
                World world = player.worldObj;

                ServerActions.copterPackElevate(player, Wearing.getWearingCopter(player));
            }
        }
        return null;
    }

    public static class JumpMessage implements IMessage
    {

        private byte type;

        public JumpMessage()
        {
        }

        public JumpMessage(byte type)
        {
            this.type = type;

        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            this.type = buf.readByte();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(type);
        }
    }
}
