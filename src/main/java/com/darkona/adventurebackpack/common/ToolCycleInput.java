package com.darkona.adventurebackpack.common;

/**
 * Turns the player's input into a tool-cycle direction on the client: +1 is "next" (the lower tool into the hand,
 * next hose mode), -1 is "previous" and 0 means the input is not a tool cycle. Pure, so the unit test covers it; the
 * server still checks the slot and the item when the packet arrives ({@link ServerActions#cycleTool}).
 */
public final class ToolCycleInput
{
    public enum Mode
    {
        /** Hold the modifier key and turn the mouse wheel (the original behaviour). */
        WHEEL,
        /** Only the "next tool" and "previous tool" keys. */
        KEYS,
        /** Both of the above. */
        BOTH;

        public static final String[] NAMES = {"wheel", "keys", "both"};

        /** Reads a config value; anything unknown falls back to {@link #BOTH}. */
        public static Mode parse(String text)
        {
            if (text != null)
            {
                String t = text.trim();
                for (Mode m : values())
                {
                    if (m.name().equalsIgnoreCase(t)) return m;
                }
            }
            return BOTH;
        }

        public boolean wheel()
        {
            return this != KEYS;
        }

        public boolean keys()
        {
            return this != WHEEL;
        }
    }

    private ToolCycleInput() {}

    /** Direction for one mouse wheel event, or 0 when the wheel must keep scrolling the hotbar. */
    public static int wheelDirection(boolean enabled, Mode mode, boolean modifierDown, int dWheel, boolean invert)
    {
        if (!enabled || !mode.wheel() || !modifierDown || dWheel == 0) return 0;
        int dir = dWheel > 0 ? 1 : -1;
        return invert ? -dir : dir;
    }

    /** Direction for the "next" and "previous" keys pressed in one input event; both at once cancel out. */
    public static int keyDirection(boolean enabled, Mode mode, boolean nextPressed, boolean previousPressed)
    {
        if (!enabled || !mode.keys() || nextPressed == previousPressed) return 0;
        return nextPressed ? 1 : -1;
    }
}
