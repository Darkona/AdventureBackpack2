package com.darkona.adventurebackpack.util;

import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.items.ItemAdventureBackpack;
import com.darkona.adventurebackpack.items.ItemAdventureHat;
import com.darkona.adventurebackpack.items.ItemPistonBoots;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Darkona on 12/10/2014.
 */
public class Utils {
    public static boolean isWearingHat(EntityPlayer player) {
        return player.inventory.armorInventory[3] != null && player.inventory.armorInventory[3].getItem() instanceof ItemAdventureHat;
    }

    public static boolean isWearingBackpack(EntityPlayer player) {
        return player.inventory.armorInventory[2] != null && player.inventory.armorInventory[2].getItem() instanceof ItemAdventureBackpack;
    }

    public static boolean isHoldingBackpack(EntityPlayer player) {
        return player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemAdventureBackpack;
    }

    public static boolean isWearingBoots(EntityPlayer player) {
        return player.inventory.armorInventory[0] != null && player.inventory.armorInventory[0].getItem() instanceof ItemPistonBoots;
    }

    public static ItemStack getWearingHat(EntityPlayer player) {
        if (isWearingHat(player)) return player.inventory.armorInventory[3];
        return null;
    }

    public static ItemStack getWearingBackpack(EntityPlayer player) {
        if (isWearingBackpack(player))
            return player.inventory.armorInventory[2];
        return null;
    }

    public static ItemStack getHoldingBackpack(EntityPlayer player) {
        if (isHoldingBackpack(player))
            return player.inventory.getCurrentItem();
        return null;
    }

    public static ItemStack getWearingBoots(EntityPlayer player) {
        return isWearingBoots(player) ? player.inventory.armorInventory[0] : null;
    }

    public static int isBlockRegisteredAsFluid(Block block) {
        /*
         * for (Map.Entry<String,Fluid> fluid :
		 * getRegisteredFluids().entrySet()) { int ID =
		 * (fluid.getValue().getBlockID() == BlockID) ? fluid.getValue().getID()
		 * : -1; if (ID > 0) return ID; }
		 */
        int fluidID = -1;
        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
            fluidID = (fluid.getBlock() == block) ? fluid.getID() : -1;
            if (fluidID > 0)
                return fluidID;
        }
        return fluidID;
    }

    public static boolean shouldGiveEmpty(ItemStack cont) {
        boolean valid = true;
        // System.out.println("Item class name is: "+cont.getItem().getClass().getName());

        try {
            // Industrialcraft cells
            // if (apis.ic2.api.item.Items.getItem("cell").getClass().isInstance(cont.getItem()))
            // {
            //     valid = false;
            // }
            // Forestry capsules
            if (java.lang.Class.forName("forestry.core.items.ItemLiquidContainer").isInstance(cont.getItem())) {
                valid = false;
            }
        } catch (Exception oops) {

        }
        // Others

        return valid;
    }

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

