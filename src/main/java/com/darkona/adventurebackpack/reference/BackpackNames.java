package com.darkona.adventurebackpack.reference;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 15/10/2014
 *
 * @author Darkona
 */
public class BackpackNames {


    public static String[] backpackNames = {
            "BeefRaw", "Black", "BlazeRod", "BlockCoal", "BlockDiamond", "BlockEmerald", "BlockGold", "BlockIron", "BlockLapis",
            "BlockRedstone", "Blue", "Bone", "BookShelf", "Brown", "Cactus", "Cake", "Chest", "Cloth", "Cookie", "Cyan", "DragonEgg",
            "Egg", "Electric", "EnchantmentTable", "EnderPearl", "EyeOfEnder", "Feather", "FishRaw", "GhastTear", "Gray", "Green", "HayBlock",
            "Leather", "LightBlue", "Lightgem", "LightGray", "Lime", "Magenta", "MagmaCream", "Melon", "MushroomBrown", "MushroomRed", "MushroomStew",
            "NetherStalkSeeds", "NetherStar", "Obsidian", "Orange", "Pigman", "Pink", "PorkchopRaw", "Pumpkin", "Purple", "QuartzBlock", "Rainbow", "Red",
            "Sandstone", "Silver", "Skull.creeper", "Skull.skeleton", "Skull.wither", "Skull.zombie", "SlimeBall", "Snow", "SpiderEye", "Sponge", "Standard",
            "White", "Yellow"
    };

    /**
     * Remember to use these for Abilities
     */
    public static HashMap<String, String> itemNames = new HashMap<String, String>() {
        {
            put("BlazeRod", "Blaze");
            put("BeefRaw", "Cow");
            put("Bone", "Wolf");
            put("Skull.creeper", "Creeper");
            put("Skull.skeleton", "Skeleton");
            put("Skull.zombie", "Zombie");
            put("Skull.wither", "Wither Skeleton");
            put("SlimeBall", "Slime");
            put("MagmaCream", "Magma Cube");
            put("SpiderEye", "Spider");
            put("GhastTear", "Ghast");
            put("Feather", "Chicken");
            put("EyeOfEnder", "End");
            put("HayBlock", "Hay");
            put("FishRaw", "Ocelot");
            put("DragonEgg", "Dragon");
            put("NetherStar", "Wither");
            put("Pigman", "Zombie Pigman");
            put("MushroomBrown", "Brown Mushroom");
            put("MushroomRed", "Red Mushroom");
            put("PorkchopRaw", "Pig");
            put("BookShelf", "Books");
            put("BlockCoal", "Coal");
            put("BlockDiamond", "Diamond");
            put("BlockEmerald", "Emerald");
            put("BlockRedstone", "Redstone");
            put("BlockLapis", "Lapislazuli");
            put("QuartzBlock", "Quartz");
            put("Cloth", "Sheep");
            put("Lightgem", "Glowstone");
            put("EnchantmentTable", "Deluxe");
            put("Rainbow", "Nyan!");
            put("EnderPearl", "Enderman");
            put("MushroomStew", "Mooshroom");
            put("BlockGold", "Golden");
            put("BlockIron", "Iron");
            put("NetherStalkSeeds", "Nether");
            put("Rainbow", "Nyan!");
        }
    };

    public static String getBackpackColorName(ItemStack item) {
        if (item == null) return "";
        if (item.stackTagCompound == null) {
            item.stackTagCompound = new NBTTagCompound();
        }
        if (!item.stackTagCompound.hasKey("color") || item.stackTagCompound.getString("color").isEmpty()) {
            item.stackTagCompound.setString("color", "Standard");
            item.stackTagCompound.setString("colorName", "Standard");
        }
        return item.stackTagCompound.getString("colorName");
    }

    public static String getDisplayNameForColor(String color) {
        for (Map.Entry entry : itemNames.entrySet()) {
            if (((String) entry.getKey()).equals(color)) {
                return (String) entry.getValue();
            }
        }
        return color;
    }
}
