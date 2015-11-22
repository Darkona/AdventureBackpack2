package com.darkona.adventurebackpack.reference;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.util.BackpackUtils;
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
        NBTTagCompound backpackData = BackpackUtils.getBackpackData(backpack) != null ? BackpackUtils.getBackpackData(backpack) : new NBTTagCompound() ;
        backpack.setItemDamage(damage);
        assert backpackData != null;
        backpackData.setString("colorName", backpackNames[damage]);
        BackpackUtils.setBackpackData(backpack,backpackData);
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
        NBTTagCompound backpackData = BackpackUtils.getBackpackData(backpack) != null ? BackpackUtils.getBackpackData(backpack) : new NBTTagCompound() ;
        assert backpackData != null;
        if (backpackData.getString("colorName").isEmpty())
        {
            backpackData.setString("colorName", "Standard");
        }
        return backpackData.getString("colorName");
    }

    public static void setBackpackColorName(ItemStack backpack, String newName)
    {
        if (backpack != null)
        {
            NBTTagCompound backpackData = BackpackUtils.getBackpackData(backpack) != null ? BackpackUtils.getBackpackData(backpack) : new NBTTagCompound() ;
            assert backpackData != null;
            backpackData.setString("colorName", newName);
            BackpackUtils.setBackpackData(backpack, backpackData);
        }
    }
}
