package com.darkona.adventurebackpack.network.messages;

import com.darkona.adventurebackpack.client.ClientActions;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.network.RelayGuard;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created on 06/01/2015
 *
 * @author Darkona
 */
public class EntitySoundPacket implements IMessageHandler<EntitySoundPacket.Message, EntitySoundPacket.Message>
{

    public static final boolean play = true;

    public static final byte NYAN_SOUND = 0;
    public static final byte COPTER_SOUND = 1;
    public static final byte JETPACK_FIZZ = 2;
    public static final byte BOILING_BUBBLES = 3;
    public static final byte LEAKING_STEAM = 4;

    @Override
    public Message onMessage(Message message, MessageContext ctx)
    {
        if (ctx.side.isClient())
        {
            if (Minecraft.getMinecraft().theWorld == null) return null;
            ClientActions.playSoundAtEntity(Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID), message.soundCode);
        } else
        {
            // A client request: only the jetpack ignition of the sender itself is relayed (RelayGuard)
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (RelayGuard.mayRelaySound(player, message.entityID, message.soundCode))
            {
                ModNetwork.sendToNearby(message, player);
            }
        }
        return null;
    }

    public static class Message implements IMessage
    {


        private byte soundCode;
        private int entityID;

        public Message(byte soundCode, Entity entity)
        {
            this.soundCode = soundCode;
            this.entityID = entity.getEntityId();
        }

        public Message()
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            soundCode = buf.readByte();
            entityID = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(soundCode);
            buf.writeInt(entityID);
        }
    }
}
