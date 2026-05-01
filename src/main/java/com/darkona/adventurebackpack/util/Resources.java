package com.darkona.adventurebackpack.util;

import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.reference.ModInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;

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

    // Texture locations are asked for on every rendered frame (performance contract, tier 4): build each one once.
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

    /**
     * Texture of the special-day backpack, falling back to Standard when the file does not exist (#106).
     * The resource lookup runs once per holiday name, never per frame. Client side only.
     */
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
