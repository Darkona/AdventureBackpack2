package com.darkona.adventurebackpack.inventory;

/**
 * Clicks a container must refuse while it shows the item held in the hand (backpack, copter pack or jetpack opened
 * with a right click): moving that stack would leave the open inventory writing into a stack that is somewhere else,
 * which duplicates or loses its contents. Pure, so the unit test covers it.
 */
public final class HeldContainerGuard
{
    /** Container.slotClick mode for the number keys: swap the clicked slot with hotbar slot {@code button}. */
    public static final int HOTBAR_SWAP = 2;

    private HeldContainerGuard() {}

    /**
     * @param holding        the container shows the held item (not a worn one)
     * @param clickedIsHeld  the clicked slot holds the very stack in the hand
     * @param mode           slotClick mode (third argument)
     * @param button         slotClick button (second argument); the hotbar slot for {@link #HOTBAR_SWAP}
     * @param currentItem    the selected hotbar slot
     */
    public static boolean refuses(boolean holding, boolean clickedIsHeld, int mode, int button, int currentItem)
    {
        if (!holding) return false;
        return clickedIsHeld || (mode == HOTBAR_SWAP && button == currentItem);
    }
}
