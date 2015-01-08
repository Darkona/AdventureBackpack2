package com.darkona.adventurebackpack.network.messages;

import com.darkona.adventurebackpack.common.ClientActions;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

/**
 * Created on 06/01/2015
 *
 * @author Darkona
 */
public class PlayerParticlePacket implements IMessageHandler<PlayerParticlePacket.Message, PlayerParticlePacket.Message>
{
    public static final byte NYAN_PARTICLE = 0;
    public static final byte COPTER_PARTICLE = 1;
    public static final byte SLIME_PARTICLE = 2;


    @Override
    public Message onMessage(Message message, MessageContext ctx)
    {
        if (ctx.side.isClient())
        {
            EntityPlayer player = Minecraft.getMinecraft().theWorld.func_152378_a(UUID.fromString(message.playerUUID));
            ClientActions.showParticlesAtPlayer(player, message.particleCode);
        }
        return null;
    }

    public static class Message implements IMessage
    {

        private byte particleCode;
        private String playerUUID;

        public Message(byte particleCode, String playerUUID)
        {
            this.particleCode = particleCode;
            this.playerUUID = playerUUID;
        }

        public Message()
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            particleCode = buf.readByte();
            playerUUID = ByteBufUtils.readUTF8String(buf);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(particleCode);
            ByteBufUtils.writeUTF8String(buf, playerUUID);
        }
    }
}
