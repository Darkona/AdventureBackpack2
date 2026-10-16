package com.darkona.adventurebackpack.inventory;

public final class HeldContainerGuard
{
    public static final int HOTBAR_SWAP = 2;

    private HeldContainerGuard() {}

    public static boolean refuses(boolean holding, boolean clickedIsHeld, int mode, int button, int currentItem)
    {
        if (!holding) return false;
        return clickedIsHeld || (mode == HOTBAR_SWAP && button == currentItem);
    }

    public static boolean stillOpen(boolean wearing, Object opened, Object worn, Object held)
    {
        if (opened == null) return false;
        return opened == (wearing ? worn : held);
    }
}
