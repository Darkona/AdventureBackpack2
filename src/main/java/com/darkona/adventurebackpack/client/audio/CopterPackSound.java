package com.darkona.adventurebackpack.client.audio;

import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Wearing;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created on 16/10/2014
 *
 * @author Darkona
 */
public class CopterPackSound extends MovingSound
{

    public EntityPlayer thePlayer;
    protected boolean repeat = true;
    protected int repeatDelay = 0;

    protected float pitch;

    public CopterPackSound(EntityPlayer player)
    {
        super(new ResourceLocation(ModInfo.MOD_ID, "helicopter"));
        volume = 0.6f;
        pitch = 1.0F;
        thePlayer = player;
    }

    public EntityPlayer getThePlayer()
    {
        return thePlayer;
    }

    public void setThePlayer(EntityPlayer player)
    {
        thePlayer = player;
    }

    public void setRepeat(boolean newRepeat)
    {
        LogHelper.info("Setting sound repeat");
        repeat = newRepeat;
    }

    public void setDonePlaying()
    {
        this.repeat = false;
        this.donePlaying = true;
        this.repeatDelay = 0;
    }

    @Override
    public boolean isDonePlaying()
    {
        return this.donePlaying;
    }

    @Override
    public void update()
    {
        ItemStack copter = Wearing.getWearingCopter(thePlayer);
        byte status = 0;
        if (thePlayer == null || thePlayer.worldObj == null || copter == null || !(copter.getItem() instanceof ItemCopterPack))
        {
            setDonePlaying();
            return;
        }
        if (copter.hasTagCompound() && copter.getTagCompound().hasKey("status"))
        {
            status = copter.getTagCompound().getByte("status");
            if (status == ItemCopterPack.OFF_MODE)
            {
                setDonePlaying();
            } else
            {
                if (status == ItemCopterPack.HOVER_MODE)
                {
                    pitch = (thePlayer.motionY > 0) ? 1.2f : (thePlayer.motionY < -0.1) ? 0.8f : 1.0f;
                }
                if (status == ItemCopterPack.NORMAL_MODE)
                {
                    if (thePlayer.onGround)
                    {
                        pitch = 0.8f;
                    } else
                    {
                        pitch = (thePlayer.motionY > 0) ? 1.2f : (thePlayer.isSneaking()) ? 0.8f : 1.0f;
                    }
                }
            }
        } else
        {
            setDonePlaying();
        }
        xPosF = (float) thePlayer.posX;
        yPosF = (float) thePlayer.posY;
        zPosF = (float) thePlayer.posZ;
    }

    @Override
    public boolean canRepeat()
    {
        return this.repeat;
    }

    @Override
    public float getVolume()
    {
        return this.volume;
    }

    @Override
    public float getPitch()
    {
        return this.pitch;
    }

    @Override
    public int getRepeatDelay()
    {
        return this.repeatDelay;
    }

    @Override
    public AttenuationType getAttenuationType()
    {
        return AttenuationType.LINEAR;
    }

}