//    public static String getBackpackColorName(TileAdventureBackpack te) {
//        if (te == null) return "";
//        if (te.getColorName() == null) {
//            te.set = new NBTTagCompound();
//        }
//        if (!te.stackTagCompound.hasKey("color") || te.stackTagCompound.getString("color").isEmpty()) {
//            te.stackTagCompound.setString("color", "Standard");
//            te.stackTagCompound.setString("colorName", "Standard");
//        }
//        return te.stackTagCompound.getString("colorName");
//    }

    public static String[] backpackNames = {
            "BeefRaw", "Black", "BlazeRod", "BlockCoal", "BlockDiamond", "BlockEmerald", "BlockGold", "BlockIron", "BlockLapis",
            "BlockRedstone", "Blue", "Bone", "BookShelf", "Brown", "Cactus", "Cake", "Chest", "Cloth", "Cookie", "Cyan", "DragonEgg",
            "Egg", "Electric", "EnchantmentTable", "EnderPearl", "EyeOfEnder", "Feather", "FishRaw", "GhastTear", "Gray", "Green", "HayBlock",
            "Leather", "LightBlue", "Lightgem", "LightGray", "Lime", "Magenta", "MagmaCream", "Melon", "MushroomBrown", "MushroomRed", "MushroomStew",
            "NetherStalkSeeds", "NetherStar", "Obsidian", "Orange", "Pigman", "Pink", "PorkchopRaw", "Pumpkin", "Purple", "QuartzBlock", "Rainbow", "Red",
            "Sandstone", "Silver", "Skull.creeper", "Skull.skeleton", "Skull.wither", "Skull.zombie", "SlimeBall", "Snow", "SpiderEye", "Sponge", "Standard",
            "White", "Yellow"
    };

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

    public static ChunkCoordinates findBlock(World world, int x, int y, int z, Block block, int range) {
        for (int i = x - range; i <= x + range; i++) {
            for (int j = z - range; j <= z + range; j++) {
                if (world.getBlock(i, y, j) == block) {
                    return new ChunkCoordinates(i, y, j);
                }
            }
        }
        return null;
    }

    public static String getDisplayNameForColor(String color) {
        for (Map.Entry entry : itemNames.entrySet()) {
            if (((String) entry.getKey()).equals(color)) {
                return (String) entry.getValue();
            }
        }
        return color;
    }


    public static String capitalize(String s) {
        // Character.toUpperCase(itemName.charAt(0)) + itemName.substring(1);
        return s.substring(0, 1).toUpperCase().concat(s.substring(1));
    }

    public static int getOppositeCardinalFromMeta(int meta) {
        return (meta % 2 == 0) ? (meta == 0) ? 2 : 0 : ((meta + 1) % 4) + 1;
    }

    //This is some black magic that returns a block or com.darkona.adventurebackpack.entity as far as the argument reach goes.
    public static MovingObjectPosition getMovingObjectPositionFromPlayersHat(World world, EntityPlayer player, boolean flag, double reach) {
        float f = 1.0F;
        float playerPitch = player.prevRotationPitch
                + (player.rotationPitch - player.prevRotationPitch) * f;
        float playerYaw = player.prevRotationYaw
                + (player.rotationYaw - player.prevRotationYaw) * f;
        double playerPosX = player.prevPosX + (player.posX - player.prevPosX)
                * f;
        double playerPosY = (player.prevPosY + (player.posY - player.prevPosY)
                * f + 1.6200000000000001D)
                - player.yOffset;
        double playerPosZ = player.prevPosZ + (player.posZ - player.prevPosZ)
                * f;
        Vec3 vecPlayer = Vec3.createVectorHelper(playerPosX, playerPosY,
                playerPosZ);
        float cosYaw = (float) Math.cos(-playerYaw * 0.01745329F - 3.141593F);
        float sinYaw = (float) Math.sin(-playerYaw * 0.01745329F - 3.141593F);
        float cosPitch = (float) -Math.cos(-playerPitch * 0.01745329F);
        float sinPitch = (float) Math.sin(-playerPitch * 0.01745329F);
        float pointX = sinYaw * cosPitch;
        float pointY = sinPitch;
        float pointZ = cosYaw * cosPitch;
        Vec3 vecPoint = vecPlayer.addVector(pointX * reach, pointY * reach,
                pointZ * reach);
        MovingObjectPosition movingobjectposition = world.func_147447_a/*rayTraceBlocks_do_do*/(
                vecPlayer, vecPoint, flag, !flag, flag);
        return movingobjectposition;
    }

    /**
     * Will return a backpack inventory from a backpack on the player's armor
     * slot if true, or from his hand if false;
     *
     * @param player  the player com.darkona.adventurebackpack.entity
     * @param wearing boolean flag
     * @return
     */
    public static InventoryItem getBackpackInv(EntityPlayer player, boolean wearing) {
        return new InventoryItem((wearing) ? player.inventory.armorItemInSlot(2) : player.getCurrentEquippedItem());
    }

    public static String printCoordinates(int x, int y, int z) {
        return "X= " + x + ", Y= " + y + ", Z= " + z;
    }
}
