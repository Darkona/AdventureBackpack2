package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BucketRuleTest {
    private static final int BUCKET = 1000;

    @Test
    void takesTheBlockWhenTheWholeBucketFits() {
        assertTrue(BucketRule.canTakeSource(1000, BUCKET));
    }

    @Test
    void leavesTheBlockWhenOnlyPartFits() {
        assertFalse(BucketRule.canTakeSource(999, BUCKET));
        assertFalse(BucketRule.canTakeSource(500, BUCKET));
    }

    @Test
    void leavesTheBlockWhenNothingFits() {
        assertFalse(BucketRule.canTakeSource(0, BUCKET));
    }

    @Test
    void noFluidIsLostWithANearlyFullTank() {
        // the old rule took the block whenever anything fitted: with 3500/4000 mB, 500 mB of the bucket vanished
        int capacity = 4000;
        int amount = 3500;
        int accepted = Math.min(BUCKET, capacity - amount);
        int worldFluid = BUCKET;
        if (BucketRule.canTakeSource(accepted, BUCKET)) {
            amount += accepted;
            worldFluid -= BUCKET;
        }
        assertEquals(3500 + 1000, amount + worldFluid);
    }
}
