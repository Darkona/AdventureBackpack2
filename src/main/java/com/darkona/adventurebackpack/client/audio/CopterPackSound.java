package com.darkona.adventurebackpack.client.audio;

import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Created on 16/10/2014
 *
 * @author Darkona
 */
public class CopterPackSound extends MovingSound
{

    public EntityPlayer player;
    private boolean repeat;
    private CopterPackSound myself;
    protected float pitch;

    public CopterPackSound()
    {
        super(new ResourceLocation(ModInfo.MOD_ID, "helicopter"));
        this.repeat = true;
        this.volume = 0.8f;
        this.pitch = 1.0F;
    }

    public CopterPackSound(EntityPlayer player)
    {
        super(new ResourceLocation(ModInfo.MOD_ID, "helicopter"));
        this.volume = 0.8f;
        this.pitch = 1.0F;
        this.player = player;
        this.repeat = true;
    }

    public void setDonePlaying()
    {
        this.donePlaying = true;
    }

    @Override
    public void update()
    {
        if(player.isDead || !Wearing.isWearingCopter(player)){
            setDonePlaying();
            return;
        }
        if(Wearing.getWearingCopter(player).hasTagCompound() && Wearing.getWearingCopter(player).getTagCompound().hasKey("status"))
        {
            if(Wearing.getWearingCopter(player).getTagCompound().getByte("status") == ItemCopterPack.OFF_MODE)
            {
                setDonePlaying();
                return;
            }
        }

        if(!player.onGround) this.pitch = (player.motionY > 0) ? 1.2F :  (player.isSneaking()) ? 0.8F : 1.0F;
        if(player.onGround) this.pitch = 0.8F;

        this.xPosF = (float) this.player.posX;
        this.yPosF = (float) this.player.posY;
        this.zPosF = (float) this.player.posZ;
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
    public AttenuationType getAttenuationType()
    {
        return AttenuationType.LINEAR;
    }
}
