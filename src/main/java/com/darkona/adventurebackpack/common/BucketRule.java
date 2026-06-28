package com.darkona.adventurebackpack.common;

/**
 * A fluid source block in the world holds exactly one bucket. The hose takes it only when the tank accepts the whole
 * bucket, like a vanilla bucket: taking the block with less room destroyed the rest of the fluid.
 */
public final class BucketRule
{
    private BucketRule() {}

    /** True when a source block of {@code bucket} millibuckets may be taken into a tank that accepts {@code accepted}. */
    public static boolean canTakeSource(int accepted, int bucket)
    {
        return accepted >= bucket;
    }
}
