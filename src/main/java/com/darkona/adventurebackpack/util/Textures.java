package com.darkona.adventurebackpack.util;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.reference.ModInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Darkona on 10/10/2014.
 */
public class Textures
{

    public static final String TEXTURE_LOCATION = ModInfo.MOD_ID.toLowerCase();

    public static String modelTextureResourceString(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, "textures/models/" + name).toString();
    }

    public static String getBackpackTextureStringFromType(ItemStack backpack)
    {
        return getBackpackTextureResLocFromType(backpack).toString();
    }

    public static ResourceLocation getBackpackTextureResLocFromType(ItemStack backpack)
    {
        return new ResourceLocation(TEXTURE_LOCATION, "textures/backpack/backpack" + backpack.getTagCompound().getString("color") + ".png");
    }

    public static ResourceLocation resourceRL(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, name);
    }

    public static ResourceLocation backpackTexRL(TileAdventureBackpack te)
    {
        return new ResourceLocation(TEXTURE_LOCATION, "textures/backpack/backpack" + te.getColor() + ".png");
    }

    public static ResourceLocation guiTextures(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, "textures/gui/" + name + ".png");
    }

    public static String iconName(String name)
    {
        return ModInfo.MOD_ID.toLowerCase() + ":" + name;
    }


}
