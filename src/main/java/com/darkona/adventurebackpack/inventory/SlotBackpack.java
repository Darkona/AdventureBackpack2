package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Darkona on 12/10/2014.
 */
public class SlotBackpack extends SlotAdventureBackpack
{
	
	private static String[] forbiddenItemClasses =
    {
        // Adventure Backpack 2
        //"com.darkona.adventurebackpack.item.ItemAdventureBackpack",
        // Jabba Dolly
        "mcp.mobius.betterbarrels.common.items.dolly.ItemBarrelMover",   
        "mcp.mobius.betterbarrels.common.items.dolly.ItemDiamondMover",
        // Forestry Backpacks, includes Railcraft and MagicBees addons
        "forestry.storage.items.ItemBackpack",
        "forestry.storage.items.ItemBackpackNaturalist",
        // Backpack Mod
        "de.eydamos.backpack.item.ItemBackpack",
        "de.eydamos.backpack.item.ItemWorkbenchBackpack"
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
        if ((stack != null) && (stack.getItem() instanceof ItemAdventureBackpack || stack.getItem() == Item.getItemFromBlock(ModBlocks.blockBackpack))) return false; 
        
        if (ConfigHandler.DONT_GO_DEEPER) 
        {
        	Item item = stack.getItem();
        
        	for (String itemClass : forbiddenItemClasses)
        	{
        		if (item.getClass().getName() == (itemClass)) return false;	
        	}

        	/*String name = item.getUnlocalizedName().toLowerCase();
            
        	for (String itemName : forbiddenItemNames)
        	{
        		@SuppressWarnings("unused")
        		String a = itemName;
        		if (name.contains(itemName)) return false;
        	}*/
        }
        return true;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack)
    {
    	return ((stack != null) && (valid(stack)));    	
    }

    @Override
    public void onPickupFromSlot(EntityPlayer p_82870_1_, ItemStack p_82870_2_)
    {
        super.onPickupFromSlot(p_82870_1_, p_82870_2_);
    }

}
