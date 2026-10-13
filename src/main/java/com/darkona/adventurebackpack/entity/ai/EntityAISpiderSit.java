package com.darkona.adventurebackpack.entity.ai;

import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * A sitting tamed spider stays put, like a sitting wolf ({@code EntityAISit}): mutex 5 (move and jump) blocks the
 * follow and wander tasks. In water it swims instead.
 */
public class EntityAISpiderSit extends EntityAIBase
{
    private final EntityFriendlySpider spider;

    public EntityAISpiderSit(EntityFriendlySpider spider)
    {
        this.spider = spider;
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute()
    {
        return spider.isSitting() && spider.riddenByEntity == null && !spider.isInWater() && spider.onGround;
    }

    @Override
    public void startExecuting()
    {
        spider.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask()
    {
        spider.setJumping(false);
    }
}
