package com.darkona.adventurebackpack.client;

import com.darkona.adventurebackpack.client.audio.*;
import com.darkona.adventurebackpack.common.BoatMotorSound;
import com.darkona.adventurebackpack.common.FlamethrowerSound;
import com.darkona.adventurebackpack.inventory.InventoryFlamethrowerPack;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
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
import net.minecraft.item.ItemStack;

public class ClientActions
{
    @SideOnly(Side.CLIENT)
    public static boolean isLocalPlayer(EntityPlayer player)
    {
        return player != null && player == net.minecraft.client.Minecraft.getMinecraft().thePlayer;
    }

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

    @SideOnly(Side.CLIENT)
    public static void flamethrowerSounds(EntityPlayer player, ItemStack pack)
    {
        boolean lit = InventoryFlamethrowerPack.isLit(pack);
        boolean firing = lit && FlamethrowerLoopSound.isFiring(player);
        int events = BackpackProperty.get(player).getFlameSound().update(player.ticksExisted, lit, firing);
        if (events == 0) return;
        float master = FlamethrowerSound.master(ConfigHandler.RENDER_FLAMETHROWER_VOLUME);
        if (!FlamethrowerSound.audible(ConfigHandler.FLAMETHROWER_SOUND, master)) return;
        if ((events & FlamethrowerSound.IGNITE) != 0)
        {
            playAt(player, FlamethrowerSound.IGNITE_SOUND, FlamethrowerSound.IGNITE_VOLUME * master, FlamethrowerSound.IGNITE_PITCH);
        }
        if ((events & FlamethrowerSound.PUT_OUT) != 0)
        {
            if (!InventoryFlamethrowerPack.hasFuel(pack))
            {
                playAt(player, FlamethrowerSound.NO_FUEL_SOUND, FlamethrowerSound.NO_FUEL_VOLUME * master, FlamethrowerSound.NO_FUEL_PITCH);
            } else
            {
                playAt(player, FlamethrowerSound.PUT_OUT_SOUND, FlamethrowerSound.PUT_OUT_VOLUME * master, FlamethrowerSound.PUT_OUT_PITCH);
            }
        }
        if ((events & FlamethrowerSound.FIRE_START) != 0)
        {
            playAt(player, FlamethrowerSound.FIRE_START_SOUND, FlamethrowerSound.FIRE_START_VOLUME * master, FlamethrowerSound.FIRE_START_PITCH);
            FMLClientHandler.instance().getClient().getSoundHandler().playSound(new FlamethrowerLoopSound(player));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void flamethrowerDryClick(EntityPlayer player)
    {
        float master = FlamethrowerSound.master(ConfigHandler.RENDER_FLAMETHROWER_VOLUME);
        if (!FlamethrowerSound.audible(ConfigHandler.FLAMETHROWER_SOUND, master)) return;
        playAt(player, FlamethrowerSound.DRY_SOUND, FlamethrowerSound.DRY_VOLUME * master, FlamethrowerSound.DRY_PITCH);
    }

    @SideOnly(Side.CLIENT)
    private static void playAt(EntityPlayer player, String sound, float volume, float pitch)
    {
        player.worldObj.playSound(player.posX, player.posY + 1.0D, player.posZ, sound, volume, pitch, false);
    }
}
