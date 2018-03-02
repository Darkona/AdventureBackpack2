package com.darkona.adventurebackpack.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created on 09/01/2015
 *
 * @author Darkona
 */
public class EntityCrossbowProjectile extends Entity implements IProjectile
{
    public EntityCrossbowProjectile(World p_i1776_1_)
    {
        super(p_i1776_1_);
    }

    @Override
    protected void entityInit()
    {

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {

    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {

    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    @Override
    public void setThrowableHeading(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_)
    {

    }
}
