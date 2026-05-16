package com.darkona.adventurebackpack.common;

/**
 * Where the worn wearable goes when its wearer dies. Pure: the event handlers gather the facts and the
 * unit test covers every combination.
 * <p>
 * Grave mods of 1.7.10 take the items in two ways. GraveStone (modid {@code GraveStone}) empties the player's
 * inventory in {@code LivingDeathEvent} at HIGH priority, so the wearable must be in the main inventory before
 * that ({@link #beforeDeath}). OpenBlocks reads {@code PlayerDropsEvent.drops} at LOW priority, so the wearable
 * must be in the drop list before that ({@link #onDrops}, run at HIGH). Baubles drops its own slots in
 * {@code PlayerDropsEvent}; a backpack worn in a bauble slot is left to it.
 *
 * @author Javier Darkona
 */
public final class DeathRoute
{
    private DeathRoute()
    {
    }

    public enum Route
    {
        /** Nothing to do: no wearable, or another step already took it. */
        NONE,
        /** keepInventory: the wearable stays with the player and comes back on respawn. */
        KEEP,
        /** Worn in a bauble slot: Baubles drops it (and grave mods that read Baubles take it). Only unlink it. */
        BAUBLES,
        /** Moved into the main inventory before a GraveStone grave empties it. */
        GRAVE_INVENTORY,
        /** Dropped into the drop list for a grave mod that reads the drops (OpenBlocks). Never placed. */
        GRAVE_DROPS,
        /** Placed as a block near the death point (backpack.placeOnDeath); falls back to a drop when no spot is found. */
        PLACE,
        /** Dropped as an item. */
        DROP
    }

    /**
     * {@code LivingDeathEvent}, before GraveStone collects the inventory.
     *
     * @param graveCompat      config compat.graves.enabled
     * @param inventoryGrave   a grave mod that empties the inventory will make a grave for this death
     * @param freeSlot         the main inventory has an empty slot
     */
    public static Route beforeDeath(boolean hasWearable, boolean inBauble, boolean keepInventory, boolean graveCompat, boolean inventoryGrave, boolean freeSlot)
    {
        if (!hasWearable || inBauble || keepInventory) return Route.NONE;
        return graveCompat && inventoryGrave && freeSlot ? Route.GRAVE_INVENTORY : Route.NONE;
    }

    /**
     * {@code PlayerDropsEvent}, before grave mods read the drop list.
     *
     * @param dropsCancelled  another mod cancelled the drops event (no grave mod acts on a cancelled event)
     * @param graveCompat     config compat.graves.enabled
     * @param dropsGrave      a grave mod that reads the drop list is active
     * @param placeOnDeath    config backpack.placeOnDeath
     * @param placeable       the wearable can be placed as a block (only the Adventure Backpack)
     */
    public static Route onDrops(boolean hasWearable, boolean inBauble, boolean keepInventory, boolean dropsCancelled, boolean graveCompat, boolean dropsGrave, boolean placeOnDeath, boolean placeable)
    {
        if (!hasWearable) return Route.NONE;
        if (inBauble) return Route.BAUBLES;
        if (keepInventory) return Route.KEEP;
        if (graveCompat && dropsGrave && !dropsCancelled) return Route.GRAVE_DROPS;
        return placeOnDeath && placeable ? Route.PLACE : Route.DROP;
    }
}
