package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import net.minecraft.entity.player.EntityPlayer;

public final class AbilityToggle
{
    public static final int GLOBALLY_DISABLED = 0;
    public static final int TURNED_ON = 1;
    public static final int TURNED_OFF = 2;

    private AbilityToggle()
    {
    }

    public static boolean active(boolean globalEnabled, boolean playerOff)
    {
        return globalEnabled && !playerOff;
    }

    public static int toggle(boolean globalEnabled, boolean playerOff)
    {
        if (!globalEnabled) return GLOBALLY_DISABLED;
        return playerOff ? TURNED_ON : TURNED_OFF;
    }

    public static boolean isActive(EntityPlayer player)
    {
        if (!ConfigHandler.BACKPACK_ABILITIES) return false;
        BackpackProperty props = player == null ? null : BackpackProperty.get(player);
        return active(true, props != null && props.isAbilitiesOff());
    }
}
