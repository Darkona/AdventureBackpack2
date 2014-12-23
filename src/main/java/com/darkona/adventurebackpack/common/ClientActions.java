package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.client.Visuals;
import com.darkona.adventurebackpack.misc.NyanMovingSound;
import com.darkona.adventurebackpack.network.NyanCatPacket;
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
 * @see com.darkona.adventurebackpack.api.FluidEffectRegistry
 * @see com.darkona.adventurebackpack.common.BackpackAbilities
 */
public class ClientActions
{

    @SideOnly(Side.CLIENT)
    public static void awesomeness(EntityPlayer player, byte code)
    {
        Wearing.getWearingBackpack(player).getTagCompound().setInteger("lastTime", Utils.secondsToTicks(150));
        SoundHandler snd = FMLClientHandler.instance().getClient().getSoundHandler();
        NyanMovingSound nyaaan = new NyanMovingSound(player);
        if (code == NyanCatPacket.PLAY_NYAN)
        {
                /*ISound nyan = new NyanMovingSound(player);*/

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
}
