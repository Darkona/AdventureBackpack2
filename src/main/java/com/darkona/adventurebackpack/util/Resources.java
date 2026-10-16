package com.darkona.adventurebackpack.util;

import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.reference.ModInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;

public class Resources
{
    public static final String TEXTURE_LOCATION = ModInfo.MOD_ID.toLowerCase();

    public static String modelTextureResourceString(String name)
    {
        String cached = MODEL_TEXTURE_STRINGS.get(name);
        if (cached == null)
        {
            cached = modelTextureStringOf(name);
            MODEL_TEXTURE_STRINGS.put(name, cached);
        }
        return cached;
    }

    static String modelTextureStringOf(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, "textures/models/" + name).toString();
    }

    private static final ConcurrentHashMap<String, String> MODEL_TEXTURE_STRINGS = new ConcurrentHashMap<String, String>();

    private static final ConcurrentHashMap<String, ResourceLocation> BACKPACK_TEXTURES = new ConcurrentHashMap<String, ResourceLocation>();
    private static final ConcurrentHashMap<String, String> BACKPACK_TEXTURE_STRINGS = new ConcurrentHashMap<String, String>();
    private static final ConcurrentHashMap<String, ResourceLocation> MODEL_TEXTURES = new ConcurrentHashMap<String, ResourceLocation>();

    public static String backpackTexturesStringFromColor(ItemStack backpack)
    {
        String color = BackpackNames.getBackpackColorName(backpack);
        String cached = BACKPACK_TEXTURE_STRINGS.get(color);
        if (cached == null)
        {
            cached = backpackTextureFromString(color).toString();
            BACKPACK_TEXTURE_STRINGS.put(color, cached);
        }
        return cached;
    }

    public static ResourceLocation backpackTextureFromString(String color)
    {
        ResourceLocation cached = BACKPACK_TEXTURES.get(color);
        if (cached == null)
        {
            cached = new ResourceLocation(TEXTURE_LOCATION, "textures/backpack/" + color + ".png");
            BACKPACK_TEXTURES.put(color, cached);
        }
        return cached;
    }

    private static String cachedHoliday;
    private static ResourceLocation cachedHolidayTexture;

    public static synchronized ResourceLocation holidayTexture(String holiday)
    {
        if (cachedHolidayTexture == null || holiday == null || !holiday.equals(cachedHoliday))
        {
            String name = HolidayTexture.resolve(holiday, h -> {
                try
                {
                    net.minecraft.client.Minecraft.getMinecraft().getResourceManager().getResource(backpackTextureFromString(h));
                    return true;
                } catch (Exception e)
                {
                    return false;
                }
            });
            cachedHoliday = holiday;
            cachedHolidayTexture = backpackTextureFromString(name);
        }
        return cachedHolidayTexture;
    }

    public static ResourceLocation backpackTextureFromColor(IInventoryAdventureBackpack adventureBackpack)
    {
        return backpackTextureFromString(adventureBackpack.getColorName());
    }

    public static ResourceLocation guiTextures(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, "textures/gui/" + name + ".png");
    }

    public static ResourceLocation itemTextures(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, name);
    }

    public static ResourceLocation blockTextures(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, name);
    }

    public static ResourceLocation fluidTextures(String name)
    {
        return new ResourceLocation(TEXTURE_LOCATION, "textures/blocks/fluid." + name + ".png");
    }

    public static ResourceLocation modelTextures(String name)
    {
        ResourceLocation cached = MODEL_TEXTURES.get(name);
        if (cached == null)
        {
            cached = new ResourceLocation(TEXTURE_LOCATION, "textures/models/" + name + ".png");
            MODEL_TEXTURES.put(name, cached);
        }
        return cached;
    }

    public static String getIconString(String name)
    {
        return TEXTURE_LOCATION + ":" + name;
    }
}
