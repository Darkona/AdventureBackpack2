package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.darkona.adventurebackpack.common.KeyActions.Action;
import com.darkona.adventurebackpack.common.KeyActions.Held;
import com.darkona.adventurebackpack.common.KeyActions.Key;
import com.darkona.adventurebackpack.common.KeyActions.Worn;
import org.junit.jupiter.api.Test;

/** The priority table of the four keys (notes/diseno-teclas.md, wiki Controls). */
class KeyActionsTest {
    private static Action resolve(Key key, Worn worn, Held held, boolean boat) {
        return KeyActions.resolve(key, worn, held, boat, true);
    }

    @Test
    void gearOpensTheWornGear() {
        assertEquals(Action.OPEN_BACKPACK, resolve(Key.GEAR, Worn.BACKPACK, Held.OTHER, false));
        assertEquals(Action.OPEN_COPTER, resolve(Key.GEAR, Worn.COPTER, Held.OTHER, false));
        assertEquals(Action.OPEN_JETPACK, resolve(Key.GEAR, Worn.JETPACK, Held.HOSE, true));
        assertEquals(Action.OPEN_SCUBA, resolve(Key.GEAR, Worn.SCUBA, Held.OTHER, false));
        assertEquals(Action.OPEN_FLAMETHROWER, resolve(Key.GEAR, Worn.FLAMETHROWER, Held.CROSSBOW, false));
        assertEquals(Action.NONE, resolve(Key.GEAR, Worn.NONE, Held.OTHER, false));
    }

    @Test
    void abilityTurnsTheWornGearOnAndOff() {
        assertEquals(Action.TOGGLE_ABILITIES, resolve(Key.ABILITY, Worn.BACKPACK, Held.HOSE, false));
        assertEquals(Action.TOGGLE_COPTER, resolve(Key.ABILITY, Worn.COPTER, Held.OTHER, false));
        assertEquals(Action.TOGGLE_JETPACK, resolve(Key.ABILITY, Worn.JETPACK, Held.OTHER, true));
        assertEquals(Action.TOGGLE_PILOT, resolve(Key.ABILITY, Worn.FLAMETHROWER, Held.OTHER, false));
        assertEquals(Action.NONE, resolve(Key.ABILITY, Worn.SCUBA, Held.OTHER, false), "the scuba gear always works");
        assertEquals(Action.NONE, resolve(Key.ABILITY, Worn.NONE, Held.CROSSBOW, false));
    }

    @Test
    void modeGoesBoatThenCopterThenHose() {
        assertEquals(Action.BOAT_MOTOR, resolve(Key.MODE, Worn.COPTER, Held.HOSE, true));
        assertEquals(Action.COPTER_MODE, resolve(Key.MODE, Worn.COPTER, Held.HOSE, false));
        assertEquals(Action.HOSE_TANK, resolve(Key.MODE, Worn.BACKPACK, Held.HOSE, false));
        assertEquals(Action.HOSE_TANK, resolve(Key.MODE, Worn.NONE, Held.HOSE, false));
        assertEquals(Action.NONE, resolve(Key.MODE, Worn.BACKPACK, Held.CYCLABLE_TOOL, false));
        assertEquals(Action.NONE, resolve(Key.MODE, Worn.JETPACK, Held.OTHER, false));
    }

    @Test
    void stateGoesCrossbowThenHoseThenTools() {
        assertEquals(Action.CROSSBOW_MODE, resolve(Key.STATE, Worn.BACKPACK, Held.CROSSBOW, true));
        assertEquals(Action.HOSE_MODE, resolve(Key.STATE, Worn.BACKPACK, Held.HOSE, false));
        assertEquals(Action.CYCLE_TOOL, resolve(Key.STATE, Worn.BACKPACK, Held.CYCLABLE_TOOL, false));
        assertEquals(Action.NONE, resolve(Key.STATE, Worn.COPTER, Held.CYCLABLE_TOOL, false), "tools need a backpack");
        assertEquals(Action.NONE, resolve(Key.STATE, Worn.BACKPACK, Held.OTHER, false));
    }

    @Test
    void theToolSettingOnlyStopsTheHoseAndTheTools() {
        assertEquals(Action.CROSSBOW_MODE, KeyActions.resolve(Key.STATE, Worn.NONE, Held.CROSSBOW, false, false));
        assertEquals(Action.NONE, KeyActions.resolve(Key.STATE, Worn.BACKPACK, Held.HOSE, false, false));
        assertEquals(Action.NONE, KeyActions.resolve(Key.STATE, Worn.BACKPACK, Held.CYCLABLE_TOOL, false, false));
        assertEquals(Action.HOSE_TANK, KeyActions.resolve(Key.MODE, Worn.BACKPACK, Held.HOSE, false, false));
    }

    @Test
    void unknownKeysDoNothing() {
        assertEquals(Action.NONE, KeyActions.resolve(null, Worn.BACKPACK, Held.HOSE, true, true));
        assertNull(Key.fromId(-1));
        assertNull(Key.fromId(Key.values().length));
        assertEquals(Key.STATE, Key.fromId(Key.STATE.ordinal()));
    }

    @Test
    void sneakGoesBackwardsAndForgedDirectionsAreOneStep() {
        assertEquals(1, KeyActions.direction(false));
        assertEquals(-1, KeyActions.direction(true));
        assertEquals(-1, KeyActions.clampDirection(-100));
        assertEquals(1, KeyActions.clampDirection(0));
        assertEquals(1, KeyActions.clampDirection(77));
    }
}
