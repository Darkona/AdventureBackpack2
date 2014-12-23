package com.darkona.adventurebackpack.reference;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created on 15/10/2014
 *
 * @author Darkona
 */
public class BackpackNames
{

    public static String[] backpackNames = {
            "Cow", "Bat", "Black", "Blaze", "Carrot", "Coal", "Diamond", "Emerald", "Gold", "Iron", "IronGolem","Lapis",
            "Redstone", "Blue", "Bookshelf", "Brown", "Cactus", "Cake", "Chest",
            "Cookie", "Cyan", "Dragon", "Egg", "Electric", "Deluxe", "Enderman", "End", "Chicken",
            "Ocelot", "Ghast", "Gray", "Green", "Haybale", "Horse","Leather", "LightBlue", "Glowstone",
            "LightGray", "Lime", "Magenta", "MagmaCube", "Melon", "BrownMushroom", "RedMushroom",
            "Mooshroom", "Nether", "Wither", "Obsidian", "Orange","Overworld", "Pigman", "Pink", "Pig", "Pumpkin",
            "Purple", "Quartz", "Rainbow", "Red", "Sandstone", "Sheep", "Silver", "Squid", "Sunflower", "Creeper", "Skeleton",
            "WitherSkeleton", "Zombie", "Slime", "Snow", "Spider", "Sponge", "Standard", "Villager", "White", "Wolf", "Yellow"
    };


    public static String getBackpackColorName(TileAdventureBackpack te){
        return te.getColorName();
    }
    public static String getBackpackColorName(ItemStack item)
    {
        if (item == null) return "";
        if (item.stackTagCompound == null)
        {
            item.stackTagCompound = new NBTTagCompound();
        }
        if (item.stackTagCompound.getString("colorName").isEmpty())
        {
            item.stackTagCompound.setString("colorName", "Standard");
        }
        return item.stackTagCompound.getString("colorName");
    }

    public static void setBackpackColorName(ItemStack item, String newName){
        if(item!=null){
            if(item.stackTagCompound == null){
                item.setTagCompound(new NBTTagCompound());
            }
            item.stackTagCompound.setString("colorName", newName);
        }
    }
}
