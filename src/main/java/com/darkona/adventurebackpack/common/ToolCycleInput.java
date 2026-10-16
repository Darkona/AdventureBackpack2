package com.darkona.adventurebackpack.common;

public final class ToolCycleInput
{
    public enum Mode
    {
        WHEEL,
        KEYS,
        BOTH;

        public static final String[] NAMES = {"wheel", "keys", "both"};

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

    public static int wheelDirection(boolean enabled, Mode mode, boolean modifierDown, int dWheel, boolean invert)
    {
        if (!enabled || !mode.wheel() || !modifierDown || dWheel == 0) return 0;
        int dir = dWheel > 0 ? 1 : -1;
        return invert ? -dir : dir;
    }

    public static boolean keyAllowed(boolean enabled, Mode mode)
    {
        return enabled && mode.keys();
    }
}
