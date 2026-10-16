package com.darkona.adventurebackpack.client.audio;

import com.darkona.adventurebackpack.common.RenderInfo;
import com.darkona.adventurebackpack.common.RenderInfoCache;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class BoilingBoilerSound extends MovingSound
{
    public EntityPlayer thePlayer;
    protected boolean repeat = true;
    protected int repeatDelay = 0;

    protected float pitch;

    public BoilingBoilerSound(EntityPlayer player)
    {
        super(new ResourceLocation(ModInfo.MOD_ID, "s_boiling"));
        volume = 0.25f;
        pitch = 0.4F;
        thePlayer = player;
    }

    public EntityPlayer getThePlayer()
    {
        return thePlayer;
    }

    public void setThePlayer(EntityPlayer player){
        thePlayer = player;
    }

     public void setDonePlaying()
    {
        repeat = false;
        donePlaying = true;
        repeatDelay = 0;
    }

    @Override
    public boolean isDonePlaying()
    {
        return this.donePlaying;
    }

    @Override
    public void update()
    {
        if (thePlayer == null || thePlayer.isDead || thePlayer.worldObj == null || !Wearing.isWearingSteam(thePlayer))
        {
            setDonePlaying();
            return;
        }

        RenderInfo info = RenderInfoCache.get(Wearing.getWearingSteam(thePlayer));
        if (info.jetpackBoiling && info.leftFluid != null && info.leftFluid.amount > 0)
        {
            xPosF = (float)thePlayer.posX;
            yPosF = (float)thePlayer.posY;
            zPosF = (float)thePlayer.posZ;
        }else
        {
            setDonePlaying();
        }
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
    public int getRepeatDelay(){ return this.repeatDelay; }

    @Override
    public AttenuationType getAttenuationType()
    {
        return AttenuationType.LINEAR;
    }
}
