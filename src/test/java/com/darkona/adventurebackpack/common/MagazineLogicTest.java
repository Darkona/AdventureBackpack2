package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.darkona.adventurebackpack.common.CrossbowLogic.Action;
import org.junit.jupiter.api.Test;

class MagazineLogicTest {
    private static final int CAP = 6;
    private static final String ARROW = MagazineLogic.LEGACY_AMMO;
    private static final String BOLT = MagazineLogic.BOLT_AMMO;

    @Test
    void anOldCartridgeWithoutChargesIsAFullMagazineOfSix() {
        assertEquals(6, MagazineLogic.charges(false, 0, CAP));
        assertEquals(6, MagazineLogic.charges(false, 0, 12));
        assertEquals(4, MagazineLogic.charges(false, 0, 4), "never more than the capacity");
        assertEquals(ARROW, MagazineLogic.ammoType(null));
        assertEquals(ARROW, MagazineLogic.ammoType(""));
        assertEquals(BOLT, MagazineLogic.ammoType(BOLT));
    }

    @Test
    void taggedChargesStayBetweenZeroAndTheCapacity() {
        assertEquals(0, MagazineLogic.charges(true, 0, CAP));
        assertEquals(3, MagazineLogic.charges(true, 3, CAP));
        assertEquals(0, MagazineLogic.charges(true, -2, CAP));
        assertEquals(CAP, MagazineLogic.charges(true, 40, CAP));
    }

    @Test
    void anEmptyMagazineTakesAnyAmmunitionALoadedOneOnlyItsOwn() {
        assertTrue(MagazineLogic.accepts(0, null, BOLT));
        assertTrue(MagazineLogic.accepts(0, ARROW, BOLT));
        assertTrue(MagazineLogic.accepts(3, BOLT, BOLT));
        assertFalse(MagazineLogic.accepts(3, BOLT, ARROW));
        assertTrue(MagazineLogic.accepts(3, null, ARROW), "an old cartridge holds arrows");
        assertFalse(MagazineLogic.accepts(0, null, null));
    }

    @Test
    void oneChargePerClickUntilFull() {
        assertEquals(1, MagazineLogic.chargesToLoad(0, CAP, 64, 4));
        assertEquals(1, MagazineLogic.chargesToLoad(5, CAP, 64, 4));
        assertEquals(0, MagazineLogic.chargesToLoad(CAP, CAP, 64, 4));
        assertEquals(0, MagazineLogic.chargesToLoad(2, CAP, 0, 4), "no ammunition");
    }

    @Test
    void zeroTicksLoadsTheWholeRoomAtOnceButNotMoreThanTheAmmunition() {
        assertEquals(CAP, MagazineLogic.chargesToLoad(0, CAP, 64, 0));
        assertEquals(2, MagazineLogic.chargesToLoad(4, CAP, 64, 0));
        assertEquals(3, MagazineLogic.chargesToLoad(0, CAP, 3, 0));
        assertEquals(CAP, MagazineLogic.chargesToLoad(0, CAP, Integer.MAX_VALUE, 0), "creative");
    }

    @Test
    void heldClicksFillAPartlyLoadedMagazine() {
        int charges = 2;
        int arrows = 10;
        long readyAt = 0;
        for (long now = 100; now < 200; now += 4) {
            if (MagazineLogic.isLoading(now, readyAt, 4)) continue;
            int load = MagazineLogic.chargesToLoad(charges, CAP, arrows, 4);
            if (load == 0) break;
            charges += load;
            arrows -= load;
            readyAt = now + 4;
        }
        assertEquals(CAP, charges);
        assertEquals(6, arrows, "4 arrows went in, nothing more");
    }

    @Test
    void reloadRecipeAddsTheAmmunitionUpToTheCapacity() {
        assertEquals(6, MagazineLogic.recipeCharges(0, CAP, 6));
        assertEquals(5, MagazineLogic.recipeCharges(2, CAP, 3));
        assertEquals(-1, MagazineLogic.recipeCharges(4, CAP, 3), "more than the free room");
        assertEquals(-1, MagazineLogic.recipeCharges(0, CAP, 0), "a magazine alone is not a recipe");
    }

    @Test
    void oldCrossbowsHoldTheCartridgeTheirBoltsCameFrom() {
        assertTrue(MagazineLogic.hasMagazine(false, false, 3));
        assertFalse(MagazineLogic.hasMagazine(false, false, 0));
        assertTrue(MagazineLogic.hasMagazine(true, true, 0));
        assertFalse(MagazineLogic.hasMagazine(true, false, 0));
    }

    @Test
    void aDryClickGivesBackOnlyAnEmptyMagazine() {
        assertTrue(MagazineLogic.ejectOnDry(0, true));
        assertFalse(MagazineLogic.ejectOnDry(1, true), "a hook shot that costs more keeps the partial magazine");
        assertFalse(MagazineLogic.ejectOnDry(0, false));
    }

    @Test
    void boltsHitHarderThanArrows() {
        assertEquals(1.5, MagazineLogic.damageMultiplier(BOLT, 1.5), 1e-9);
        assertEquals(1.0, MagazineLogic.damageMultiplier(ARROW, 1.5), 1e-9);
        assertEquals(1.0, MagazineLogic.damageMultiplier(null, 1.5), 1e-9, "old crossbows fire arrows");
        assertEquals(1.0, MagazineLogic.damageMultiplier("othermod:dart", 1.5), 1e-9);
    }

    @Test
    void durabilityBarShowsTheEmptyPart() {
        assertEquals(0.0, MagazineLogic.emptyFraction(CAP, CAP), 1e-9);
        assertEquals(1.0, MagazineLogic.emptyFraction(0, CAP), 1e-9);
        assertEquals(0.5, MagazineLogic.emptyFraction(3, CAP), 1e-9);
    }

    /**
     * The crossbow and its magazines as plain numbers: firing, emptying, swapping by sneaking and dry clicks. The
     * total of charges only drops by the bolts fired, and the number of magazines never changes.
     */
    @Test
    void swappingNeverLosesChargesOrMagazines() {
        List<Integer> inventory = new ArrayList<Integer>();
        inventory.add(CAP);
        inventory.add(3);
        inventory.add(0);
        int ammo = 0;
        boolean magazine = false;
        int fired = 0;
        int startCharges = CAP + 3;
        int magazines = inventory.size();
        boolean[] sneaks = {false, false, true, false, false, false, false, true, false, false, false, false, false, false, false, false};

        for (boolean sneaking : sneaks) {
            int slot = firstLoaded(inventory);
            Action action = CrossbowLogic.decide(ammo, CAP, 0, 0, 30, sneaking, slot >= 0);
            switch (action) {
                case FIRE:
                    ammo--;
                    fired++;
                    break;
                case RELOAD:
                    int charges = inventory.remove(slot);
                    if (magazine) inventory.add(ammo);
                    ammo = charges;
                    magazine = true;
                    break;
                case DRY:
                    if (MagazineLogic.ejectOnDry(ammo, magazine)) {
                        inventory.add(0);
                        magazine = false;
                    }
                    break;
                default:
                    break;
            }
            int total = ammo;
            for (int c : inventory) total += c;
            assertEquals(startCharges - fired, total, "charges after " + action);
            assertEquals(magazines, inventory.size() + (magazine ? 1 : 0), "magazines after " + action);
        }
        assertEquals(startCharges, fired, "every charge was fired");
        assertFalse(magazine, "the last dry click gave the empty magazine back");
        for (int c : inventory) assertEquals(0, c);
    }

    private static int firstLoaded(List<Integer> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i) > 0) return i;
        }
        return -1;
    }
}
