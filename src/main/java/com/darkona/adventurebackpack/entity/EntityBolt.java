package com.darkona.adventurebackpack.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created on 07/01/2015
 *
 * @author Darkona
 */
public class EntityBolt extends Entity implements IProjectile
{
    public EntityBolt(World world)
    {
        super(world);
    }

    @Override
    protected void entityInit()
    {

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {

    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {

    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    @Override
    public void setThrowableHeading(double x, double y, double z, float speedMultiplier, float whatever)
    {

    }
}
