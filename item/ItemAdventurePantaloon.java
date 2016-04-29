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

/**
 * Created on 11/10/2014
 *
 * @author Darkona
 */
public class ItemAdventurePantaloon extends ArmorAB
{

    public ItemAdventurePantaloon()
    {
        super(2, 2);
        setUnlocalizedName("pants");
    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
    {
        return armorType == 2;
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
        return Resources.modelTextureResourceString("adventurepants_texture.png");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack itemStack, int armorSlot)
    {
        return new ModelFullArmor();
    }

}
