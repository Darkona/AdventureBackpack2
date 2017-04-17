package com.darkona.adventurebackpack.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.darkona.adventurebackpack.inventory.IInventoryTanks;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.inventory.InventoryCoalJetpack;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.item.IBackWearableItem;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureHat;
import com.darkona.adventurebackpack.item.ItemCoalJetpack;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.item.ItemPistonBoots;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.reference.BackpackNames;

/**
 * Created on 11/10/2014
 *
 * @author Darkona
 */
public class Wearing
{
    public static boolean isWearingWearable(EntityPlayer player)
    {
        return BackpackProperty.get(player).getWearable() != null && BackpackProperty.get(player).getWearable().getItem() instanceof IBackWearableItem;
    }

    public static ItemStack getWearingWearable(EntityPlayer player)
    {
        return isWearingWearable(player) ? BackpackProperty.get(player).getWearable() : null;
    }

    public static boolean isHoldingWearable(EntityPlayer player)
    {
        return player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof IBackWearableItem;
    }

    public static boolean isWearingCopter(EntityPlayer player)
    {
        return BackpackProperty.get(player).getWearable() != null && BackpackProperty.get(player).getWearable().getItem() instanceof ItemCopterPack;
    }

    public static ItemStack getWearingCopter(EntityPlayer player)
    {
        return isWearingCopter(player) ? BackpackProperty.get(player).getWearable() : null;
    }

    public static boolean isHoldingJetpack(EntityPlayer player)
    {
        return player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemCoalJetpack;
    }

    public static ItemStack getHoldingJetpack(EntityPlayer player)
    {
        return isHoldingJetpack(player) ? player.inventory.getCurrentItem() : null;
    }

    public static boolean isWearingJetpack(EntityPlayer player)
    {
        return BackpackProperty.get(player).getWearable() != null && BackpackProperty.get(player).getWearable().getItem() instanceof ItemCoalJetpack;
    }

    public static ItemStack getWearingJetpack(EntityPlayer player)
    {
        return isWearingJetpack(player) ? BackpackProperty.get(player).getWearable() : null;
    }

    public static boolean isHoldingCopter(EntityPlayer player)
    {
        return player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemCopterPack;
    }

    public static ItemStack getHoldingCopter(EntityPlayer player)
    {
        return isHoldingCopter(player) ? player.inventory.getCurrentItem() : null;
    }

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
        return BackpackProperty.get(player).getWearable() != null && BackpackProperty.get(player).getWearable().getItem() instanceof ItemAdventureBackpack;
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
        return isWearingBackpack(player) ? BackpackProperty.get(player).getWearable() : null;
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
     * Will return a backpack inventory from a backpack in the slot if true, or in the hand if false.
     *
     * @param player  the player
     * @param wearing boolean flag
     * @return
     */
    public static InventoryBackpack getBackpackInv(EntityPlayer player, boolean wearing)
    {
        return new InventoryBackpack((wearing) ? BackpackProperty.get(player).getWearable() : player.getCurrentEquippedItem());
    }

    public static boolean isWearingTheRightBackpack(EntityPlayer player, String... backpacks)
    {
        if (Wearing.isWearingBackpack(player))
        {
            for (String name : backpacks)
            {
                if (BackpackNames.getBackpackColorName(Wearing.getWearingBackpack(player)).equals(name))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static IInventoryTanks getWearableInv(EntityPlayer player)
    {
        ItemStack wearable = Wearing.getWearingWearable(player);
        if (wearable.getItem() instanceof ItemAdventureBackpack) return new InventoryBackpack(wearable);
        if (wearable.getItem() instanceof ItemCoalJetpack) return new InventoryCoalJetpack(wearable);
        if (wearable.getItem() instanceof ItemCopterPack) return new InventoryCopterPack(wearable);
        return null;
    }
}
