package com.darkona.adventurebackpack.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;

public class EntityBolt extends EntityArrow
{
    private static final float ARROW_SPEED_FACTOR = 1.5F;

    public EntityBolt(World world)
    {
        super(world);
    }

    public EntityBolt(World world, EntityLivingBase shooter, float speed)
    {
        super(world, shooter, speed / ARROW_SPEED_FACTOR);
    }
}
