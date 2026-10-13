package com.darkona.adventurebackpack.entity.ai;

import java.util.Arrays;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan;
import com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan.Mode;
import com.darkona.adventurebackpack.entity.ai.climb.ClimbPlanner;
import com.darkona.adventurebackpack.entity.ai.climb.WorldBlockQuery;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * The tamed spider's navigator: the vanilla ground path finder, plus the vertical moves it cannot make (up a wall or a
 * tower, over the top edge, down a cliff, off a low edge) planned by {@link ClimbPlanner} when the ground path does
 * not reach the owner.
 * <p>
 * The spider returns it from {@code getNavigator()} and ticks it from {@code updateAITick()}; the private vanilla
 * navigator of {@code EntityLiving} is left without a path, so it does nothing. Per tick it costs a comparison and
 * at most one {@code setMoveTo}; planning (about a hundred block reads at most) happens on the follow task's repath
 * timer and only while the ground path falls short. Nothing is allocated per tick.
 */
public class SpiderNavigate extends PathNavigate
{
    /** How far along the line to the owner a wall is looked for. */
    private static final int SCAN_RADIUS = 8;
    /** A failed column is avoided until this many ticks pass without another failure. */
    private static final int BAD_COLUMN_TICKS = 200;
    private static final long EMPTY = Long.MIN_VALUE;

    private final EntityFriendlySpider spider;
    private final WorldBlockQuery query;
    private final ClimbPlan plan = new ClimbPlan();
    private final long[] badColumns = new long[4];
    private int badNext;
    private long lastFailTick;
    private int ticksInMode;
    private int ceilingTicks;
    private int timeLimit;
    private double speed;
    private boolean clinging;
    /** Within this squared distance of the face column's centre a descending spider holds on to it. */
    private static final double CLING_DIST_SQ = 1.7 * 1.7;

    public SpiderNavigate(EntityFriendlySpider spider, World world)
    {
        super(spider, world);
        this.spider = spider;
        this.query = new WorldBlockQuery(world);
        Arrays.fill(badColumns, EMPTY);
    }

    public Mode mode()
    {
        return plan.mode;
    }

    /** On a wall or going over an edge: the follow task must not let go now. */
    public boolean isClimbing()
    {
        Mode m = plan.mode;
        return m == Mode.ASCEND || m == Mode.LIP || m == Mode.DESCEND || m == Mode.DROP;
    }

    /** True while the wall climbing physics is wanted (see {@link EntityFriendlySpider#isOnLadder()}). */
    public boolean wantsLadder()
    {
        return plan.mode == Mode.ASCEND || plan.mode == Mode.LIP;
    }

    public boolean isDescending()
    {
        return plan.mode == Mode.DESCEND;
    }

    /**
     * Goes after {@code target}: the ground path when it gets there, else a vertical move. Called on the follow task's
     * repath timer. Returns false when there is neither a path nor a plan.
     */
    public boolean followEntity(Entity target, double speed)
    {
        this.speed = speed;
        if (spider.worldObj.getTotalWorldTime() - lastFailTick > BAD_COLUMN_TICKS) Arrays.fill(badColumns, EMPTY);
        if (isClimbing()) return true; // committed: the move ends by itself (done, failed or timed out)

        int sy = MathHelper.floor_double(spider.boundingBox.minY);
        int ty = MathHelper.floor_double(target.boundingBox.minY);
        boolean pathOk = tryMoveToEntityLiving(target, speed);
        if (!ConfigHandler.SPIDER_SMART_CLIMB || Math.abs(ty - sy) < 2 || (pathOk && pathReaches(target, ty)))
        {
            stopClimb();
            return pathOk;
        }

        Mode before = plan.mode;
        int beforeX = plan.wallX, beforeZ = plan.wallZ;
        if (!ClimbPlanner.plan(query, MathHelper.floor_double(spider.posX), sy, MathHelper.floor_double(spider.posZ),
                MathHelper.floor_double(target.posX), ty, MathHelper.floor_double(target.posZ),
                ConfigHandler.SPIDER_CLIMB_MAX_HEIGHT, ConfigHandler.SPIDER_SAFE_DROP_HEIGHT, SCAN_RADIUS, badColumns, plan))
        {
            stopClimb();
            return pathOk;
        }
        if (!(before == plan.mode && beforeX == plan.wallX && beforeZ == plan.wallZ))
        {
            ticksInMode = 0; // a new move; the same approach keeps its clock so it can time out
        }
        ceilingTicks = 0;
        timeLimit = ClimbPlanner.modeTimeLimit(Math.max(plan.topY - plan.startY, plan.startY - plan.bottomY));
        if (plan.mode == Mode.APPROACH && plan.approachByPath)
        {
            if (!tryMoveToXYZ(plan.approachX + 0.5, sy, plan.approachZ + 0.5, speed)) plan.approachByPath = false;
        }
        else
        {
            super.clearPathEntity();
        }
        return true;
    }

    /** The ground path ends next to the target (the 1.7.10 path finder returns a path to the nearest point it reached). */
    private boolean pathReaches(Entity target, int targetY)
    {
        PathEntity path = getPath();
        PathPoint end = path == null ? null : path.getFinalPathPoint();
        if (end == null || Math.abs(end.yCoord - targetY) > 1) return false;
        double dx = end.xCoord + 0.5 + spider.width * 0.5 - target.posX;
        double dz = end.zCoord + 0.5 + spider.width * 0.5 - target.posZ;
        return dx * dx + dz * dz <= 9.0;
    }

