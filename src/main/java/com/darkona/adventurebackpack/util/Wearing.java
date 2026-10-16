package com.darkona.adventurebackpack.util;

import adventurebackpack.api.IBackWearableItem;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.item.*;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.reference.BackpackNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Wearing
{
    static ItemStack wornOfType(ItemStack worn, Class<?> type)
    {
        return worn != null && type.isInstance(worn.getItem()) ? worn : null;
    }

    public static boolean isWearingWearable(EntityPlayer player){
        return wornOfType(BackpackProperty.get(player).getWearable(), IBackWearableItem.class) != null;
    }

    public static ItemStack getWearingWearable(EntityPlayer player)
    {
       return wornOfType(BackpackProperty.get(player).getWearable(), IBackWearableItem.class);
    }

    public static boolean isHoldingWearable(EntityPlayer player){
        return player.inventory.getCurrentItem()!= null && player.inventory.getCurrentItem().getItem() instanceof IBackWearableItem;
    }

    public static boolean isWearingCopter(EntityPlayer player)
    {
        return wornOfType(BackpackProperty.get(player).getWearable(), ItemCopterPack.class) != null;
    }

    public static ItemStack getWearingCopter(EntityPlayer player)
    {
         return wornOfType(BackpackProperty.get(player).getWearable(), ItemCopterPack.class);
    }

    public static boolean isHoldingSteam(EntityPlayer player)
    {
        return player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemSteamJetpack;
    }

    public static ItemStack getHoldingSteam(EntityPlayer player)
    {
        return isHoldingSteam(player) ?  player.inventory.getCurrentItem() : null;
    }

    public static boolean isWearingSteam(EntityPlayer player)
    {
        return wornOfType(BackpackProperty.get(player).getWearable(), ItemSteamJetpack.class) != null;
    }

    public static ItemStack getWearingSteam(EntityPlayer player)
    {
        return wornOfType(BackpackProperty.get(player).getWearable(), ItemSteamJetpack.class);
    }

    public static boolean isHoldingCopter(EntityPlayer player)
    {
        return player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemCopterPack;
    }

    public static ItemStack getHoldingCopter(EntityPlayer player)
    {
        return isHoldingCopter(player) ?  player.inventory.getCurrentItem() : null;
    }

    public static boolean isWearingScuba(EntityPlayer player)
    {
        return wornOfType(BackpackProperty.get(player).getWearable(), ItemScubaGear.class) != null;
    }

    public static ItemStack getWearingScuba(EntityPlayer player)
    {
        return wornOfType(BackpackProperty.get(player).getWearable(), ItemScubaGear.class);
    }

    public static boolean isHoldingScuba(EntityPlayer player)
    {
        return player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemScubaGear;
    }

    public static ItemStack getHoldingScuba(EntityPlayer player)
    {
        return isHoldingScuba(player) ? player.inventory.getCurrentItem() : null;
    }

    public static boolean isWearingFlamethrowerPack(EntityPlayer player)
    {
        return wornOfType(BackpackProperty.get(player).getWearable(), ItemFlamethrowerPack.class) != null;
    }

    public static ItemStack getWearingFlamethrowerPack(EntityPlayer player)
    {
        return wornOfType(BackpackProperty.get(player).getWearable(), ItemFlamethrowerPack.class);
    }

    public static boolean isHoldingFlamethrowerPack(EntityPlayer player)
    {
        return player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFlamethrowerPack;
    }

    public static ItemStack getHoldingFlamethrowerPack(EntityPlayer player)
    {
        return isHoldingFlamethrowerPack(player) ? player.inventory.getCurrentItem() : null;
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
        return wornOfType(BackpackProperty.get(player).getWearable(), ItemAdventureBackpack.class) != null;
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
        return wornOfType(BackpackProperty.get(player).getWearable(), ItemAdventureBackpack.class);
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

    public static InventoryBackpack getBackpackInv(EntityPlayer player, boolean wearing)
    {
        return new InventoryBackpack((wearing) ? BackpackProperty.get(player).getWearable() : player.getCurrentEquippedItem());
    }

    public static boolean isWearingTheRightBackpack(EntityPlayer player, String... backpacks)
    {
        ItemStack backpack = getWearingBackpack(player);
        if (backpack != null)
        {
            String color = BackpackNames.getBackpackColorName(backpack);
            for(String name : backpacks)
            {
                if(color.equals(name))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
