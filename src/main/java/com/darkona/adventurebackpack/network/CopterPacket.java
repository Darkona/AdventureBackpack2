package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.common.ClientActions;
import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Created by Darkona on 12/10/2014.
 */
public class CopterPacket implements IMessageHandler<CopterPacket.CopterMessage,CopterPacket.CopterMessage>
{
    public static byte ON_OFF = 0;
    public static byte TOGGLE = 1;
    public static byte SOUND = 2;
    @Override
    public CopterPacket.CopterMessage onMessage(CopterMessage message, MessageContext ctx)
    {
        if(ctx.side.isServer())
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if (player != null)
            {
                if(message.type == SOUND){

                    return null;
                }else
                {
                    ServerActions.toggleCopterPack(player, Wearing.getWearingCopter(player),message.type);
                }
            }
        }
        if(ctx.side.isClient())
        {
            if(message.type == SOUND)
            {
                EntityPlayer player = Minecraft.getMinecraft().theWorld.func_152378_a(UUID.fromString(message.playerID));
                EntityClientPlayerMP thisPlayer = Minecraft.getMinecraft().thePlayer;
                //LogHelper.info("player = " + player.getUniqueID());
                //LogHelper.info("player = " + thisPlayer.getUniqueID());
                //if(player.getUniqueID() != thisPlayer.getUniqueID())
                {
                    ClientActions.copterSound(player);
                }
            }
        }
        return null;
    }

    public static class CopterMessage implements IMessage
    {

        private byte type;
        private String playerID;

        public CopterMessage()
        {
        }

        public CopterMessage(byte type, String playerID)
        {
            this.type = type;
            this.playerID = playerID;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            this.type = buf.readByte();
            playerID = ByteBufUtils.readUTF8String(buf);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(type);
            ByteBufUtils.writeUTF8String(buf, playerID);
        }
    }
}
