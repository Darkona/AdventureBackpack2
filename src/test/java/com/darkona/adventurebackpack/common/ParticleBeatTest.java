package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ParticleBeatTest
{
    private static int count(int ticks, java.util.function.LongPredicate beat)
    {
        int n = 0;
        for (long t = 0; t < ticks; t++)
        {
            if (beat.test(t)) n++;
        }
        return n;
    }

    @Test
    void flamesComeOncePerBurstOnTheServersBeat()
    {
        assertEquals(5, count(20, t -> ParticleBeat.flames(t, 7, 4)));
        // the same beat as the server's burst (TickThrottle with the same entity id)
        for (long t = 0; t < 40; t++)
        {
            assertEquals(com.darkona.adventurebackpack.util.TickThrottle.every(t, 7, 4), ParticleBeat.flames(t, 7, 4));
        }
    }

    @Test
    void smokeComesWithEverySecondBurst()
    {
        assertEquals(5, count(40, t -> ParticleBeat.smoke(t, 3, 4)));
        for (long t = 0; t < 80; t++)
        {
            if (ParticleBeat.smoke(t, 3, 4)) assertTrue(ParticleBeat.flames(t, 3, 4), "smoke only on a burst tick");
        }
    }

    @Test
    void thePilotFlickersEveryHalfSecond()
    {
        assertEquals(6, count(60, t -> ParticleBeat.pilot(t, 11)));
    }

    @Test
    void differentPlayersDoNotAllBeatOnTheSameTick()
    {
        int same = 0;
        for (long t = 0; t < 20; t++)
        {
            if (ParticleBeat.pilot(t, 1) && ParticleBeat.pilot(t, 2)) same++;
        }
        assertEquals(0, same);
    }

    @Test
    void theFlameCountIsNeverNegative()
    {
        assertEquals(6, ParticleBeat.flameCount(6));
        assertEquals(0, ParticleBeat.flameCount(-3));
    }
}