    /** The follow task found no progress: avoid this column and plan again on the next repath. */
    public void abandonCurrentColumn()
    {
        if (plan.mode == Mode.APPROACH) // a move on the wall has its own time limits; letting go there would drop it
        {
            markBad(plan.columnKey());
            stopClimb();
        }
    }

    @Override
    public void clearPathEntity()
    {
        super.clearPathEntity();
        stopClimb();
    }

    private void stopClimb()
    {
        clinging = false;
        plan.mode = Mode.NONE;
        ticksInMode = 0;
        ceilingTicks = 0;
    }

    private void markBad(long key)
    {
        badColumns[badNext] = key;
        badNext = (badNext + 1) & 3;
        lastFailTick = spider.worldObj.getTotalWorldTime();
    }

    @Override
    public void onUpdateNavigation()
    {
        if (plan.mode == Mode.NONE || (plan.mode == Mode.APPROACH && plan.approachByPath && !noPath()))
        {
            super.onUpdateNavigation();
            return;
        }
        ticksInMode++;
        double wx = plan.wallX + 0.5 - spider.posX;
        double wz = plan.wallZ + 0.5 - spider.posZ;
        double wallDistSq = wx * wx + wz * wz;
        if (plan.mode == Mode.ASCEND && spider.isCollidedVertically && !spider.onGround) ceilingTicks++;
        else ceilingTicks = 0;

        Mode next = ClimbPlanner.nextMode(plan.mode, spider.posY, plan.startY, plan.topY, plan.bottomY,
                ConfigHandler.SPIDER_SAFE_DROP_HEIGHT, spider.onGround, spider.isBesideClimbableBlock() || clinging, wallDistSq,
                ceilingTicks, ticksInMode, timeLimit);
        if (next != plan.mode)
        {
            if (next == Mode.FAILED)
            {
                markBad(plan.columnKey());
                if ((plan.mode == Mode.ASCEND || plan.mode == Mode.LIP) && !spider.onGround
                        && spider.posY > plan.startY + ConfigHandler.SPIDER_SAFE_DROP_HEIGHT)
                {
                    // halfway up a wall that did not work: climb back down it instead of letting go
                    plan.mode = Mode.DESCEND;
                    plan.bottomY = plan.startY;
                    plan.startY = MathHelper.floor_double(spider.posY) + 1;
                    ticksInMode = 0;
                    ceilingTicks = 0;
                    timeLimit = ClimbPlanner.modeTimeLimit(plan.startY - plan.bottomY);
                    return;
                }
                stopClimb();
                return;
            }
            if (next == Mode.NONE)
            {
                stopClimb();
                return;
            }
            plan.mode = next;
            ticksInMode = 0;
            ceilingTicks = 0;
            if (next == Mode.LIP)
            {
                // the little hop over the edge
                if (spider.motionY < 0.25) spider.motionY = 0.25;
            }
        }

        int dirX = plan.approachX - plan.wallX, dirZ = plan.approachZ - plan.wallZ;
        clinging = false;
        switch (plan.mode)
        {
            case APPROACH:
                spider.getMoveHelper().setMoveTo(plan.approachX + 0.5, plan.startY, plan.approachZ + 0.5, speed);
                break;
            case ASCEND:
                spider.getMoveHelper().setMoveTo(plan.wallX + 0.5, plan.topY, plan.wallZ + 0.5, speed);
                break;
            case LIP:
                spider.getMoveHelper().setMoveTo(plan.wallX + 0.5, plan.topY, plan.wallZ + 0.5, speed);
                // in the air the spider barely steers: carry it over the top onto the column
                steerTowards(plan.wallX + 0.5, plan.wallZ + 0.5, 0.2);
                break;
            case DROP:
                spider.getMoveHelper().setMoveTo(plan.approachX + 0.5 + 0.6 * dirX, plan.bottomY, plan.approachZ + 0.5 + 0.6 * dirZ, speed);
                break;
            case DESCEND:
                if (spider.onGround && spider.posY > plan.startY - 0.5)
                {
                    // walk off the edge, just far enough to drop beside the face
                    spider.getMoveHelper().setMoveTo(plan.approachX + 0.5 + 0.3 * dirX, plan.startY, plan.approachZ + 0.5 + 0.3 * dirZ, speed);
                }
                else
                {
                    // hug the face on the way down; the spider slows its fall while within reach of it
                    spider.getMoveHelper().setMoveTo(plan.wallX + 0.5, plan.bottomY, plan.wallZ + 0.5, speed);
                    if (!spider.isBesideClimbableBlock()) steerTowards(plan.wallX + 0.5, plan.wallZ + 0.5, 0.1);
                    clinging = !spider.onGround && wallDistSq <= CLING_DIST_SQ;
                }
                break;
            default:
                break;
        }
    }

    /** Sets the horizontal motion towards a point, proportional and capped (the move helper hardly steers in the air). */
    private void steerTowards(double x, double z, double max)
    {
        double mx = (x - spider.posX) * 0.25, mz = (z - spider.posZ) * 0.25;
        spider.motionX = mx > max ? max : mx < -max ? -max : mx;
        spider.motionZ = mz > max ? max : mz < -max ? -max : mz;
    }

    /** Climbing down a face and within reach of it: the spider falls slowly and takes no fall damage. */
    public boolean isClinging()
    {
        return clinging;
    }
}
