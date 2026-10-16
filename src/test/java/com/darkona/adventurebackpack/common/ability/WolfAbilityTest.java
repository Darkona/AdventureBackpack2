package com.darkona.adventurebackpack.common.ability;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.profiler.Profiler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** The Wolf Backpack resets the target tasks of a calmed wolf: every one of them, without throwing. */
class WolfAbilityTest
{
    private static final class CountingTask extends EntityAIBase
    {
        int resets;

        @Override
        public boolean shouldExecute()
        {
            return false;
        }

        @Override
        public void resetTask()
        {
            resets++;
        }
    }

    @Test
    void everyTargetTaskIsReset()
    {
        EntityAITasks tasks = new EntityAITasks(new Profiler());
        CountingTask first = new CountingTask();
        CountingTask second = new CountingTask();
        tasks.addTask(1, first);
        tasks.addTask(2, second);

        WolfAbility.resetTasks(tasks);

        assertEquals(1, first.resets);
        assertEquals(1, second.resets);
    }
}
