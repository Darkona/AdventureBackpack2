package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.client.models.ModelFullArmor;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemAdventureJacket extends ArmorAB
{
    public ItemAdventureJacket()
    {
        super(2, 1);
        setUnlocalizedName("adventureJacket");
    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
    {
        return armorType == 1;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
        return itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        return Resources.modelTextureResourceString("adventureSuit_texture.png");
    }

    @SideOnly(Side.CLIENT)
    private static ModelBiped armorModel;

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack itemStack, int armorSlot)
    {
        if (armorModel == null) armorModel = new ModelFullArmor(1);
        return armorModel;
    }
}
