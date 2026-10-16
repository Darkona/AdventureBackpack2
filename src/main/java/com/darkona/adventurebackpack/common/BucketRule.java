package com.darkona.adventurebackpack.common;

public final class BucketRule
{
    private BucketRule() {}

    public static boolean canTakeSource(int accepted, int bucket)
    {
        return accepted >= bucket;
    }
}
