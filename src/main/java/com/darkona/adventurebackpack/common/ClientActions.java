package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.client.Visuals;
import com.darkona.adventurebackpack.client.audio.CopterPackSound;
import com.darkona.adventurebackpack.client.audio.NyanMovingSound;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.network.NyanCatPacket;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.SoundHandler;
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
    public static SoundHandler snd = FMLClientHandler.instance().getClient().getSoundHandler();

    @SideOnly(Side.CLIENT)
    public static void awesomeness(EntityPlayer player, byte code)
    {
        Wearing.getWearingBackpack(player).getTagCompound().setInteger("lastTime", Utils.secondsToTicks(150));
        NyanMovingSound nyaaan = new NyanMovingSound(player);
        if (code == NyanCatPacket.PLAY_NYAN)
        {
            snd.playSound(nyaaan);
        }
        if (code == NyanCatPacket.STOP_NYAN)
        {
            nyaaan.setDonePlaying();
        }
        if (code == NyanCatPacket.SPAWN_PARTICLE)
        {
            Visuals.NyanParticles(player, player.getEntityWorld());
        }
    }

    @SideOnly(Side.CLIENT)
    public static void copterSound(EntityPlayer player)
    {
        if(ConfigHandler.ALLOW_COPTER_SOUND)
        {
            CopterPackSound tucutucu = new CopterPackSound(player);
            snd.stopSounds();
            LogHelper.info("playing tucutucu");
            snd.playSound(tucutucu);
        }
    }
}
