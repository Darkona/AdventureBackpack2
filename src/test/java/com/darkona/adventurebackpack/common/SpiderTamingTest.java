package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class SpiderTamingTest
{
    private final UUID owner = UUID.randomUUID();
    private final UUID other = UUID.randomUUID();
    private final UUID spiderA = UUID.randomUUID();
    private final UUID spiderB = UUID.randomUUID();

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
    void aSpiderIsListedOnlyUnderItsOwner()
    {
        SpiderLedger ledger = new SpiderLedger();
        ledger.add(owner, spiderA, 0, 1, 2);
        assertTrue(ledger.isListed(owner, spiderA));
        assertFalse(ledger.isListed(other, spiderA));
        assertFalse(ledger.isListed(null, spiderA), "a spider from 1.0.0 has no owner");
        assertEquals(1, ledger.count(owner));
        assertEquals(0, ledger.count(other));
    }

    @Test
    void aDeadOrReleasedSpiderFreesItsPlace()
    {
        SpiderLedger ledger = new SpiderLedger();
        ledger.add(owner, spiderA, 0, 0, 0);
        ledger.add(owner, spiderB, 0, 0, 0);
        assertFalse(ledger.canTameMore(owner, 2));
        ledger.forget(owner, spiderA);
        assertEquals(1, ledger.count(owner));
        assertTrue(ledger.canTameMore(owner, 2));
        assertTrue(ledger.forget(owner, spiderB));
        assertFalse(ledger.forget(owner, spiderB), "forgetting twice (death then removal) changes nothing");
        assertEquals(0, ledger.count(owner));
    }
}
