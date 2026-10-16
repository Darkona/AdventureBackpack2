package com.darkona.adventurebackpack.compat.baubles;

public final class BaubleRules
{
    private BaubleRules()
    {
    }

    public enum Action
    {
        NONE,
        LINK,
        UNLINK,
        EJECT
    }

    public static boolean canEquip(boolean compatEnabled, boolean isPlayer, boolean wearsSomething)
    {
        return compatEnabled && isPlayer && !wearsSomething;
    }

    public static Action tick(boolean compatEnabled, boolean linkedToThis, boolean wearsSomething, boolean otherLinkStale)
    {
        if (linkedToThis) return compatEnabled ? Action.NONE : Action.UNLINK;
        if (!compatEnabled) return Action.NONE;
        if (!wearsSomething || otherLinkStale) return Action.LINK;
        return Action.EJECT;
    }
}
