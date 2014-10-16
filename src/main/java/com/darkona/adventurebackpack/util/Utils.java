package com.darkona.adventurebackpack.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created by Darkona on 12/10/2014.
 */
public class Utils {

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


    public static String capitalize(String s) {
        // Character.toUpperCase(itemName.charAt(0)) + itemName.substring(1);
        return s.substring(0, 1).toUpperCase().concat(s.substring(1));
    }

    public static int getOppositeCardinalFromMeta(int meta) {
        return (meta % 2 == 0) ? (meta == 0) ? 2 : 0 : ((meta + 1) % 4) + 1;
    }

    //This is some black magic that returns a block or entity as far as the argument reach goes.
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

    public static String printCoordinates(int x, int y, int z) {
        return "X= " + x + ", Y= " + y + ", Z= " + z;
    }

    /**
     *
     * @param seconds
     * @return
     */
    public static int secondsToTicks(int seconds) {
        return seconds * 20;
    }
}
