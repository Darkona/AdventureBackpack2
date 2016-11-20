package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.client.models.ModelAdventureHat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by Darkona on 11/10/2014.
 */
public class ItemAdventureHat extends ArmorAB
{

    public ItemAdventureHat()
    {
        super(2, 0);
        setMaxDamage(Items.leather_helmet.getMaxDamage() + 45);
        setUnlocalizedName("adventureHat");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        return new ModelAdventureHat();
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
	return par2ItemStack.isItemEqual(new ItemStack(Items.leather));
    }

}

