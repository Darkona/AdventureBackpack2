package com.darkona.adventurebackpack.util;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.reference.ModInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created on 10/10/2014
 *
 * @author Darkona
 */
public class Resources
{

    public static final String TEXTURE_LOCATION = ModInfo.MOD_ID.toLowerCase();

    public static String modelTextureResourceString(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, "textures/models/" + name).toString();
    }

    public static String backpackTexturesStringFromColor(ItemStack backpack)
    {
        return backpackTextureFromColor(backpack).toString();
    }

    public static ResourceLocation backpackTextureFromColor(ItemStack backpack)
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

    public static ResourceLocation itemTextures(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, "textures/items/" + name + ".png");
    }

    public static ResourceLocation blockTextures(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, "textures/blocks/" + name + ".png");
    }

    public static ResourceLocation fluidTextures(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, "textures/blocks/fluid." + name + ".png");
    }

    public static ResourceLocation modelTextures(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, "models/" + name + ".png");
    }

    public static String getIconString(String name)
    {
        return TEXTURE_LOCATION + ":" + name;
    }


}
