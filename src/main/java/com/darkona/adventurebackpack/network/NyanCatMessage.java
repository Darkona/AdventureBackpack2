package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.client.Visuals;
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
import net.minecraft.client.audio.SoundHandler;
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

    public static class NyanCatMessageServerHandler implements IMessageHandler<NyanCatMessage, NyanCatMessage> {

        @Override
        public NyanCatMessage onMessage(NyanCatMessage message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            World world = player.getEntityWorld();
            if (message.soundCode == MessageConstants.PLAY_NYAN) {
                Wearing.getWearingBackpack(player).getTagCompound().setInteger("lastTime", Utils.secondsToTicks(150));
            }
            return null;
        }


    }

    public static class NyanCatMessageClientHandler implements IMessageHandler<NyanCatMessage, NyanCatMessage> {

        @Override
        public NyanCatMessage onMessage(NyanCatMessage message, MessageContext ctx) {
            EntityPlayer player = Minecraft.getMinecraft().theWorld.func_152378_a(UUID.fromString(message.playerID));
            SoundHandler snd = Minecraft.getMinecraft().getSoundHandler();
            NyanMovingSound nyaaan = new NyanMovingSound(player);
            if (message.soundCode == MessageConstants.PLAY_NYAN) {
                /*ISound nyan = new NyanMovingSound(player);*/

                if (snd.isSoundPlaying(nyaaan)) {
                    snd.stopSound(nyaaan);
                }

                snd.playSound(nyaaan);
            }
            if (message.soundCode == MessageConstants.STOP_NYAN) {
                if (snd.isSoundPlaying(nyaaan))
                    snd.stopSound(nyaaan);
            }
            if (message.soundCode == MessageConstants.SPAWN_PARTICLE) {
                Visuals.NyanParticles(player);
            }
            return null;
        }
    }
}
