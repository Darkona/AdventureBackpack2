package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.misc.NyanMovingSound;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

/**
 * Created on 16/10/2014
 *
 * @author Darkona
 */
public class PlaySoundMessage implements IMessage {

    private byte soundCode;
    /*private double posX;
    private double posY;
    private double posZ;*/
    private String playerID;

    public PlaySoundMessage() {
    }

    public PlaySoundMessage(byte soundcode, /*double X, double Y, double Z,*/String playerID) {
        this.soundCode = soundcode;
        /*this.posX = X;
        this.posY = Y;
        this.posZ = Z;*/
        this.playerID = playerID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        soundCode = buf.readByte();
        /*posX = buf.readDouble();
        posY = buf.readDouble();
        posZ = buf.readDouble();*/
        playerID = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(soundCode);
       /* buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);*/
        ByteBufUtils.writeUTF8String(buf, playerID);
    }

    public static class PlaySoundMessageServerHandler implements IMessageHandler<PlaySoundMessage, PlaySoundMessage> {

        @Override
        public PlaySoundMessage onMessage(PlaySoundMessage message, MessageContext ctx) {
            LogHelper.info("Message Received on Server for Nyan, player UUID is" + message.playerID);
            LogHelper.info(message);
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            AdventureBackpack.networkWrapper.sendToAllAround(new PlaySoundMessage(message.soundCode, message.playerID),
                    new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 30D));
            Wearing.getWearingBackpack(ctx.getServerHandler().playerEntity).getTagCompound().setInteger("lastTime", Utils.secondsToTicks(150));

            return null;
        }
    }

    public static class PlaySoundMessageClientHandler implements IMessageHandler<PlaySoundMessage, PlaySoundMessage> {

        @Override
        public PlaySoundMessage onMessage(PlaySoundMessage message, MessageContext ctx) {
            LogHelper.info("Message Received on Client for Nyan, player UUID is" + message.playerID);
            LogHelper.info(message);
            if (message.soundCode == MessageConstants.PLAY_NYAN) {
                ISound nyan = new NyanMovingSound(Minecraft.getMinecraft().theWorld.func_152378_a(UUID.fromString(message.playerID)));
                Minecraft.getMinecraft().getSoundHandler().stopSounds();
                Minecraft.getMinecraft().getSoundHandler().playSound(nyan);
            }
            return null;
        }
    }
}
