package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.util.TickThrottle;

/**
 * When a client draws the cosmetic effects of the Flamethrower, from world time and the player's entity id, so every
 * client derives the same beat without a packet: a burst of flames, smoke every second burst, the burner sound about
 * once a second and the pilot flame at the nozzle.
 */
public final class ParticleBeat
{
    private ParticleBeat() {}

    /** Ticks between two pilot flames at the nozzle of a lit pack. */
    public static final int PILOT_TICKS = 10;
    /** Ticks between two plays of the burner sound while firing. */
    public static final int SOUND_TICKS = 20;

    /** A burst of flames: the same beat the server fires on. */
    public static boolean flames(long worldTime, int entityId, int fireIntervalTicks)
    {
        return TickThrottle.every(worldTime, entityId, fireIntervalTicks);
    }

    /** Smoke goes with every second burst. */
    public static boolean smoke(long worldTime, int entityId, int fireIntervalTicks)
    {
        return TickThrottle.every(worldTime, entityId, Math.max(1, fireIntervalTicks) * 2);
    }

    /** The burner sound while firing. */
    public static boolean sound(long worldTime, int entityId)
    {
        return TickThrottle.every(worldTime, entityId, SOUND_TICKS);
    }

    /** The small pilot flame at the nozzle while the pack is lit and the nozzle in hand. */
    public static boolean pilot(long worldTime, int entityId)
    {
        return TickThrottle.every(worldTime, entityId, PILOT_TICKS);
    }

    /** How many flame particles one burst draws: the configured number, never negative. */
    public static int flameCount(int configured)
    {
        return Math.max(0, configured);
    }
}
