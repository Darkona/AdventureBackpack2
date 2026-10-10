package com.darkona.adventurebackpack.client.audio;

import com.darkona.adventurebackpack.common.BoatMotorSound;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;

/**
 * The looping putt-putt of one boat's running motor (inflatableboat.motor.sound). It follows the boat, gets a little
 * louder and higher with the speed, and ends by itself when the motor stops, the boat dies or it stops ticking
 * (unloaded). ClientActions starts one per motor start; nothing here allocates per tick.
 */
@SideOnly(Side.CLIENT)
public class BoatMotorLoopSound extends MovingSound
{
    private final EntityInflatableBoat boat;
    private float pitch = BoatMotorSound.LOOP_PITCH_IDLE;
    private int lastAge;
    private int stale;

    public BoatMotorLoopSound(EntityInflatableBoat boat)
    {
        super(new ResourceLocation(ModInfo.MOD_ID, "boatmotorloop"));
        this.boat = boat;
        this.lastAge = boat.ticksExisted;
        this.volume = BoatMotorSound.loopVolume(ConfigHandler.BOAT_MOTOR_SOUND_VOLUME, 0.0F);
        this.xPosF = (float) boat.posX;
        this.yPosF = (float) boat.posY;
        this.zPosF = (float) boat.posZ;
    }

    @Override
    public void update()
    {
        stale = BoatMotorSound.nextStale(lastAge, boat.ticksExisted, stale);
        lastAge = boat.ticksExisted;
        if (!BoatMotorSound.loopContinues(ConfigHandler.BOAT_MOTOR_SOUND, boat.isMotorRunning(), !boat.isDead && !BoatMotorSound.gone(stale)))
        {
            donePlaying = true;
            return;
        }
        double speed = Math.sqrt(boat.motionX * boat.motionX + boat.motionZ * boat.motionZ);
        float fraction = BoatMotorSound.speedFraction(speed, ConfigHandler.BOAT_MOTOR_MAX_SPEED);
        volume = BoatMotorSound.loopVolume(ConfigHandler.BOAT_MOTOR_SOUND_VOLUME, fraction);
        pitch = BoatMotorSound.loopPitch(fraction);
        xPosF = (float) boat.posX;
        yPosF = (float) boat.posY;
        zPosF = (float) boat.posZ;
    }

    @Override
    public boolean isDonePlaying()
    {
        return donePlaying;
    }

    @Override
    public boolean canRepeat()
    {
        return true;
    }

    @Override
    public int getRepeatDelay()
    {
        return 0;
    }

    @Override
    public float getVolume()
    {
        return volume;
    }

    @Override
    public float getPitch()
    {
        return pitch;
    }

    @Override
    public AttenuationType getAttenuationType()
    {
        return AttenuationType.LINEAR;
    }
}
