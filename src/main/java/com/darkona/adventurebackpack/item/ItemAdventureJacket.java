package com.darkona.adventurebackpack.item;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created on 11/10/2014
 *
 * @author Darkona
 */
public class ItemAdventureJacket extends ArmorAB
{

    public ItemAdventureJacket()
    {
        super(1, 1);
        setMaxDamage(Items.leather_chestplate.getMaxDamage() + 70);
        setUnlocalizedName("adventureSuit");
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
	return par2ItemStack.isItemEqual(new ItemStack(Items.leather));
    }
}
