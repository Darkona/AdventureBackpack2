package com.darkona.adventurebackpack.common;

public final class KeyActions
{
    public enum Key
    {
        GEAR,
        ABILITY,
        MODE,
        STATE;

        private static final Key[] VALUES = values();

        public static Key fromId(int id)
        {
            return id >= 0 && id < VALUES.length ? VALUES[id] : null;
        }
    }

    public enum Worn
    {
        NONE, BACKPACK, COPTER, JETPACK, SCUBA, FLAMETHROWER
    }

    public enum Held
    {
        OTHER, HOSE, CROSSBOW,
        CYCLABLE_TOOL
    }

    public enum Action
    {
        NONE,
        OPEN_BACKPACK, OPEN_COPTER, OPEN_JETPACK, OPEN_SCUBA, OPEN_FLAMETHROWER,
        TOGGLE_ABILITIES, TOGGLE_COPTER, TOGGLE_JETPACK, TOGGLE_PILOT,
        BOAT_MOTOR, COPTER_MODE, HOSE_TANK,
        CROSSBOW_MODE, HOSE_MODE, CYCLE_TOOL
    }

    private KeyActions() {}

    public static Action resolve(Key key, Worn worn, Held held, boolean ridingMotorBoat, boolean toolCycleAllowed)
    {
        if (key == null) return Action.NONE;
        switch (key)
        {
            case GEAR:
                return open(worn);
            case ABILITY:
                return toggle(worn);
            case MODE:
                if (ridingMotorBoat) return Action.BOAT_MOTOR;
                if (worn == Worn.COPTER) return Action.COPTER_MODE;
                if (held == Held.HOSE) return Action.HOSE_TANK;
                return Action.NONE;
            case STATE:
                if (held == Held.CROSSBOW) return Action.CROSSBOW_MODE;
                if (!toolCycleAllowed) return Action.NONE;
                if (held == Held.HOSE) return Action.HOSE_MODE;
                if (held == Held.CYCLABLE_TOOL && worn == Worn.BACKPACK) return Action.CYCLE_TOOL;
                return Action.NONE;
            default:
                return Action.NONE;
        }
    }

    private static Action open(Worn worn)
    {
        switch (worn)
        {
            case BACKPACK: return Action.OPEN_BACKPACK;
            case COPTER: return Action.OPEN_COPTER;
            case JETPACK: return Action.OPEN_JETPACK;
            case SCUBA: return Action.OPEN_SCUBA;
            case FLAMETHROWER: return Action.OPEN_FLAMETHROWER;
            default: return Action.NONE;
        }
    }

    private static Action toggle(Worn worn)
    {
        switch (worn)
        {
            case BACKPACK: return Action.TOGGLE_ABILITIES;
            case COPTER: return Action.TOGGLE_COPTER;
            case JETPACK: return Action.TOGGLE_JETPACK;
            case FLAMETHROWER: return Action.TOGGLE_PILOT;
            default: return Action.NONE;
        }
    }

    public static int direction(boolean sneaking)
    {
        return sneaking ? -1 : 1;
    }

    public static int clampDirection(int direction)
    {
        return direction < 0 ? -1 : 1;
    }
}
