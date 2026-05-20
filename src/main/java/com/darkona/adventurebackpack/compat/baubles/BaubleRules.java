package com.darkona.adventurebackpack.compat.baubles;

/**
 * Pure decisions for a backpack in a Baubles slot. The worn wearable has one place: either the backpack's own
 * slot (BackpackProperty) or one Baubles slot, never both.
 *
 * @author Javier Darkona
 */
public final class BaubleRules
{
    private BaubleRules()
    {
    }

    public enum Action
    {
        /** Nothing changes. */
        NONE,
        /** Wear the stack in the slot: it becomes the worn wearable. */
        LINK,
        /** The slot no longer gives the worn wearable (compat turned off); the stack stays in the slot, inert. */
        UNLINK,
        /** Something else is already worn: take the stack out of the slot and give it back to the player. */
        EJECT
    }

    /** Baubles asks before a backpack goes into a slot (client and server). */
    public static boolean canEquip(boolean compatEnabled, boolean isPlayer, boolean wearsSomething)
    {
        return compatEnabled && isPlayer && !wearsSomething;
    }

    /**
     * Server, each tick of a backpack in a Baubles slot. Only compares fields (Tier 3).
     *
     * @param linkedToThis   the worn wearable is this very stack
     * @param wearsSomething a wearable is worn (from the backpack slot or from another Baubles slot)
     * @param otherLinkStale the worn wearable came from a Baubles slot that no longer holds it
     */
    public static Action tick(boolean compatEnabled, boolean linkedToThis, boolean wearsSomething, boolean otherLinkStale)
    {
        if (linkedToThis) return compatEnabled ? Action.NONE : Action.UNLINK;
        if (!compatEnabled) return Action.NONE;
        if (!wearsSomething || otherLinkStale) return Action.LINK;
        return Action.EJECT;
    }
}
