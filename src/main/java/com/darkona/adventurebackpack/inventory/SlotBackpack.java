package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.darkona.adventurebackpack.config.ConfigHandler;

/**
 * Created by Darkona on 12/10/2014.
 */
public class SlotBackpack extends SlotAdventureBackpack
{

    private static final String[] FORBIDDEN_CLASSES = {
            // Adventure Backpack 2
            "com.darkona.adventurebackpack.item.ItemAdventureBackpack",
            // Backpack Mod
            "de.eydamos.backpack.item.ItemBackpack",
            "de.eydamos.backpack.item.ItemWorkbenchBackpack",
            // Blue Power Canvas Bags
            "com.bluepowermod.item.ItemCanvasBag",
            // Extra Utilities Golden Bag of Holding
            "com.rwtema.extrautils.item.ItemGoldenBag",
            // Forestry Backpacks +addons
            "forestry.storage.items.ItemBackpack",
            "forestry.storage.items.ItemBackpackNaturalist",
            // Jabba Dolly
            "mcp.mobius.betterbarrels.common.items.dolly.ItemBarrelMover",
            "mcp.mobius.betterbarrels.common.items.dolly.ItemDiamondMover",
            // Project Red Exploration Backpacks
            "mrtjp.projectred.exploration.ItemBackpack",};

    public SlotBackpack(IInventoryAdventureBackpack inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    public static boolean isValidItem(ItemStack stack)
    {
        if (stack == null) return false;
        Item itemCurrent = stack.getItem();

        for (String classDisallowed : FORBIDDEN_CLASSES)
        {
            if (itemCurrent.getClass().getName().equals(classDisallowed)) return false;
        }

        if (ConfigHandler.enableItemFilters)
        {
            for (String itemDisallowed : ConfigHandler.nameLocalized)
            {
                if (stack.getDisplayName().equalsIgnoreCase(itemDisallowed)) return false;
            }
            for (String itemDisallowed : ConfigHandler.nameInternalID)
            {
                if (Item.itemRegistry.getNameForObject(itemCurrent).equals(itemDisallowed)) return false;
            }
            for (String itemDisallowed : ConfigHandler.nameInternalIDs)
            {
                if (Item.itemRegistry.getNameForObject(itemCurrent).contains(itemDisallowed)) return false;
            }
            for (String itemDisallowed : ConfigHandler.nameUnlocalized)
            {
                if (itemCurrent.getUnlocalizedName().equalsIgnoreCase(itemDisallowed)) return false;
            }
        }
        return true;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return isValidItem(stack);
    }

    @Override
    public void onPickupFromSlot(EntityPlayer p_82870_1_, ItemStack p_82870_2_)
    {
        super.onPickupFromSlot(p_82870_1_, p_82870_2_);
    }

}
