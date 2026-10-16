package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.CreativeTabAB;
import com.darkona.adventurebackpack.init.ModMaterials;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ArmorAB extends ItemArmor
{
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

    private String unlocalizedName;
    private String armorTexture;

    @Override
    public Item setUnlocalizedName(String name)
    {
        unlocalizedName = null;
        armorTexture = null;
        return super.setUnlocalizedName(name);
    }

    @Override
    public String getUnlocalizedName()
    {
        String name = unlocalizedName;
        if (name == null)
        {
            name = buildUnlocalizedName(super.getUnlocalizedName());
            unlocalizedName = name;
        }
        return name;
    }

    private String buildUnlocalizedName(String vanillaName)
    {
        return String.format("item.%s%s", ModInfo.MOD_ID.toLowerCase() + ":", getUnwrappedUnlocalizedName(vanillaName));
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
        String texture = armorTexture;
        if (texture == null)
        {
            texture = buildArmorTexture(this.getUnlocalizedName());
            armorTexture = texture;
        }
        return texture;
    }

    static String buildArmorTexture(String uName)
    {
        return Resources.modelTextureResourceString(uName.substring(uName.indexOf(":") + 1)) + "_texture.png";
    }
}
