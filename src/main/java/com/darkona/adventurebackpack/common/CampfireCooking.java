package com.darkona.adventurebackpack.common;

/**
 * Decisions of the campfire's cooking, pure so the unit test covers them. The campfire has one virtual slot: a right
 * click with a raw item puts up to campfire.cooking.maxPerUse of it on the fire, and each unit cooks in ticksPerItem
 * ticks. The cooked items go to the player who put them there when that player is close, or pop out on top of the
 * campfire. The timer is a world time in the tile and a scheduled block update: nothing ticks while the fire is idle.
 */
public final class CampfireCooking
{
    private CampfireCooking()
    {
    }

    /**
     * True when the campfire takes this item.
     *
     * @param enabled      enabled.campfire and campfire.cooking.enabled
     * @param hasResult    the furnace has a recipe for the item
     * @param resultIsFood the furnace result is food
     * @param onlyFood     campfire.cooking.onlyFood
     */
    public static boolean canCook(boolean enabled, boolean hasResult, boolean resultIsFood, boolean onlyFood)
    {
        return enabled && hasResult && (resultIsFood || !onlyFood);
    }

    /**
     * How many items a right click puts on the fire. The slot takes one kind of item at a time and at most
     * {@code capacity} of it; the rest stays in the player's hand.
     *
     * @param held     items in the player's hand
     * @param inSlot   items already on the fire (0 when idle)
     * @param sameItem the held item is the one already on the fire
     * @param capacity campfire.cooking.maxPerUse
     */
    public static int take(int held, int inSlot, boolean sameItem, int capacity)
    {
        if (held <= 0) return 0;
        if (inSlot > 0 && !sameItem) return 0;
        int room = capacity - inSlot;
        return room <= 0 ? 0 : Math.min(held, room);
    }

    /** True when the item on the fire is done: the world clock reached the time saved when it started. */
    public static boolean ready(long now, long readyAt)
    {
        return now >= readyAt;
    }

    /**
     * Ticks to wait before the next check: the time left, at least one tick. A block update that came too early
     * (a random tick, or a scheduled one left from before) uses it to schedule the right one again.
     */
    public static int delay(long now, long readyAt)
    {
        long left = readyAt - now;
        if (left < 1) return 1;
        return left > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) left;
    }

    /** True when the cooked item goes straight into the player's inventory; 0 range always drops it. */
    public static boolean inReturnRange(double distanceSq, double range)
    {
        return range > 0 && distanceSq <= range * range;
    }
}
