package com.darkona.adventurebackpack.entity;

import com.darkona.adventurebackpack.common.BoltFlight;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityBolt extends EntityArrow implements IEntityAdditionalSpawnData
{
    private static final float ARROW_SPEED_FACTOR = 1.5F;
    private static final String TAG_DRAG = "BoltDrag";
    private static final String TAG_GRAVITY = "BoltGravity";
    private static final String TAG_PIERCING = "BoltPiercing";

    private float drag = BoltFlight.ARROW_DRAG;
    private float gravity = BoltFlight.ARROW_GRAVITY;
    private int piercing;
    private boolean hitThisTick;

    public EntityBolt(World world)
    {
        super(world);
    }

    public EntityBolt(World world, EntityLivingBase shooter, float speed)
    {
        super(world, shooter, speed / ARROW_SPEED_FACTOR);
    }

    public void setFlight(float drag, float gravity, int piercing)
    {
        this.drag = drag;
        this.gravity = gravity;
        this.piercing = piercing < 0 ? 0 : piercing;
    }

    public void markHit()
    {
        hitThisTick = true;
    }

    @Override
    public void onUpdate()
    {
        hitThisTick = false;
        if (BoltFlight.isArrowFlight(drag, gravity))
        {
            super.onUpdate();
            return;
        }
        double vx = motionX;
        double vy = motionY;
        double vz = motionZ;
        super.onUpdate();
        if (isDead || arrowShake > 0 || !BoltFlight.flewFreely(vx, vy, vz, motionX, motionY, motionZ)) return;
        double scale = BoltFlight.dragScale(drag);
        motionX *= scale;
        motionZ *= scale;
        motionY = BoltFlight.correctY(motionY, drag, gravity);
    }

    @Override
    public void setDead()
    {
        if (hitThisTick && piercing > 0 && !worldObj.isRemote)
        {
            hitThisTick = false;
            piercing--;
            return;
        }
        super.setDead();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);
        if (BoltFlight.isArrowFlight(drag, gravity) && piercing == 0) return;
        tag.setFloat(TAG_DRAG, drag);
        tag.setFloat(TAG_GRAVITY, gravity);
        tag.setInteger(TAG_PIERCING, piercing);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);
        if (!tag.hasKey(TAG_DRAG)) return;
        setFlight(tag.getFloat(TAG_DRAG), tag.getFloat(TAG_GRAVITY), tag.getInteger(TAG_PIERCING));
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeDouble(motionX);
        buffer.writeDouble(motionY);
        buffer.writeDouble(motionZ);
        buffer.writeFloat(drag);
        buffer.writeFloat(gravity);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        motionX = buffer.readDouble();
        motionY = buffer.readDouble();
        motionZ = buffer.readDouble();
        drag = buffer.readFloat();
        gravity = buffer.readFloat();
    }
}
