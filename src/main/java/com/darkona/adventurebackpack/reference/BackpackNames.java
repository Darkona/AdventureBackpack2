package com.darkona.adventurebackpack.reference;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.util.BackpackUtils;

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
            "Zombie",
            "ModdedNetwork"
    };

    public static ItemStack setBackpackColorNameFromDamage(ItemStack backpack, int damage)
    {
        if (backpack == null) return null;
        if (!(backpack.getItem() instanceof ItemAdventureBackpack)) return null;
        NBTTagCompound backpackTag = BackpackUtils.getBackpackTag(backpack) != null ? BackpackUtils.getBackpackTag(backpack) : new NBTTagCompound();
        backpack.setItemDamage(damage);
        assert backpackTag != null;
        backpackTag.setString("colorName", backpackNames[damage]);
        BackpackUtils.setBackpackTag(backpack, backpackTag);
        return backpack;
    }

    public static int getBackpackDamageFromName(String name)
    {
        for (int i = 0; i < backpackNames.length; i++)
        {
            if (backpackNames[i].equals(name)) return i;
        }
        return 0;
    }

    public static String getBackpackColorName(TileAdventureBackpack te)
    {
        return te.getColorName();
    }

    public static String getBackpackColorName(ItemStack backpack)
    {
        if (backpack == null) return "";
        NBTTagCompound backpackTag = BackpackUtils.getBackpackTag(backpack) != null ? BackpackUtils.getBackpackTag(backpack) : new NBTTagCompound();
        assert backpackTag != null;
        if (backpackTag.getString("colorName").isEmpty())
        {
            backpackTag.setString("colorName", "Standard");
        }
        return backpackTag.getString("colorName");
    }

    public static void setBackpackColorName(ItemStack backpack, String newName)
    {
        if (backpack != null)
        {
            NBTTagCompound backpackTag = BackpackUtils.getBackpackTag(backpack) != null ? BackpackUtils.getBackpackTag(backpack) : new NBTTagCompound();
            assert backpackTag != null;
            backpackTag.setString("colorName", newName);
            BackpackUtils.setBackpackTag(backpack, backpackTag);
        }
    }
}
