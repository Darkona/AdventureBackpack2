package com.darkona.adventurebackpack.entity.ai;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * A tamed spider goes after its owner like a wolf, with or without the Spider Backpack: it walks, climbs walls and
 * towers to an owner above and climbs down to an owner below ({@link SpiderNavigate}). When it gets no closer for a
 * while it tries another wall; when it stays stuck, or falls far behind, it jumps next to the owner
 * (abilities.spider.teleportDistance, teleportStuckDistance).
 * <p>
 * The owner is looked up by UUID only when the task starts and dropped when it stops, so the task never keeps a
 * player that left. The decisions live in {@link SpiderFollowRules}.
 */
public class EntityAISpiderFollowOwner extends EntityAIBase
{
    /** Ticks between two progress checks. */
    private static final int PROGRESS_TICKS = 20;
    /** Squared blocks it must gain between two checks to count as moving on. */
    private static final double MIN_GAIN = 1.0;
    /** Ticks it rests after giving up, before it tries again. */
    private static final int GIVE_UP_TICKS = 60;

    private final EntityFriendlySpider spider;
    private final double speed;
    private EntityPlayer owner;
    private int repathTicks;
    private int progressTicks;
    private double bestDistSq;
    private int stuckTicks;
    private int ticksSinceTeleport = Integer.MAX_VALUE / 2;
    private long retryAfter;

    public EntityAISpiderFollowOwner(EntityFriendlySpider spider, double speed)
    {
        this.spider = spider;
        this.speed = speed;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        if (spider.getOwnerId() == null || spider.riddenByEntity != null || spider.isSitting()) return false;
        if (spider.worldObj.getTotalWorldTime() < retryAfter) return false;
        EntityPlayer candidate = spider.getOwnerPlayer();
        if (candidate == null || candidate.ridingEntity == spider) return false;
        if (!SpiderFollowRules.shouldFollow(spider.getDistanceSqToEntity(candidate), ConfigHandler.SPIDER_FOLLOW_START_DISTANCE,
                spider.isSitting(), spider.riddenByEntity != null, candidate.isEntityAlive(), true)) return false;
        owner = candidate;
        return true;
    }

    @Override
    public boolean continueExecuting()
    {
        if (owner == null || owner.ridingEntity == spider || spider.worldObj.getTotalWorldTime() < retryAfter) return false;
        return SpiderFollowRules.shouldContinue(spider.getDistanceSqToEntity(owner), ConfigHandler.SPIDER_FOLLOW_STOP_DISTANCE,
                spider.isSitting(), spider.riddenByEntity != null, owner.isEntityAlive(), owner.worldObj == spider.worldObj,
                spider.navigator().isClimbing());
    }

    @Override
    public void startExecuting()
    {
        repathTicks = 0;
        progressTicks = PROGRESS_TICKS;
        stuckTicks = 0;
        bestDistSq = spider.getDistanceSqToEntity(owner);
    }

    @Override
    public void resetTask()
    {
        owner = null;
        spider.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask()
    {
        SpiderNavigate nav = spider.navigator();
        spider.getLookHelper().setLookPositionWithEntity(owner, 10.0F, (float) spider.getVerticalFaceSpeed());
        ticksSinceTeleport++;
        if (--repathTicks <= 0)
        {
            repathTicks = ConfigHandler.SPIDER_REPATH_TICKS;
            nav.followEntity(owner, speed);
        }
        if (--progressTicks > 0) return;
        progressTicks = PROGRESS_TICKS;

        double distSq = spider.getDistanceSqToEntity(owner);
        if (SpiderFollowRules.progressed(bestDistSq, distSq, MIN_GAIN))
        {
            bestDistSq = distSq;
            stuckTicks = 0;
        }
        else
        {
            stuckTicks += PROGRESS_TICKS;
            if (distSq > bestDistSq) bestDistSq = distSq; // the owner moved away (or up): measure from here
        }
        int level = SpiderFollowRules.stuckLevel(stuckTicks, ConfigHandler.SPIDER_STUCK_TICKS);
        if (level == SpiderFollowRules.STUCK_REPLAN)
        {
            nav.abandonCurrentColumn();
            repathTicks = 0;
        }
        boolean sitting = spider.isSitting(), ridden = spider.riddenByEntity != null;
        if (SpiderFollowRules.shouldTeleport(distSq, ConfigHandler.SPIDER_TELEPORT_DISTANCE, level,
                ConfigHandler.SPIDER_TELEPORT_STUCK_DISTANCE, ticksSinceTeleport, ConfigHandler.SPIDER_TELEPORT_COOLDOWN_TICKS,
                sitting, ridden) && !spider.getLeashed() && teleportNearOwner())
        {
            ticksSinceTeleport = 0;
            stuckTicks = 0;
            bestDistSq = spider.getDistanceSqToEntity(owner);
        }
        else if (level >= SpiderFollowRules.STUCK_GIVE_UP && !nav.isClimbing())
        {
            // rest a while, then start over; continueExecuting turns false now
            retryAfter = spider.worldObj.getTotalWorldTime() + GIVE_UP_TICKS;
            stuckTicks = 0;
        }
    }

    /**
     * Like the wolf: the first free spot in a ring two blocks around the owner, at the owner's feet height, then one and
     * two lower and one higher; last, the block the owner stands on (the top of a one block pillar). The spot needs a
     * solid top under it, nothing hot under or in it, and room for the whole spider. Returns false when there is none
     * (the owner flies or swims, for example).
     */
    private boolean teleportNearOwner()
    {
        World world = spider.worldObj;
        int ox = MathHelper.floor_double(owner.posX);
        int oz = MathHelper.floor_double(owner.posZ);
        int oy = MathHelper.floor_double(owner.boundingBox.minY);
        for (int dyIndex = 0; dyIndex < 4; dyIndex++)
        {
            int y = oy + (dyIndex == 0 ? 0 : dyIndex == 1 ? -1 : dyIndex == 2 ? -2 : 1);
            for (int dx = -2; dx <= 2; ++dx)
            {
                for (int dz = -2; dz <= 2; ++dz)
                {
                    if ((dx == -2 || dz == -2 || dx == 2 || dz == 2) && tryPlace(world, ox + dx, y, oz + dz)) return true;
                }
            }
        }
        return tryPlace(world, ox, oy, oz);
    }

    private boolean tryPlace(World world, int x, int y, int z)
    {
        if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) return false;
        Material below = world.getBlock(x, y - 1, z).getMaterial();
        Material at = world.getBlock(x, y, z).getMaterial();
        if (below == Material.lava || below == Material.fire || at == Material.lava || at == Material.fire || at.isLiquid()) return false;
        if (at.blocksMovement() || world.getBlock(x, y + 1, z).getMaterial().blocksMovement()) return false;
        double px = spider.posX, py = spider.posY, pz = spider.posZ;
        spider.setPosition(x + 0.5, y, z + 0.5);
        if (world.getCollidingBoundingBoxes(spider, spider.boundingBox).isEmpty() && !world.isAnyLiquid(spider.boundingBox))
        {
            spider.setLocationAndAngles(x + 0.5, y, z + 0.5, spider.rotationYaw, spider.rotationPitch);
            spider.fallDistance = 0.0F;
            spider.getNavigator().clearPathEntity();
            return true;
        }
        spider.setPosition(px, py, pz);
        return false;
    }
}
