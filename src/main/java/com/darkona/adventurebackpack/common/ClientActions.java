package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.client.Visuals;
import com.darkona.adventurebackpack.client.audio.CopterPackSound;
import com.darkona.adventurebackpack.client.audio.NyanMovingSound;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.network.messages.PlayerParticlePacket;
import com.darkona.adventurebackpack.network.messages.PlayerSoundPacket;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created on 11/10/2014
 *
 * @author Darkona
 * @see com.darkona.adventurebackpack.handlers.PlayerEventHandler
 * @see com.darkona.adventurebackpack.fluids.FluidEffectRegistry
 * @see com.darkona.adventurebackpack.common.BackpackAbilities
 */
public class ClientActions
{

    @SideOnly(Side.CLIENT)
    public static void showParticlesAtPlayer(EntityPlayer player, byte particleCode)
    {
        switch (particleCode)
        {
            case PlayerParticlePacket.COPTER_PARTICLE:
                Visuals.CopterParticles(player, player.worldObj);
                break;
            case PlayerParticlePacket.NYAN_PARTICLE:
                Visuals.NyanParticles(player, player.worldObj);
                break;
            case PlayerParticlePacket.SLIME_PARTICLE:
                Visuals.SlimeParticles(player, player.worldObj);
                break;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playSoundAtPlayer(EntityPlayer player, byte soundCode)
    {
        SoundHandler snd = FMLClientHandler.instance().getClient().getSoundHandler();

        switch (soundCode)
        {
            case PlayerSoundPacket.COPTER_SOUND:
                if (ConfigHandler.ALLOW_COPTER_SOUND)
                {
                    CopterPackSound tucutucu = new CopterPackSound(player);
                    Minecraft.getMinecraft().getSoundHandler().playSound(tucutucu);
                }
                break;
            case PlayerSoundPacket.NYAN_SOUND:
                NyanMovingSound nyaaan = new NyanMovingSound(player);
                Minecraft.getMinecraft().getSoundHandler().playSound(nyaaan);

                break;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void synchronizePlayer(NBTTagCompound properties)
    {
        BackpackProperty.get(Minecraft.getMinecraft().thePlayer).loadNBTData(properties);
    }
}
