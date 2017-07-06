package com.darkona.adventurebackpack.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import com.darkona.adventurebackpack.config.ConfigHandler;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class Utils
{
    public static float degreesToRadians(float degrees)
    {
        return degrees / 57.2957795f;
    }

    public static float radiansToDegrees(float radians)
    {
        return radians * 57.2957795f;
    }

    public static int isBlockRegisteredAsFluid(Block block)
    {
        int fluidID = -1;
        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values())
        {
            fluidID = (fluid.getBlock() == block) ? fluid.getID() : -1;
            if (fluidID > 0)
            {
                return fluidID;
            }
        }
        return fluidID;
    }

    public static ChunkCoordinates findBlock2D(World world, int x, int y, int z, Block block, int range)
    {
        for (int i = x - range; i <= x + range; i++)
        {
            for (int j = z - range; j <= z + range; j++)
            {
                if (world.getBlock(i, y, j) == block)
                {
                    return new ChunkCoordinates(i, y, j);
                }
            }
        }
        return null;
    }

    public static ChunkCoordinates findBlock3D(World world, int x, int y, int z, Block block, int hRange, int vRange)
    {
        for (int i = (y - vRange); i <= (y + vRange); i++)
        {
            for (int j = (x - hRange); j <= (x + hRange); j++)
            {
                for (int k = (z - hRange); k <= (z + hRange); k++)
                {
                    if (world.getBlock(j, i, k) == block)
                    {
                        return new ChunkCoordinates(j, i, k);
                    }
                }
            }
        }
        return null;
    }

    public static String capitalize(String s)
    {
        // Character.toUpperCase(itemName.charAt(0)) + itemName.substring(1);
        return s.substring(0, 1).toUpperCase().concat(s.substring(1));
    }

    public static int getOppositeCardinalFromMeta(int meta)
    {
        return (meta % 2 == 0) ? (meta == 0) ? 2 : 0 : ((meta + 1) % 4) + 1;
    }

    //This is some black magic that returns a block or entity as far as the argument reach goes.
    public static MovingObjectPosition getMovingObjectPositionFromPlayersHat(World world, EntityPlayer player, boolean flag, double reach)
    {
        float f = 1.0F;
        float playerPitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float playerYaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double playerPosX = player.prevPosX + (player.posX - player.prevPosX) * f;
        double playerPosY = (player.prevPosY + (player.posY - player.prevPosY) * f + 1.6200000000000001D) - player.yOffset;
        double playerPosZ = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
        Vec3 vecPlayer = Vec3.createVectorHelper(playerPosX, playerPosY, playerPosZ);
        float cosYaw = (float) Math.cos(-playerYaw * 0.01745329F - 3.141593F);
        float sinYaw = (float) Math.sin(-playerYaw * 0.01745329F - 3.141593F);
        float cosPitch = (float) -Math.cos(-playerPitch * 0.01745329F);
        float sinPitch = (float) Math.sin(-playerPitch * 0.01745329F);
        float pointX = sinYaw * cosPitch;
        float pointY = sinPitch;
        float pointZ = cosYaw * cosPitch;
        Vec3 vecPoint = vecPlayer.addVector(pointX * reach, pointY * reach, pointZ * reach);
        return world.func_147447_a/*rayTraceBlocks_do_do*/(vecPlayer, vecPoint, flag, !flag, flag);
    }

    public static String printCoordinates(int x, int y, int z)
    {
        return "X= " + x + ", Y= " + y + ", Z= " + z;
    }

    public static int secondsToTicks(int seconds)
    {
        return seconds * 20;
    }

    public static int secondsToTicks(float seconds)
    {
        return (int) seconds * 20;
    }

    public static boolean inServer()
    {
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        return side == Side.SERVER;
    }

    private static ChunkCoordinates checkCoordsForBackpack(IBlockAccess world, int origX, int origZ, int x, int y, int z, boolean except)
    {
        if (world.isAirBlock(x, y, z) || isReplaceable(world, x, y, z))
        {
            return new ChunkCoordinates(x, y, z);
        }
        return null;
    }

    public static boolean isReplaceable(IBlockAccess world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        return block.isReplaceable(world, x, y, z);
    }

    private static ChunkCoordinates checkCoordsForPlayer(IBlockAccess world, int origX, int origZ, int x, int y, int z, boolean except)
    {
        LogHelper.info("Checking coordinates in X=" + x + ", Y=" + y + ", Z=" + z);
        if (except && world.isSideSolid(x, y - 1, z, ForgeDirection.UP, true) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z) && !areCoordinatesTheSame2D(origX, origZ, x, z))
        {
            LogHelper.info("Found spot with the exception of the origin point");
            return new ChunkCoordinates(x, y, z);
        }
        if (!except && world.isSideSolid(x, y - 1, z, ForgeDirection.UP, true) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
        {
            LogHelper.info("Found spot without exceptions");
            return new ChunkCoordinates(x, y, z);
        }
        return null;
    }

    /**
     * Gets you the nearest Empty Chunk Coordinates, free of charge! Looks in two dimensions and finds a block
     * that a: can have stuff placed on it and b: has space above it.
     * This is a spiral search, will begin at close range and move out.
     *
     * @param world  The world object.
     * @param origX  Original X coordinate
     * @param origZ  Original Z coordinate
     * @param X      Moving X coordinate, should be the same as origX when called.
     * @param Y      Y coordinate, does not move.
     * @param Z      Moving Z coordinate, should be the same as origZ when called.
     * @param radius The radius of the search. If set to high numbers, will create a ton of lag
     * @param except Wether to include the origin of the search as a valid block.
     * @param steps  Number of steps of the recursive recursiveness that recurses through the recursion. It is the first size of the spiral, should be one (1) always at the first call.
     * @param pass   Pass switch for the witchcraft I can't quite explain. Set to 0 always at the beggining.
     * @param type   True = for player, False = for backpack
     * @return The coordinates of the block in the chunk of the world of the game of the server of the owner of the computer, where you can place something above it.
     */
    public static ChunkCoordinates getNearestEmptyChunkCoordinatesSpiral(IBlockAccess world, int origX, int origZ, int X, int Y, int Z, int radius, boolean except, int steps, byte pass, boolean type)
    {
        //Spiral search, because I'm awesome :)
        //This is so the backpack tries to get placed near the death point first
        //And then goes looking farther away at each step
        //Steps mod 2 == 0 => X++, Z--
        //Steps mod 2 == 1 => X--, Z++

        if (steps >= radius) return null;
        int i = X, j = Z;
        if (steps % 2 == 0)
        {
            if (pass == 0)
            {
                for (; i <= X + steps; i++)
                {
                    ChunkCoordinates coords = type ? checkCoordsForPlayer(world, origX, origZ, X, Y, Z, except) : checkCoordsForBackpack(world, origX, origZ, X, Y, Z, except);
                    if (coords != null)
                    {
                        return coords;
                    }
                }
                pass++;
                return getNearestEmptyChunkCoordinatesSpiral(world, origX, origZ, i, Y, j, radius, except, steps, pass, type);
            }
            if (pass == 1)
            {
                for (; j >= Z - steps; j--)
                {
                    ChunkCoordinates coords = type ? checkCoordsForPlayer(world, origX, origZ, X, Y, Z, except) : checkCoordsForBackpack(world, origX, origZ, X, Y, Z, except);
                    if (coords != null)
                    {
                        return coords;
                    }
                }
                pass--;
                steps++;
                return getNearestEmptyChunkCoordinatesSpiral(world, origX, origZ, i, Y, j, radius, except, steps, pass, type);
            }
        }

        if (steps % 2 == 1)
        {
            if (pass == 0)
            {
                for (; i >= X - steps; i--)
                {
                    ChunkCoordinates coords = type ? checkCoordsForPlayer(world, origX, origZ, X, Y, Z, except) : checkCoordsForBackpack(world, origX, origZ, X, Y, Z, except);
                    if (coords != null)
                    {
                        return coords;
                    }
                }
                pass++;
                return getNearestEmptyChunkCoordinatesSpiral(world, origX, origZ, i, Y, j, radius, except, steps, pass, type);
            }
            if (pass == 1)
            {
                for (; j <= Z + steps; j++)
                {
                    ChunkCoordinates coords = type ? checkCoordsForPlayer(world, origX, origZ, X, Y, Z, except) : checkCoordsForBackpack(world, origX, origZ, X, Y, Z, except);
                    if (coords != null)
                    {
                        return coords;
                    }
                }
                pass--;
                steps++;
                return getNearestEmptyChunkCoordinatesSpiral(world, origX, origZ, i, Y, j, radius, except, steps, pass, type);
            }
        }

        return null;
    }

    /**
     * Compares two coordinates. Heh.
     *
     * @param X1 First coordinate X.
     * @param Y1 First coordinate Y.
     * @param Z1 First coordinate Z.
     * @param X2 Second coordinate X.
     * @param Y2 Second coordinate Y.
     * @param Z2 Second coordinate Z. I really didn't need to type all that, its obvious.
     * @return If both coordinates are the same, returns true. This is the least helpful javadoc ever.
     */
    private static boolean areCoordinatesTheSame(int X1, int Y1, int Z1, int X2, int Y2, int Z2)
    {
        return (X1 == X2 && Y1 == Y2 && Z1 == Z2);
    }

    private static boolean areCoordinatesTheSame2D(int X1, int Z1, int X2, int Z2)
    {
        return (X1 == X2 && Z1 == Z2);
    }

    /**
     * Seriously why doesn't Java's instanceof check for null?
     *
     * @return true if the object is not null and is an instance of the supplied class.
     */
    public static boolean notNullAndInstanceOf(Object object, Class clazz)
    {
        return object != null && clazz.isInstance(object);
    }

    public static String getFirstWord(String text)
    {
        if (text.indexOf(' ') > -1)
        { // Check if there is more than one word.
            String firstWord = text.substring(0, text.indexOf(' '));
            String secondWord = text.substring(text.indexOf(' ') + 1);
            return firstWord.equals("Molten") ? secondWord : firstWord; // Extract first word.
        } else
        {
            return text; // Text is the first word itself.
        }
    }

    public static String makeItRainbow(String theString)
    {
        EnumChatFormatting[] rainbowSequence = {EnumChatFormatting.RED, EnumChatFormatting.GOLD, EnumChatFormatting.YELLOW,
                EnumChatFormatting.GREEN, EnumChatFormatting.AQUA, EnumChatFormatting.BLUE, EnumChatFormatting.DARK_PURPLE};

        StringBuilder rainbowed = new StringBuilder();
        for (int i = 0; i < theString.length(); i++)
        {
            rainbowed.append(rainbowSequence[i % 7]).append(theString.charAt(i));
        }
        return rainbowed.toString();
    }

    public static boolean isDimensionAllowed (int dimensionID)
    {
        String currentDimID = String.valueOf(dimensionID);
        for (String forbiddenID : ConfigHandler.forbiddenDimensions)
        {
            if (currentDimID.equals(forbiddenID))
            {
                return false;
            }
        }
        return true;
    }
}
