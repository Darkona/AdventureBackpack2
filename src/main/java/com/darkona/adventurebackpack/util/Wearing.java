package com.darkona.adventurebackpack.util;

import com.darkona.adventurebackpack.items.ItemPistonBoots;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Darkona on 11/10/2014.
 */
public class Wearing {

    public static boolean isWearingBoots(EntityPlayer player){
        return player.inventory.armorInventory[0] != null && player.inventory.armorInventory[0].getItem() instanceof ItemPistonBoots;
    }
}
