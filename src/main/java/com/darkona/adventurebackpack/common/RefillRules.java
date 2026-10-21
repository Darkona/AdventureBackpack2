package com.darkona.adventurebackpack.common;

public final class RefillRules
{
    private RefillRules() {}

    public static int amount(int handCount, int handMax, int sourceCount)
    {
        if (handMax <= 0 || sourceCount <= 0) return 0;
        return Math.max(0, Math.min(handMax - Math.max(0, handCount), sourceCount));
    }
}
