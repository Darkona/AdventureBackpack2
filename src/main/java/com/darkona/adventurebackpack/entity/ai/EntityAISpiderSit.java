package com.darkona.adventurebackpack.entity.ai;

import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import net.minecraft.entity.ai.EntityAIBase;

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
