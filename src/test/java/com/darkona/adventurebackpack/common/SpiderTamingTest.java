package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpiderTamingTest
{
    private final UUID owner = UUID.randomUUID();
    private final UUID other = UUID.randomUUID();
    private final UUID spiderA = UUID.randomUUID();
    private final UUID spiderB = UUID.randomUUID();

    @BeforeEach
    void reset()
    {
        SpiderTaming.clear();
    }

    @Test
    void limitCountsTheSpidersAlreadyTamed()
    {
        assertTrue(SpiderTaming.canTameMore(0, 2));
        assertTrue(SpiderTaming.canTameMore(1, 2));
        assertFalse(SpiderTaming.canTameMore(2, 2));
        assertFalse(SpiderTaming.canTameMore(0, 0), "0 turns taming off");
    }

    @Test
    void chanceOfOneAlwaysTamesAndZeroNever()
    {
        assertTrue(SpiderTaming.rollTames(0.999f, 1.0));
        assertFalse(SpiderTaming.rollTames(0.0f, 0.0));
        assertTrue(SpiderTaming.rollTames(0.2f, 0.33));
        assertFalse(SpiderTaming.rollTames(0.5f, 0.33));
    }

    @Test
    void aSpiderIsTamedOnlyUnderItsOwner()
    {
        SpiderTaming.add(owner, spiderA);
        assertTrue(SpiderTaming.isTamed(owner, spiderA));
        assertFalse(SpiderTaming.isTamed(other, spiderA));
        assertFalse(SpiderTaming.isTamed(null, spiderA), "a spider from an older version has no owner");
        assertEquals(1, SpiderTaming.count(owner));
        assertEquals(0, SpiderTaming.count(other));
    }

    @Test
    void theOwnerLeavingReleasesEverySpider()
    {
        SpiderTaming.add(owner, spiderA);
        SpiderTaming.add(owner, spiderB);
        SpiderTaming.add(other, UUID.randomUUID());
        SpiderTaming.releaseAll(owner);
        assertFalse(SpiderTaming.isTamed(owner, spiderA));
        assertFalse(SpiderTaming.isTamed(owner, spiderB));
        assertEquals(0, SpiderTaming.count(owner));
        assertEquals(1, SpiderTaming.count(other), "other owners keep theirs");
    }

    @Test
    void aDeadOrReleasedSpiderFreesItsPlace()
    {
        SpiderTaming.add(owner, spiderA);
        SpiderTaming.add(owner, spiderB);
        SpiderTaming.forget(owner, spiderA);
        assertEquals(1, SpiderTaming.count(owner));
        assertTrue(SpiderTaming.isTamed(owner, spiderB));
        SpiderTaming.forget(owner, spiderB);
        SpiderTaming.forget(owner, spiderB);
        assertEquals(0, SpiderTaming.count(owner));
    }
}
