package com.darkona.adventurebackpack.reference;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
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
            "Standard",
            "Cow",
            "Bat",
            "Black",
            "Blaze",
            "Carrot",
            "Coal",
            "Diamond",
            "Emerald",
            "Gold",
            "Iron",
            "IronGolem",
            "Lapis",
            "Redstone",
            "Blue",
            "Bookshelf",
            "Brown",
            "Cactus",
            "Cake",
            "Chest",
            "Cookie",
            "Cyan",
            "Dragon",
            "Egg",
            "Electric",
            "Deluxe",
            "Enderman",
            "End",
            "Chicken",
            "Ocelot",
            "Ghast",
            "Gray",
            "Green",
            "Haybale",
            "Horse",
            "Leather",
            "LightBlue",
            "Glowstone",
            "LightGray",
            "Lime",
            "Magenta",
            "MagmaCube",
            "Melon",
            "BrownMushroom",
            "RedMushroom",
            "Mooshroom",
            "Nether",
            "Wither",
            "Obsidian",
            "Orange",
            "Overworld",
            "Pigman",
            "Pink",
            "Pig",
            "Pumpkin",
            "Purple",
            "Quartz",
            "Rainbow",
            "Red",
            "Sandstone",
            "Sheep",
            "Silverfish",
            "Squid",
            "Sunflower",
            "Creeper",
            "Skeleton",
            "WitherSkeleton",
            "Slime",
            "Snow",
            "Spider",
            "Sponge",
            "Villager",
            "White",
            "Wolf",
            "Yellow",
            "Zombie"
    };

    public static ItemStack setBackpackColorNameFromDamage(ItemStack backpack, int damage)
    {

        if (backpack == null) return null;
        if (!(backpack.getItem() instanceof ItemAdventureBackpack)) return null;
        if(backpack.stackTagCompound == null)
        {
            backpack.stackTagCompound = new NBTTagCompound();
        }
        backpack.setItemDamage(damage);
        backpack.stackTagCompound.setString("colorName", backpackNames[damage]);
        return backpack;
    }

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
