package com.darkona.adventurebackpack.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RefillRulesTest
{
    @Test
    void topsUpToTheHandMaximum()
    {
        assertEquals(63, RefillRules.amount(1, 64, 100));
        assertEquals(10, RefillRules.amount(0, 64, 10));
        assertEquals(0, RefillRules.amount(64, 64, 10));
    }

    @Test
    void nothingToTakeOrNoRoomMeansZero()
    {
        assertEquals(0, RefillRules.amount(3, 64, 0));
        assertEquals(0, RefillRules.amount(3, 0, 20));
        assertEquals(0, RefillRules.amount(70, 64, 20));
    }
}
