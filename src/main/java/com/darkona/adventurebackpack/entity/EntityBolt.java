package com.darkona.adventurebackpack.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;

/**
 * A bolt shot by the Clockwork Crossbow. It flies, sticks and hurts like a vanilla arrow; {@code RenderBolt} only
 * gives it its own texture, and {@code CrossbowEventHandler} lets a burst ignore the hurt cooldown. Bolts saved by
 * 1.0 with a {@code BoltType} tag (the removed elemental bolts) load as plain bolts: the tag is ignored.
 */
public class EntityBolt extends EntityArrow
{
    /** Vanilla multiplies the speed given to the arrow constructor by this. */
    private static final float ARROW_SPEED_FACTOR = 1.5F;

    public EntityBolt(World world)
    {
        super(world);
    }

    /** @param speed blocks per tick as the bolt leaves the crossbow */
    public EntityBolt(World world, EntityLivingBase shooter, float speed)
    {
        super(world, shooter, speed / ARROW_SPEED_FACTOR);
    }
}
