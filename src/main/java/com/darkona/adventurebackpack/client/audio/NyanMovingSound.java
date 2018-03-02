package com.darkona.adventurebackpack.client.audio;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import com.darkona.adventurebackpack.reference.BackpackTypes;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.Wearing;

/**
 * Created on 16/10/2014
 *
 * @author Darkona
 */
public class NyanMovingSound extends MovingSound
{
    public static NyanMovingSound instance = new NyanMovingSound();

    private EntityPlayer player;


    public NyanMovingSound(EntityPlayer player)
    {
        super(new ResourceLocation(ModInfo.MOD_ID, "nyan"));
        this.volume = 0.8f;
        this.player = player;
    }

    public NyanMovingSound()
    {
        super(new ResourceLocation(ModInfo.MOD_ID, "nyan"));
    }

    public void setDonePlaying()
    {
        this.donePlaying = true;
    }

    @Override
    public void update()
    {
        if (player == null || player.isDead)
        {
            this.donePlaying = true;
        }
        else
        {
            if (Wearing.isWearingTheRightBackpack(player, BackpackTypes.RAINBOW))
            {
                this.volume = 0.8f;
            }
            else
            {
                this.donePlaying = true;
                // this.volume = 0.0f;
            }

            this.xPosF = (float) this.player.posX;
            this.yPosF = (float) this.player.posY;
            this.zPosF = (float) this.player.posZ;
        }
    }

    @Override
    public boolean canRepeat()
    {
        return false;
    }

    @Override
    public int getRepeatDelay()
    {
        return super.getRepeatDelay();
    }

    @Override
    public float getVolume()
    {
        return super.getVolume();
    }

    @Override
    public float getPitch()
    {
        return super.getPitch();
    }

    @Override
    public float getXPosF()
    {
        return super.getXPosF();
    }

    @Override
    public float getYPosF()
    {
        return super.getYPosF();
    }

    @Override
    public float getZPosF()
    {
        return super.getZPosF();
    }

    @Override
    public AttenuationType getAttenuationType()
    {
        return AttenuationType.LINEAR;
    }
}
