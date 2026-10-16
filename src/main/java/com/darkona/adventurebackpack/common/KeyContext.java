package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.common.KeyActions.Held;
import com.darkona.adventurebackpack.common.KeyActions.Worn;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class KeyContext
{
    private KeyContext() {}

    public static Worn worn(EntityPlayer player)
    {
        if (Wearing.isWearingBackpack(player)) return Worn.BACKPACK;
        if (Wearing.isWearingCopter(player)) return Worn.COPTER;
        if (Wearing.isWearingSteam(player)) return Worn.JETPACK;
        if (Wearing.isWearingScuba(player)) return Worn.SCUBA;
        if (Wearing.isWearingFlamethrowerPack(player)) return Worn.FLAMETHROWER;
        return Worn.NONE;
    }

    public static Held held(EntityPlayer player)
    {
        ItemStack held = player.inventory.getCurrentItem();
        if (held == null) return Held.OTHER;
        if (held.getItem() == ModItems.cwxbow) return Held.CROSSBOW;
        if (held.getItem() instanceof ItemHose) return Held.HOSE;
        ItemStack backpack = Wearing.getWearingBackpack(player);
        if (backpack != null && backpack.getItem() instanceof ItemAdventureBackpack && ServerActions.canCycleTool(backpack, held))
        {
            return Held.CYCLABLE_TOOL;
        }
        return Held.OTHER;
    }

    public static boolean ridingMotorBoat(EntityPlayer player)
    {
        return player.ridingEntity instanceof EntityInflatableBoat
                && ((EntityInflatableBoat) player.ridingEntity).isMotorized()
                && ConfigHandler.BOAT_MOTOR_MODE == BoatMotor.Mode.TOGGLE;
    }

    public static KeyActions.Action resolve(KeyActions.Key key, EntityPlayer player, boolean toolCycleAllowed)
    {
        return KeyActions.resolve(key, worn(player), held(player), ridingMotorBoat(player), toolCycleAllowed);
    }
}
