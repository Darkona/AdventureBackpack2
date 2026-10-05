package com.darkona.adventurebackpack.init.recipes;

import com.darkona.adventurebackpack.common.MagazineLogic;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.item.ItemBoltCartridge;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Shapeless reload of the Crossbow Magazine (crossbow.magazine.reloadRecipe): one magazine and 1 or more items of one
 * ammunition of crossbow.ammo.list, the kind the magazine already holds when it is not empty, no more than its free
 * room. The result is the magazine with those charges added; each grid slot gives one item, as in any recipe.
 */
public class CrossbowReloadRecipe implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting grid, World world)
    {
        return result(grid) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid)
    {
        return result(grid);
    }

    private static ItemStack result(InventoryCrafting grid)
    {
        ItemStack magazine = null;
        String ammo = null;
        int count = 0;
        for (int i = 0; i < grid.getSizeInventory(); i++)
        {
            ItemStack s = grid.getStackInSlot(i);
            if (s == null) continue;
            if (s.getItem() == ModItems.boltCartridge)
            {
                if (magazine != null) return null;
                magazine = s;
                continue;
            }
            String name = ItemBoltCartridge.ammoName(s);
            if (name == null || (ammo != null && !ammo.equals(name))) return null;
            ammo = name;
            count++;
        }
        if (magazine == null) return null;
        int capacity = ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE;
        int charges = ItemBoltCartridge.getCharges(magazine, capacity);
        if (!MagazineLogic.accepts(charges, ItemBoltCartridge.getAmmo(magazine), ammo)) return null;
        int loaded = MagazineLogic.recipeCharges(charges, capacity, count);
        return loaded < 0 ? null : ItemBoltCartridge.create(loaded, ammo);
    }

    @Override
    public int getRecipeSize()
    {
        return 2;
    }

    /** What recipe viewers show: a magazine loaded with arrows. */
    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemBoltCartridge.create(MagazineLogic.LEGACY_CHARGES, MagazineLogic.LEGACY_AMMO);
    }
}
