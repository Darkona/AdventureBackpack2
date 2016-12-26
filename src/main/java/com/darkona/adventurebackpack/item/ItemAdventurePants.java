package com.darkona.adventurebackpack.item;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created on 11/10/2014
 *
 * @author Darkona
 */
public class ItemAdventurePants extends ArmorAB
{

    public ItemAdventurePants()
    {
        super(2, 2);
        setMaxDamage(Items.leather_leggings.getMaxDamage() + 75);
        setUnlocalizedName("adventurePants");
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        return par2ItemStack.isItemEqual(new ItemStack(Items.leather));
    }
}
