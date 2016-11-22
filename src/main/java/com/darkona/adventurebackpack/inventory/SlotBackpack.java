package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Darkona on 12/10/2014.
 */
public class SlotBackpack extends SlotAdventureBackpack
{

    public static String[] forbiddenItemClasses =
    {
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
        "mrtjp.projectred.exploration.ItemBackpack"
    };

    /*private	static String[] forbiddenItemNames =
    {
        // "backpack", "blahblahblah"
    };*/

    public SlotBackpack(IInventoryAdventureBackpack inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    public static boolean valid(ItemStack stack)
    {
    	if (stack == null) {return false;}

    	for (String itemClass : forbiddenItemClasses)
    	{
    	    if (stack.getItem().getClass().getName().equals(itemClass)) return false;
    	}

    	if (ConfigHandler.YOU_SHALL_NOT_PASS)
    	{
    	    for (String itemName : ConfigHandler.NAME_LOCALIZED)
    	    {
    		if (stack.getDisplayName().toLowerCase().contains(itemName.toLowerCase())) return false;
    	    }
    	    for (String itemName : ConfigHandler.NAME_UNLOCALIZED)
    	    {
    		if (stack.getItem().getUnlocalizedName().toLowerCase().contains(itemName.toLowerCase())) return false;
    	    }
    	}
    	return true;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
    	return valid(stack);
    }

    @Override
    public void onPickupFromSlot(EntityPlayer p_82870_1_, ItemStack p_82870_2_)
    {
        super.onPickupFromSlot(p_82870_1_, p_82870_2_);
    }

}
