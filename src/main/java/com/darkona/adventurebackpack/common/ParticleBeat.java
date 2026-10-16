package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.util.TickThrottle;

public final class ParticleBeat
{
    private ParticleBeat() {}

    public static final int PILOT_TICKS = 10;
    public static final int SOUND_TICKS = 20;

    public static boolean flames(long worldTime, int entityId, int fireIntervalTicks)
    {
        return TickThrottle.every(worldTime, entityId, fireIntervalTicks);
    }

    public static boolean smoke(long worldTime, int entityId, int fireIntervalTicks)
    {
        return TickThrottle.every(worldTime, entityId, Math.max(1, fireIntervalTicks) * 2);
    }

    public static boolean sound(long worldTime, int entityId)
    {
        return TickThrottle.every(worldTime, entityId, SOUND_TICKS);
    }

    public static boolean pilot(long worldTime, int entityId)
    {
        return TickThrottle.every(worldTime, entityId, PILOT_TICKS);
    }

    public static int flameCount(int configured)
    {
        return Math.max(0, configured);
    }
}
