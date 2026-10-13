package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class SpiderLedgerTest
{
    private final UUID owner = UUID.randomUUID();
    private final UUID other = UUID.randomUUID();
    private final UUID a = UUID.randomUUID();
    private final UUID b = UUID.randomUUID();
    private final UUID c = UUID.randomUUID();

    @Test
    void addCountsOnceAndIsIdempotent()
    {
        SpiderLedger ledger = new SpiderLedger();
        assertTrue(ledger.add(owner, a, 0, 3, 4));
        assertFalse(ledger.add(owner, a, 0, 3, 4), "same spider, same place: nothing changes");
        assertEquals(1, ledger.count(owner));
    }

    @Test
    void limitOfTwoAndZeroTurnsTamingOff()
    {
        SpiderLedger ledger = new SpiderLedger();
        assertTrue(ledger.canTameMore(owner, 2));
        ledger.add(owner, a, 0, 0, 0);
        assertTrue(ledger.canTameMore(owner, 2));
        ledger.add(owner, b, 0, 0, 0);
        assertFalse(ledger.canTameMore(owner, 2));
        assertFalse(new SpiderLedger().canTameMore(owner, 0));
        assertTrue(ledger.canTameMore(other, 2), "the limit is per player");
    }

    @Test
    void forgetFreesAPlaceAndToleratesUnknownOwners()
    {
        SpiderLedger ledger = new SpiderLedger();
        ledger.add(owner, a, 0, 0, 0);
        assertTrue(ledger.forget(owner, a));
        assertFalse(ledger.forget(owner, a));
        assertFalse(ledger.forget(other, a));
        assertFalse(ledger.forget(null, a));
        assertEquals(0, ledger.count(owner));
    }

    @Test
    void touchReportsOnlyNewEntriesAndChunkChanges()
    {
        SpiderLedger ledger = new SpiderLedger();
        assertTrue(ledger.touch(owner, a, 0, 1, 1), "missing: listed again (the spider is the truth)");
        assertFalse(ledger.touch(owner, a, 0, 1, 1), "same chunk: no save needed");
        assertTrue(ledger.touch(owner, a, 0, 2, 1), "new chunk");
        assertTrue(ledger.touch(owner, a, -1, 2, 1), "new dimension");
        assertFalse(ledger.touch(null, a, 0, 0, 0), "no owner: nothing to list");
        SpiderLedger.Entry entry = ledger.entriesOf(owner)[0];
        assertEquals(-1, entry.dim);
        assertEquals(2, entry.chunkX);
    }

    @Test
    void spidersOverTheLimitFromAnOlderVersionAreKept()
    {
        SpiderLedger ledger = new SpiderLedger();
        ledger.touch(owner, a, 0, 0, 0);
        ledger.touch(owner, b, 0, 0, 0);
        ledger.touch(owner, c, 0, 0, 0);
        assertEquals(3, ledger.count(owner), "heartbeats list every living spider, whatever the limit");
        assertFalse(ledger.canTameMore(owner, 2), "only new tames are blocked");
    }

    @Test
    void pruneRemovesOnlyGhosts()
    {
        SpiderLedger ledger = new SpiderLedger();
        ledger.add(owner, a, 0, 0, 0);
        ledger.add(owner, b, 0, 5, 5);
        ledger.add(other, c, 0, 5, 5);
        int removed = ledger.prune(owner, new SpiderLedger.GhostCheck()
        {
            @Override
            public boolean isGhost(SpiderLedger.Entry entry)
            {
                return entry.chunkX == 5;
            }
        });
        assertEquals(1, removed);
        assertTrue(ledger.isListed(owner, a));
        assertFalse(ledger.isListed(owner, b));
        assertTrue(ledger.isListed(other, c), "other owners are not checked");
    }

    @Test
    void linesRoundTripWithTwoOwners()
    {
        SpiderLedger ledger = new SpiderLedger();
        ledger.add(owner, a, 0, -3, 7);
        ledger.add(owner, b, -1, 10, -20);
        ledger.add(other, c, 1, 0, 0);
        SpiderLedger copy = SpiderLedger.fromLines(ledger.toLines());
        assertEquals(2, copy.count(owner));
        assertEquals(1, copy.count(other));
        assertTrue(copy.isListed(owner, b));
        assertEquals(new java.util.HashSet<String>(ledger.toLines()), new java.util.HashSet<String>(copy.toLines()));
    }

    @Test
    void brokenLinesAreSkippedAndTheRestKept()
    {
        List<String> lines = new ArrayList<String>(Arrays.asList(
                owner + ";" + a + ";0;1;2",
                "garbage",
                owner + ";not-a-uuid;0;1;2",
                owner + ";" + b + ";0;x;2",
                null,
                other + ";" + c + ";0;0;0"));
        SpiderLedger ledger = SpiderLedger.fromLines(lines);
        assertEquals(1, ledger.count(owner));
        assertEquals(1, ledger.count(other));
    }
}
