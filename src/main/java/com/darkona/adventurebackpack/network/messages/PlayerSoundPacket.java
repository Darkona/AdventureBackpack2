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
public class PlayerSoundPacket implements IMessageHandler<PlayerSoundPacket.Message, PlayerSoundPacket.Message>
{

    public static Message makeCopterMessage(EntityPlayer player, boolean action)
    {
        return new Message(COPTER_SOUND, player.getPersistentID().toString(), action);
    }

    public static Message makeNyanMessage(EntityPlayer player, boolean action)
    {
        return new Message(NYAN_SOUND, player.getPersistentID().toString(), action);
    }

    public static final boolean play = true;
    public static final boolean stop = false;

    public static final byte NYAN_SOUND = 0;
    public static final byte COPTER_SOUND = 1;
    public static final byte JETPACK_FIZZ = 2;


    @Override
    public Message onMessage(Message message, MessageContext ctx)
    {
        if (ctx.side.isClient())
        {

            EntityPlayer player = Minecraft.getMinecraft().theWorld.func_152378_a(UUID.fromString(message.playerUUID));
            if (message.action)
            {
                ClientActions.playSoundAtPlayer(player, message.soundCode);
            } else
            {

            }

        }
        return null;
    }

    public static class Message implements IMessage
    {


        private byte soundCode;
        private String playerUUID;
        private boolean action;

        public Message(byte soundCode, String playerUUID, boolean action)
        {
            this.soundCode = soundCode;
            this.playerUUID = playerUUID;
            this.action = action;
        }

        public Message()
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            soundCode = buf.readByte();
            playerUUID = ByteBufUtils.readUTF8String(buf);
            action = buf.readBoolean();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(soundCode);
            ByteBufUtils.writeUTF8String(buf, playerUUID);
            buf.writeBoolean(action);
        }
    }
}
