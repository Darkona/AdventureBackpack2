package com.darkona.adventurebackpack.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.CreativeTabAB;
import com.darkona.adventurebackpack.init.ModMaterials;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.Resources;

/**
 * Created on 11/10/2014.
 *
 * @author Javier Darkona
 */
public class ArmorAB extends ItemArmor
{

    /**
     * @param type        2 Chain
     * @param renderIndex 0 Helmet, 1 Plate, 2 Pants, 3 Boots
     */
    public ArmorAB(int renderIndex, int type)
    {
        super(ModMaterials.ruggedLeather, renderIndex, type);
        setCreativeTab(CreativeTabAB.ADVENTURE_BACKPACK_CREATIVE_TAB);
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName()
    {
        return String.format("item.%s%s", ModInfo.MOD_ID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        itemIcon = iconRegister.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        String uName = this.getUnlocalizedName();
        return Resources.modelTextureResourceString(uName.substring(uName.indexOf(":") + 1)) + "_texture.png";
        //return Textures.modelTextureName(getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

}
