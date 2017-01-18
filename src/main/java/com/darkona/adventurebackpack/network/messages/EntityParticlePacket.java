package com.darkona.adventurebackpack.network.messages;

import com.darkona.adventurebackpack.client.ClientActions;
import com.darkona.adventurebackpack.init.ModNetwork;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

/**
 * Created on 06/01/2015
 *
 * @author Darkona
 */
public class EntityParticlePacket implements IMessageHandler<EntityParticlePacket.Message, EntityParticlePacket.Message>
{
    public static final byte NYAN_PARTICLE = 0;
    public static final byte COPTER_PARTICLE = 1;
    public static final byte SLIME_PARTICLE = 2;
    public static final byte JETPACK_PARTICLE = 3;

    @Override
    public Message onMessage(Message message, MessageContext ctx)
    {
        if (ctx.side.isClient())
        {
            Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
            ClientActions.showParticlesAtEntity(entity, message.particleCode);
        } else
        {
            ModNetwork.sendToNearby(message, ctx.getServerHandler().playerEntity);
        }
        return null;
    }

    public static class Message implements IMessage
    {

        private byte particleCode;
        private int entityID;

        public Message(byte particleCode, Entity entity)
        {
            this.particleCode = particleCode;
            this.entityID = entity.getEntityId();
        }

        public Message()
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            particleCode = buf.readByte();
            entityID = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(particleCode);
            buf.writeInt(entityID);
        }
    }
}
