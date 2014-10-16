package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.misc.NyanMovingSound;
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
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Created on 16/10/2014
 *
 * @author Darkona
 */
public class NyanCatMessage implements IMessage {

    private byte soundCode;
    private String playerID;

    public NyanCatMessage() {
    }

    public NyanCatMessage(byte soundcode, String playerID) {
        this.soundCode = soundcode;
        this.playerID = playerID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        soundCode = buf.readByte();
        playerID = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(soundCode);
        ByteBufUtils.writeUTF8String(buf, playerID);
    }

    public static class PlaySoundMessageServerHandler implements IMessageHandler<NyanCatMessage, NyanCatMessage> {

        @Override
        public NyanCatMessage onMessage(NyanCatMessage message, MessageContext ctx) {
            //LogHelper.info("Message Received on Server for Nyan, player UUID is" + message.playerID);
            // LogHelper.info(message);
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            AdventureBackpack.networkWrapper.sendToAllAround(new NyanCatMessage(message.soundCode, message.playerID),
                    new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 30D));
            return null;
        }
    }

    public static class PlaySoundMessageClientHandler implements IMessageHandler<NyanCatMessage, NyanCatMessage> {

        @Override
        public NyanCatMessage onMessage(NyanCatMessage message, MessageContext ctx) {
            // LogHelper.info("Message Received on Client for Nyan, player UUID is" + message.playerID);
            // LogHelper.info(message);
            EntityPlayer player = Minecraft.getMinecraft().theWorld.func_152378_a(UUID.fromString(message.playerID));
            World world = player.worldObj;
            if (message.soundCode == MessageConstants.PLAY_NYAN) {
                /*ISound nyan = new NyanMovingSound(player);*/
                Minecraft.getMinecraft().getSoundHandler().stopSounds();
                Minecraft.getMinecraft().getSoundHandler().playSound(NyanMovingSound.instance.setPlayer(player));
            }
            if (message.soundCode == MessageConstants.SPAWN_PARTICLE) {
                int i = 1;
                for (int j = 0; j < i * 2; ++j) {
                    float f = world.rand.nextFloat() * (float) Math.PI * 2.0F;
                    float f1 = world.rand.nextFloat() * 0.5F + 0.5F;
                    float f2 = MathHelper.sin(f) * i * 0.5F * f1;
                    float f3 = MathHelper.cos(f) * i * 0.5F * f1;
                    player.worldObj.spawnParticle("note",
                            player.posX + f2,
                            player.boundingBox.minY + 0.8f,
                            player.posZ + f3,
                            (double) world.rand.nextInt(12) / 24.0D,
                            -1.0D,
                            0.0D);
                }
            }
            return null;
        }
    }
}
