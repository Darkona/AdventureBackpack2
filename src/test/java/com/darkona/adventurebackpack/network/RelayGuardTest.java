package com.darkona.adventurebackpack.network;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RelayGuardTest {
    private static final long LONG_AGO = Long.MIN_VALUE / 2;

    @Test
    void relaysAValidFirstPacket() {
        assertTrue(RelayGuard.allowed(true, true, 0, 1000, LONG_AGO));
    }

    @Test
    void dropsATypeTheSenderDoesNotMake() {
        assertFalse(RelayGuard.allowed(false, true, 0, 1000, LONG_AGO));
    }

    @Test
    void dropsAnEntityThatIsNotTheSenderNorItsMount() {
        assertFalse(RelayGuard.allowed(true, false, 0, 1000, LONG_AGO));
    }

    @Test
    void dropsAFarEntity() {
        assertTrue(RelayGuard.allowed(true, true, RelayGuard.MAX_DISTANCE_SQ, 1000, LONG_AGO));
        assertFalse(RelayGuard.allowed(true, true, RelayGuard.MAX_DISTANCE_SQ + 0.01, 1000, LONG_AGO));
        assertFalse(RelayGuard.allowed(true, true, Double.MAX_VALUE, 1000, LONG_AGO));
    }

    @Test
    void limitsHowOftenOnePlayerRelays() {
        assertFalse(RelayGuard.allowed(true, true, 0, 1000, 1000));
        assertFalse(RelayGuard.allowed(true, true, 0, 1000 + RelayGuard.MIN_INTERVAL_TICKS - 1, 1000));
        assertTrue(RelayGuard.allowed(true, true, 0, 1000 + RelayGuard.MIN_INTERVAL_TICKS, 1000));
    }

    @Test
    void aClockThatWentBackDoesNotBlockForever() {
        assertTrue(RelayGuard.allowed(true, true, 0, 5, 1_000_000));
    }

    @Test
    void floodIsCutToOnePacketPerInterval() {
        long last = LONG_AGO;
        int relayed = 0;
        for (long tick = 0; tick < 100; tick++) {
            for (int i = 0; i < 50; i++) { // a modified client sends 50 packets per tick
                if (RelayGuard.allowed(true, true, 0, tick, last)) {
                    relayed++;
                    last = tick;
                }
            }
        }
        assertTrue(relayed <= 100 / RelayGuard.MIN_INTERVAL_TICKS + 1);
    }
}
