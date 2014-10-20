package com.darkona.adventurebackpack.reference;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 15/10/2014
 *
 * @author Darkona
 */
public class BackpackNames
{

    public static String[] backpackNames = {
            "Cow", "Black", "Blaze", "Coal", "Diamond", "Emerald", "Gold", "Iron", "Lapis",
            "Redstone", "Blue", "Bookshelf", "Brown", "Cactus", "Cake", "Chest",
            "Cookie", "Cyan", "Dragon", "Egg", "Electric", "Deluxe", "Enderman", "End", "Chicken",
            "Ocelot", "Ghast", "Gray", "Green", "Haybale", "Leather", "LightBlue", "Glowstone",
            "LightGray", "Lime", "Magenta", "MagmaCube", "Melon", "BrownMushroom", "RedMushroom",
            "Mooshroom", "Nether", "Wither", "Obsidian", "Orange", "Pigman", "Pink", "Pig", "Pumpkin",
            "Purple", "Quartz", "Rainbow", "Red", "Sandstone", "Sheep", "Silver", "Creeper", "Skeleton",
            "WitherSkeleton", "Zombie", "Slime", "Snow", "Spider", "Sponge", "Standard", "White", "Wolf", "Yellow"
    };


    public static String getBackpackColorName(ItemStack item)
    {
        if (item == null) return "";
        if (item.stackTagCompound == null)
        {
            item.stackTagCompound = new NBTTagCompound();
        }
        if (!item.stackTagCompound.getString("color").isEmpty())
        {
            item.stackTagCompound.setString("colorName", "Standard");
        }
        return item.stackTagCompound.getString("colorName");
    }


}
