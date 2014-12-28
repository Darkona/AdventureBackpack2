package com.darkona.adventurebackpack.misc;

import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.reference.ModInfo;
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
public class NyanMovingSound extends MovingSound
{

    public static NyanMovingSound instance = new NyanMovingSound();
    public EntityPlayer getPlayer()
    {
        return player;
    }

    public NyanMovingSound setPlayer(EntityPlayer p)
    {
        this.player = p;
        return this;
    }

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
        if (this.player.isDead)
        {
            this.donePlaying = true;
        } else
        {
            if(BackpackNames.getBackpackColorName(Wearing.getWearingBackpack(player)).equals("Rainbow") &&
                    Wearing.getWearingBackpack(player).stackTagCompound.getInteger("lastTime") > 0)
            {
                this.volume = 0.8f;
            }else
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
    public ResourceLocation getPositionedSoundLocation()
    {
        return super.getPositionedSoundLocation();
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
