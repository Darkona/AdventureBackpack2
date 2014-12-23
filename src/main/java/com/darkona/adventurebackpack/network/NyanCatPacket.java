package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.common.ClientActions;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

/**
 * Created on 16/10/2014
 *
 * @author Darkona
 */
public class NyanCatPacket  implements IMessageHandler<NyanCatPacket.NyanCatMessage, IMessage>
{

    public static final byte PLAY_NYAN = 0;
    public static final byte STOP_NYAN = 2;
    public static final byte SPAWN_PARTICLE = 1;

    @Override
    public IMessage onMessage(NyanCatMessage message, MessageContext ctx)
    {
        if(ctx.side.isServer())
        {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            if (message.soundCode == PLAY_NYAN)
            {
                Wearing.getWearingBackpack(player).getTagCompound().setInteger("lastTime", Utils.secondsToTicks(150));
            }
            return null;
        }

        if(ctx.side.isClient())
        {
            EntityPlayer player = Minecraft.getMinecraft().theWorld.func_152378_a(UUID.fromString(message.playerID));

            ClientActions.awesomeness(player, message.soundCode);

            return null;
        }
            return null;
    }



    public static class NyanCatMessage implements IMessage
    {
        private byte soundCode;
        private String playerID;

        public NyanCatMessage()
        {
        }

        public NyanCatMessage(byte soundcode, String playerID)
        {
            this.soundCode = soundcode;
            this.playerID = playerID;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            soundCode = buf.readByte();
            playerID = ByteBufUtils.readUTF8String(buf);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(soundCode);
            ByteBufUtils.writeUTF8String(buf, playerID);
        }
    }
}
