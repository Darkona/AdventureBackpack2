package com.darkona.adventurebackpack.client;

import com.darkona.adventurebackpack.client.audio.BoilingBoilerSound;
import com.darkona.adventurebackpack.client.audio.CopterPackSound;
import com.darkona.adventurebackpack.client.audio.JetpackSoundOn;
import com.darkona.adventurebackpack.client.audio.LeakingBoilerSound;
import com.darkona.adventurebackpack.client.audio.NyanMovingSound;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.network.messages.EntityParticlePacket;
import com.darkona.adventurebackpack.network.messages.EntitySoundPacket;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

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
    public static void showParticlesAtEntity(Entity entity, byte particleCode)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            switch (particleCode)
            {
                case EntityParticlePacket.COPTER_PARTICLE:
                    Visuals.CopterParticles(player, player.worldObj);
                    break;
                case EntityParticlePacket.NYAN_PARTICLE:
                    Visuals.NyanParticles(player, player.worldObj);
                    break;
                case EntityParticlePacket.SLIME_PARTICLE:
                    Visuals.SlimeParticles(player, player.worldObj);
                    break;
                case EntityParticlePacket.JETPACK_PARTICLE:
                    Visuals.JetpackParticles(player, player.worldObj);
                    break;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playSoundAtEntity(Entity entity, byte soundCode)
    {
        SoundHandler snd = FMLClientHandler.instance().getClient().getSoundHandler();
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            switch (soundCode)
            {
                case EntitySoundPacket.COPTER_SOUND:
                    if (ConfigHandler.allowSoundCopter)
                    {
                        snd.playSound(new CopterPackSound(player));
                    }
                    break;
                case EntitySoundPacket.NYAN_SOUND:
                    snd.playSound(new NyanMovingSound(player));
                    break;
                case EntitySoundPacket.JETPACK_FIZZ:
                    if (ConfigHandler.allowSoundJetpack)
                    {
                	snd.playSound(new JetpackSoundOn(player));
                    }
                    break;
                case EntitySoundPacket.BOILING_BUBBLES:
                    snd.playSound(new BoilingBoilerSound(player));
                    break;
                case EntitySoundPacket.LEAKING_STEAM:
                    snd.playSound(new LeakingBoilerSound(player));
                    break;
            }
        }
    }

}
