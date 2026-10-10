package com.darkona.adventurebackpack.client;

import com.darkona.adventurebackpack.client.audio.*;
import com.darkona.adventurebackpack.common.BoatMotorSound;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import com.darkona.adventurebackpack.network.messages.EntityParticlePacket;
import com.darkona.adventurebackpack.network.messages.EntitySoundPacket;
import com.darkona.adventurebackpack.reference.ModInfo;
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
    /** True for this client's own player; wearable updates also run for the other players a client sees. */
    @SideOnly(Side.CLIENT)
    public static boolean isLocalPlayer(EntityPlayer player)
    {
        return player != null && player == net.minecraft.client.Minecraft.getMinecraft().thePlayer;
    }

    /** True while this client holds the jump key: the copter and the jetpack climb with it. */
    @SideOnly(Side.CLIENT)
    public static boolean isJumpKeyDown()
    {
        return net.minecraft.client.Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed();
    }

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
                    if (ConfigHandler.ALLOW_COPTER_SOUND)
                    {
                        snd.playSound(new CopterPackSound(player));
                    }
                    break;
                case EntitySoundPacket.NYAN_SOUND:
                    if (ConfigHandler.RAINBOW_SOUND) snd.playSound(new NyanMovingSound(player));
                    break;
                case EntitySoundPacket.JETPACK_FIZZ:
                    if (ConfigHandler.ALLOW_JETPACK_SOUNDS) snd.playSound(new JetpackSoundOn(player));
                    break;
                case EntitySoundPacket.BOILING_BUBBLES:
                    if (ConfigHandler.ALLOW_JETPACK_SOUNDS) snd.playSound(new BoilingBoilerSound(player));
                    break;
                case EntitySoundPacket.LEAKING_STEAM:
                    if (ConfigHandler.ALLOW_JETPACK_SOUNDS) snd.playSound(new LeakingBoilerSound(player));
                    break;
            }
        }
    }

    /**
     * The boat motor's sounds (inflatableboat.motor.sound), on the change of its running flag: START plays the cord pull
     * once and begins the loop that follows the boat; STOP plays the cough once at the boat. Called only on the client.
     */
    @SideOnly(Side.CLIENT)
    public static void boatMotorSound(EntityInflatableBoat boat, BoatMotorSound.Change change)
    {
        float volume = BoatMotorSound.master(ConfigHandler.BOAT_MOTOR_SOUND_VOLUME);
        if (volume <= 0.0F) return;
        if (change == BoatMotorSound.Change.START)
        {
            if (BoatMotorSound.pullAudible(boat.ticksExisted))
            {
                boat.worldObj.playSound(boat.posX, boat.posY, boat.posZ, ModInfo.MOD_ID + ":boatmotorstart", volume, 1.0F, false);
            }
            FMLClientHandler.instance().getClient().getSoundHandler().playSound(new BoatMotorLoopSound(boat));
        } else if (change == BoatMotorSound.Change.STOP)
        {
            boat.worldObj.playSound(boat.posX, boat.posY, boat.posZ, ModInfo.MOD_ID + ":boatmotorstop", volume, 1.0F, false);
        }
    }

}
