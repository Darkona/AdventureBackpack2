package com.darkona.adventurebackpack.common;

import static com.darkona.adventurebackpack.common.SpiderCare.Action.HEAL;
import static com.darkona.adventurebackpack.common.SpiderCare.Action.MOUNT;
import static com.darkona.adventurebackpack.common.SpiderCare.Action.NONE;
import static com.darkona.adventurebackpack.common.SpiderCare.Action.RELEASE;
import static com.darkona.adventurebackpack.common.SpiderCare.Action.TOGGLE_SIT;
import static com.darkona.adventurebackpack.common.SpiderCare.Hand.EMPTY;
import static com.darkona.adventurebackpack.common.SpiderCare.Hand.HEAL_ITEM;
import static com.darkona.adventurebackpack.common.SpiderCare.Hand.OTHER;
import static com.darkona.adventurebackpack.common.SpiderCare.Hand.TAME_ITEM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.common.SpiderCare.Hand;
import org.junit.jupiter.api.Test;

class SpiderCareTest
{
    private static SpiderCare.Action act(boolean owner, boolean ridden, boolean sneaking, Hand hand, boolean canRide, boolean hurt)
    {
        return SpiderCare.interactAction(owner, ridden, sneaking, hand, canRide, hurt);
    }

    @Test
    void strangersAndRiddenSpidersIgnoreClicks()
    {
        for (Hand hand : Hand.values())
        {
            assertEquals(NONE, act(false, false, false, hand, true, true));
            assertEquals(NONE, act(false, false, true, hand, true, true));
            assertEquals(NONE, act(true, true, true, hand, true, true));
            assertEquals(NONE, act(true, true, false, hand, true, true));
        }
    }

    @Test
    void sneakingWithTheTameItemSetsItFree()
    {
        assertEquals(RELEASE, act(true, false, true, TAME_ITEM, true, false));
        assertEquals(RELEASE, act(true, false, true, TAME_ITEM, false, true));
    }

    @Test
    void sneakingWithAnythingElseSitsOrStands()
    {
        for (Hand hand : new Hand[] {EMPTY, HEAL_ITEM, OTHER})
        {
            assertEquals(TOGGLE_SIT, act(true, false, true, hand, true, true));
            assertEquals(TOGGLE_SIT, act(true, false, true, hand, false, false), "with or without the backpack");
        }
    }

    @Test
    void theHealItemHealsOnlyAHurtSpider()
    {
        assertEquals(HEAL, act(true, false, false, HEAL_ITEM, true, true));
        assertEquals(HEAL, act(true, false, false, HEAL_ITEM, false, true));
        assertEquals(NONE, act(true, false, false, HEAL_ITEM, true, false), "full health: nothing, like a wolf");
    }

    @Test
    void aPlainClickRidesOnlyWithTheBackpack()
    {
        for (Hand hand : new Hand[] {EMPTY, TAME_ITEM, OTHER})
        {
            assertEquals(MOUNT, act(true, false, false, hand, true, false));
            assertEquals(NONE, act(true, false, false, hand, false, false), "no backpack (or ability off): no ride");
        }
    }

    @Test
    void healthRules()
    {
        assertEquals(20F, SpiderCare.healedHealth(18F, 20F, 4F));
        assertEquals(14F, SpiderCare.healedHealth(10F, 20F, 4F));
        assertEquals(10F, SpiderCare.healedHealth(10F, 20F, -4F), "never hurts");
        assertTrue(SpiderCare.canHeal(19.5F, 20F));
        assertFalse(SpiderCare.canHeal(20F, 20F));
        assertEquals(30F, SpiderCare.tamedHealth(30F));
        assertEquals(20F, SpiderCare.clampLoadedHealth(28F, 20F), "a lower configured maximum caps loaded health");
        assertEquals(12F, SpiderCare.clampLoadedHealth(12F, 20F));
    }

    @Test
    void withBitSetsAndClearsOnlyItsBits()
    {
        assertEquals((byte) 3, SpiderCare.withBit((byte) 1, 2, true));
        assertEquals((byte) 1, SpiderCare.withBit((byte) 3, 2, false));
        assertEquals((byte) 0x82, SpiderCare.withBit((byte) 0x83, 1, false));
        assertEquals((byte) 2, SpiderCare.withBit((byte) 2, 2, true));
    }
}
