package com.darkona.adventurebackpack.client.audio;

import com.darkona.adventurebackpack.inventory.InventoryCoalJetpack;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Created on 16/01/2015
 *
 * @author Darkona
 */
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

        InventoryCoalJetpack inv = new InventoryCoalJetpack(Wearing.getWearingSteam(thePlayer));
        if(inv.isBoiling() && inv.getCoalTank().getFluidAmount() > 0)
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
