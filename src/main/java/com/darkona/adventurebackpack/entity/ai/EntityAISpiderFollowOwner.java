package com.darkona.adventurebackpack.entity.ai;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * A tamed spider walks (or climbs) after its owner while the owner wears the Spider Backpack, like the horse that
 * follows its owner ({@link EntityAIHorseFollowOwner}) and the vanilla wolf. When it falls too far behind it jumps
 * next to the owner (abilities.spider.teleportDistance, 0 = never).
 * <p>
 * The owner is looked up by UUID only when the task starts and dropped when it stops, so the task never keeps a
 * player that left.
 */
public class EntityAISpiderFollowOwner extends EntityAIBase
{
    private final EntityFriendlySpider spider;
    private final double speed;
    private EntityPlayer owner;
    private PathNavigate navigator;
    private int repathTicks;

    public EntityAISpiderFollowOwner(EntityFriendlySpider spider, double speed)
    {
        this.spider = spider;
        this.speed = speed;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        if (spider.riddenByEntity != null || spider.getOwnerId() == null) return false;
        EntityPlayer candidate = spider.getOwnerPlayer();
        if (candidate == null || candidate.ridingEntity == spider) return false;
        double start = ConfigHandler.SPIDER_FOLLOW_START_DISTANCE;
        if (spider.getDistanceSqToEntity(candidate) < start * start) return false;
        if (!EntityFriendlySpider.isFriendOf(candidate)) return false;
        owner = candidate;
        return true;
    }

    @Override
    public boolean continueExecuting()
    {
        if (owner == null || owner.isDead || owner.worldObj != spider.worldObj || spider.riddenByEntity != null) return false;
        double stop = ConfigHandler.SPIDER_FOLLOW_STOP_DISTANCE;
        return !navigator.noPath() && spider.getDistanceSqToEntity(owner) > stop * stop;
    }

    @Override
    public void startExecuting()
    {
        navigator = spider.getNavigator();
        repathTicks = 0;
    }

    @Override
    public void resetTask()
    {
        owner = null;
        if (navigator != null) navigator.clearPathEntity();
    }

    @Override
    public void updateTask()
    {
        spider.getLookHelper().setLookPositionWithEntity(owner, 10.0F, (float) spider.getVerticalFaceSpeed());
        if (--repathTicks > 0) return;
        repathTicks = 10;
        if (navigator.tryMoveToEntityLiving(owner, speed)) return;
        int teleport = ConfigHandler.SPIDER_TELEPORT_DISTANCE;
        if (teleport > 0 && !spider.getLeashed() && spider.getDistanceSqToEntity(owner) >= (double) teleport * teleport)
        {
            teleportNearOwner();
        }
    }

    /** Like the wolf: the first free spot with solid ground in a ring two blocks around the owner. */
    private void teleportNearOwner()
    {
        World world = spider.worldObj;
        int x = MathHelper.floor_double(owner.posX) - 2;
        int z = MathHelper.floor_double(owner.posZ) - 2;
        int y = MathHelper.floor_double(owner.boundingBox.minY);
        for (int dx = 0; dx <= 4; ++dx)
        {
            for (int dz = 0; dz <= 4; ++dz)
            {
                if ((dx < 1 || dz < 1 || dx > 3 || dz > 3)
                        && World.doesBlockHaveSolidTopSurface(world, x + dx, y - 1, z + dz)
                        && !world.getBlock(x + dx, y, z + dz).isNormalCube()
                        && !world.getBlock(x + dx, y + 1, z + dz).isNormalCube())
                {
                    spider.setLocationAndAngles(x + dx + 0.5F, y, z + dz + 0.5F, spider.rotationYaw, spider.rotationPitch);
                    navigator.clearPathEntity();
                    return;
                }
            }
        }
    }
}
