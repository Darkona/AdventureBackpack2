package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.darkona.adventurebackpack.common.DeathRoute.Route;
import org.junit.jupiter.api.Test;

/** Where the worn wearable goes on death, with and without grave mods (#117, #128, #86). */
class DeathRouteTest {
    // onDrops(hasWearable, inBauble, keepInventory, dropsCancelled, graveCompat, dropsGrave, placeOnDeath, placeable)

    @Test
    void withoutGraveModTheBackpackIsPlaced() {
        assertEquals(Route.PLACE, DeathRoute.onDrops(true, false, false, false, true, false, true, true));
    }

    @Test
    void withoutGraveModAndPlaceOffItDrops() {
        assertEquals(Route.DROP, DeathRoute.onDrops(true, false, false, false, true, false, false, true));
    }

    @Test
    void copterAndJetpackAreNeverPlaced() {
        assertEquals(Route.DROP, DeathRoute.onDrops(true, false, false, false, false, false, true, false));
    }

    @Test
    void openBlocksGraveTakesItFromTheDropsInsteadOfPlacing() {
        assertEquals(Route.GRAVE_DROPS, DeathRoute.onDrops(true, false, false, false, true, true, true, true));
    }

    @Test
    void graveCompatOffKeepsTheOldBehaviour() {
        assertEquals(Route.PLACE, DeathRoute.onDrops(true, false, false, false, false, true, true, true));
    }

    @Test
    void cancelledDropsMakeNoGraveSoTheBackpackIsPlaced() {
        assertEquals(Route.PLACE, DeathRoute.onDrops(true, false, false, true, true, true, true, true));
    }

    @Test
    void keepInventoryKeepsIt() {
        assertEquals(Route.KEEP, DeathRoute.onDrops(true, false, true, false, true, true, true, true));
    }

    @Test
    void baubleSlotIsLeftToBaublesEvenWithKeepInventory() {
        assertEquals(Route.BAUBLES, DeathRoute.onDrops(true, true, false, false, true, true, true, true));
        assertEquals(Route.BAUBLES, DeathRoute.onDrops(true, true, true, false, true, true, true, true));
    }

    @Test
    void nothingWornNothingToDo() {
        assertEquals(Route.NONE, DeathRoute.onDrops(false, false, false, false, true, true, true, true));
    }

    // beforeDeath(hasWearable, inBauble, keepInventory, graveCompat, inventoryGrave, freeSlot)

    @Test
    void graveStoneGetsItThroughTheInventory() {
        assertEquals(Route.GRAVE_INVENTORY, DeathRoute.beforeDeath(true, false, false, true, true, true));
    }

    @Test
    void fullInventoryFallsBackToTheDropsStep() {
        assertEquals(Route.NONE, DeathRoute.beforeDeath(true, false, false, true, true, false));
    }

    @Test
    void noGraveStoneGraveNoMove() {
        assertEquals(Route.NONE, DeathRoute.beforeDeath(true, false, false, true, false, true));
        assertEquals(Route.NONE, DeathRoute.beforeDeath(true, false, false, false, true, true));
        assertEquals(Route.NONE, DeathRoute.beforeDeath(true, false, true, true, true, true));
        assertEquals(Route.NONE, DeathRoute.beforeDeath(true, true, false, true, true, true));
    }
}
