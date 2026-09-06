package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Each player can switch the abilities of the worn backpack off and on with a key (#57). The flag lives in
 * {@link BackpackProperty} (saved with the player and synced to the clients), the server flips it on a request and
 * the global enabled.abilities switch always wins: when it is off the abilities stay off and the key does nothing.
 */
public final class AbilityToggle
{
    /** What the server answers to a toggle request. */
    public static final int GLOBALLY_DISABLED = 0;
    public static final int TURNED_ON = 1;
    public static final int TURNED_OFF = 2;

    private AbilityToggle()
    {
    }

    /** True when the worn backpack's abilities work: the config allows them and the player did not switch them off. */
    public static boolean active(boolean globalEnabled, boolean playerOff)
    {
        return globalEnabled && !playerOff;
    }

    /** The result of a toggle request; the new flag is {@code result == TURNED_OFF}. */
    public static int toggle(boolean globalEnabled, boolean playerOff)
    {
        if (!globalEnabled) return GLOBALLY_DISABLED;
        return playerOff ? TURNED_ON : TURNED_OFF;
    }

    /** {@link #active} for a player; a player without properties (not constructed yet) counts as switched on. */
    public static boolean isActive(EntityPlayer player)
    {
        if (!ConfigHandler.BACKPACK_ABILITIES) return false;
        BackpackProperty props = player == null ? null : BackpackProperty.get(player);
        return active(true, props != null && props.isAbilitiesOff());
    }
}
