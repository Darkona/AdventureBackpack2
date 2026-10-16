package com.darkona.adventurebackpack.playerProperties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SyncIntervalTest
{
    @Test
    void firstChangeIsSentAtOnce()
    {
        assertTrue(SyncInterval.due(0, Integer.MIN_VALUE, 4));
        assertTrue(SyncInterval.due(123456, Integer.MIN_VALUE, 4));
    }

    @Test
    void changesEveryTickAreSentEveryIntervalTicks()
    {
        int last = Integer.MIN_VALUE, sends = 0;
        for (int t = 100; t < 140; t++)
        {
            if (SyncInterval.due(t, last, 4))
            {
                last = t;
                sends++;
            }
        }
        org.junit.jupiter.api.Assertions.assertEquals(10, sends, "40 ticks of changes, one send every 4");
    }

    @Test
    void intervalOneIsEveryTick()
    {
        assertTrue(SyncInterval.due(5, 5, 1));
        assertTrue(SyncInterval.due(5, 4, 0));
    }

    @Test
    void waitsInsideTheIntervalAndNotAfter()
    {
        assertFalse(SyncInterval.due(101, 100, 4));
        assertFalse(SyncInterval.due(103, 100, 4));
        assertTrue(SyncInterval.due(104, 100, 4));
    }

    @Test
    void aCounterThatWentBackSendsAtOnce()
    {
        assertTrue(SyncInterval.due(3, 5000, 4), "a respawned entity counts from 0 again");
    }
}
