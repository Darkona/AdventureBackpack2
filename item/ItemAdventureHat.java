package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.client.models.ModelAdventureHat;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * Created by Darkona on 11/10/2014.
 */
public class ItemAdventureHat extends ArmorAB
{

    public ItemAdventureHat()
    {
        super(2, 0);
        setUnlocalizedName("adventureHat");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        return new ModelAdventureHat();
    }


    @SideOnly(Side.CLIENT)
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        String modelTexture;
        modelTexture = Resources.modelTextures("adventureHat").toString();

        return modelTexture;
    }
}

