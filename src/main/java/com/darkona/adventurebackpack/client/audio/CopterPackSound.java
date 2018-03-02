package com.darkona.adventurebackpack.client.audio;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Wearing;

import static com.darkona.adventurebackpack.common.Constants.Copter.TAG_STATUS;

/**
 * Created on 16/10/2014
 *
 * @author Darkona
 */
public class CopterPackSound extends MovingSound
{
    private EntityPlayer thePlayer;
    private boolean repeat = true;
    private int repeatDelay = 0;
    private float pitch;

    public CopterPackSound(EntityPlayer player)
    {
        super(new ResourceLocation(ModInfo.MOD_ID, "helicopter3"));
        volume = 0.6f;
        pitch = 1.0F;
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
        if (thePlayer == null || thePlayer.isDead || thePlayer.worldObj == null || copter == null || !(copter.getItem() instanceof ItemCopterPack))
        {
            setDonePlaying();
            return;
        }

        status = BackpackUtils.getWearableCompound(copter).getByte(TAG_STATUS);
        if (status == ItemCopterPack.OFF_MODE)
        {
            setDonePlaying();
        }
        else if (status == ItemCopterPack.HOVER_MODE)
        {
            pitch = (thePlayer.motionY > 0) ? 1.2f : (thePlayer.motionY < -0.1) ? 0.8f : 1.0f;
        }
        else if (status == ItemCopterPack.NORMAL_MODE)
        {
            if (thePlayer.onGround)
                pitch = 0.8f;
            else
                pitch = (thePlayer.motionY > 0) ? 1.2f : (thePlayer.isSneaking()) ? 0.8f : 1.0f;
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
