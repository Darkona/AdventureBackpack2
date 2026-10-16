package com.darkona.adventurebackpack.common;

public final class DeathRoute
{
    private DeathRoute()
    {
    }

    public enum Route
    {
        NONE,
        KEEP,
        BAUBLES,
        GRAVE_INVENTORY,
        GRAVE_DROPS,
        PLACE,
        DROP
    }

    public static Route beforeDeath(boolean hasWearable, boolean inBauble, boolean keepInventory, boolean graveCompat, boolean inventoryGrave, boolean freeSlot)
    {
        if (!hasWearable || inBauble || keepInventory) return Route.NONE;
        return graveCompat && inventoryGrave && freeSlot ? Route.GRAVE_INVENTORY : Route.NONE;
    }

    public static Route onDrops(boolean hasWearable, boolean inBauble, boolean keepInventory, boolean dropsCancelled, boolean graveCompat, boolean dropsGrave, boolean placeOnDeath, boolean placeable)
    {
        if (!hasWearable) return Route.NONE;
        if (inBauble) return Route.BAUBLES;
        if (keepInventory) return Route.KEEP;
        if (graveCompat && dropsGrave && !dropsCancelled) return Route.GRAVE_DROPS;
        return placeOnDeath && placeable ? Route.PLACE : Route.DROP;
    }
}
