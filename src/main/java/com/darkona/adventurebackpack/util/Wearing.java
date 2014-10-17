package com.darkona.adventurebackpack.util;

import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureHat;
import com.darkona.adventurebackpack.item.ItemPistonBoots;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Darkona on 11/10/2014.
 */
public class Wearing
{

    public static boolean isWearingBoots(EntityPlayer player)
    {
        return player.inventory.armorInventory[0] != null && player.inventory.armorInventory[0].getItem() instanceof ItemPistonBoots;
    }

    public static boolean isWearingHat(EntityPlayer player)
    {
        return player.inventory.armorInventory[3] != null && player.inventory.armorInventory[3].getItem() instanceof ItemAdventureHat;
    }

    public static boolean isWearingBackpack(EntityPlayer player)
    {
        return player.inventory.armorInventory[2] != null && player.inventory.armorInventory[2].getItem() instanceof ItemAdventureBackpack;
    }

    public static boolean isHoldingBackpack(EntityPlayer player)
    {
        return player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemAdventureBackpack;
    }

    public static ItemStack getWearingHat(EntityPlayer player)
    {
        if (isWearingHat(player)) return player.inventory.armorInventory[3];
        return null;
    }

    public static ItemStack getWearingBackpack(EntityPlayer player)
    {
        if (isWearingBackpack(player))
        {
            return player.inventory.armorInventory[2];
        }
        return null;
    }

    public static ItemStack getHoldingBackpack(EntityPlayer player)
    {
        if (isHoldingBackpack(player))
        {
            return player.inventory.getCurrentItem();
        }
        return null;
    }

    public static ItemStack getWearingBoots(EntityPlayer player)
    {
        return isWearingBoots(player) ? player.inventory.armorInventory[0] : null;
    }

    /**
     * Will return a backpack inventory from a backpack on the player's armor
     * slot if true, or from his hand if false;
     *
     * @param player  the player com.darkona.adventurebackpack.entity
     * @param wearing boolean flag
     * @return
     */
    public static InventoryItem getBackpackInv(EntityPlayer player, boolean wearing)
    {
        return new InventoryItem((wearing) ? player.inventory.armorItemInSlot(2) : player.getCurrentEquippedItem());
    }
}
