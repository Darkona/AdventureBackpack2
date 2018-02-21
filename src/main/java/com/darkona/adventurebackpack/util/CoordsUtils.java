package com.darkona.adventurebackpack.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.init.ModBlocks;

/**
 * Created on 31.01.2018
 *
 * @author Ugachaga
 */
public class CoordsUtils
{
    private CoordsUtils() {}

    public static ChunkCoordinates findBlock2D(World world, int cX, int cY, int cZ, Block block, int range)
    {
        for (int i = cX - range; i <= cX + range; i++)
        {
            for (int j = cZ - range; j <= cZ + range; j++)
            {
                if (world.getBlock(i, cY, j) == block)
                {
                    return new ChunkCoordinates(i, cY, j);
                }
            }
        }
        return null;
    }

    public static ChunkCoordinates findBlock3D(World world, int cX, int cY, int cZ, Block block, int hRange, int vRange)
    {
        for (int i = (cY - vRange); i <= (cY + vRange); i++)
        {
            for (int j = (cX - hRange); j <= (cX + hRange); j++)
            {
                for (int k = (cZ - hRange); k <= (cZ + hRange); k++)
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

    private static ChunkCoordinates checkCoordsForBackpack(IBlockAccess world, int origX, int origZ, int cX, int cY, int cZ, boolean except)
    {
        if (world.isAirBlock(cX, cY, cZ) || isReplaceable(world, cX, cY, cZ))
        {
            return new ChunkCoordinates(cX, cY, cZ);
        }
        return null;
    }

    public static boolean isReplaceable(IBlockAccess world, int cX, int cY, int cZ)
    {
        Block block = world.getBlock(cX, cY, cZ);
        return block.isReplaceable(world, cX, cY, cZ);
    }

    private static ChunkCoordinates checkCoordsForPlayer(IBlockAccess world, int origX, int origZ, int cX, int cY, int cZ, boolean except)
    {
        LogHelper.info("Checking coordinates in X=" + cX + ", Y=" + cY + ", Z=" + cZ);
        if (except && world.isSideSolid(cX, cY - 1, cZ, ForgeDirection.UP, true) && world.isAirBlock(cX, cY, cZ) && world.isAirBlock(cX, cY + 1, cZ) && !areCoordinatesTheSame2D(origX, origZ, cX, cZ))
        {
            LogHelper.info("Found spot with the exception of the origin point");
            return new ChunkCoordinates(cX, cY, cZ);
        }
        if (!except && world.isSideSolid(cX, cY - 1, cZ, ForgeDirection.UP, true) && world.isAirBlock(cX, cY, cZ) && world.isAirBlock(cX, cY + 1, cZ))
        {
            LogHelper.info("Found spot without exceptions");
            return new ChunkCoordinates(cX, cY, cZ);
        }
        return null;
    }

    /**
     * Gets you the nearest Empty Chunk Coordinates, free of charge! Looks in two dimensions and finds a block
     * that a: can have stuff placed on it and b: has space above it.
     * This is a spiral search, will begin at close range and move out.
     *
     * @param world  The world object.
     * @param origX  Original cX coordinate
     * @param origZ  Original cZ coordinate
     * @param cX     Moving cX coordinate, should be the same as origX when called.
     * @param cY     cY coordinate, does not move.
     * @param cZ     Moving cZ coordinate, should be the same as origZ when called.
     * @param radius The radius of the search. If set to high numbers, will create a ton of lag
     * @param except Wether to include the origin of the search as a valid block.
     * @param steps  Number of steps of the recursive recursiveness that recurses through the recursion. It is the first size of the spiral, should be one (1) always at the first call.
     * @param pass   Pass switch for the witchcraft I can't quite explain. Set to 0 always at the beggining.
     * @param type   True = for player, False = for backpack
     * @return The coordinates of the block in the chunk of the world of the game of the server of the owner of the computer, where you can place something above it.
     */
    public static ChunkCoordinates getNearestEmptyChunkCoordinatesSpiral(IBlockAccess world, int origX, int origZ, int cX, int cY, int cZ, int radius, boolean except, int steps, byte pass, boolean type)
    {
        //Spiral search, because I'm awesome :)
        //This is so the backpack tries to get placed near the death point first
        //And then goes looking farther away at each step
        //Steps mod 2 == 0 => cX++, cZ--
        //Steps mod 2 == 1 => cX--, cZ++

        if (steps >= radius) return null;
        int i = cX, j = cZ;
        if (steps % 2 == 0)
        {
            if (pass == 0)
            {
                for (; i <= cX + steps; i++)
                {
                    ChunkCoordinates coords = type ? checkCoordsForPlayer(world, origX, origZ, cX, cY, cZ, except) : checkCoordsForBackpack(world, origX, origZ, cX, cY, cZ, except);
                    if (coords != null)
                    {
                        return coords;
                    }
                }
                pass++;
                return getNearestEmptyChunkCoordinatesSpiral(world, origX, origZ, i, cY, j, radius, except, steps, pass, type);
            }
            if (pass == 1)
            {
                for (; j >= cZ - steps; j--)
                {
                    ChunkCoordinates coords = type ? checkCoordsForPlayer(world, origX, origZ, cX, cY, cZ, except) : checkCoordsForBackpack(world, origX, origZ, cX, cY, cZ, except);
                    if (coords != null)
                    {
                        return coords;
                    }
                }
                pass--;
                steps++;
                return getNearestEmptyChunkCoordinatesSpiral(world, origX, origZ, i, cY, j, radius, except, steps, pass, type);
            }
        }

        if (steps % 2 == 1)
        {
            if (pass == 0)
            {
                for (; i >= cX - steps; i--)
                {
                    ChunkCoordinates coords = type ? checkCoordsForPlayer(world, origX, origZ, cX, cY, cZ, except) : checkCoordsForBackpack(world, origX, origZ, cX, cY, cZ, except);
                    if (coords != null)
                    {
                        return coords;
                    }
                }
                pass++;
                return getNearestEmptyChunkCoordinatesSpiral(world, origX, origZ, i, cY, j, radius, except, steps, pass, type);
            }
            if (pass == 1)
            {
                for (; j <= cZ + steps; j++)
                {
                    ChunkCoordinates coords = type ? checkCoordsForPlayer(world, origX, origZ, cX, cY, cZ, except) : checkCoordsForBackpack(world, origX, origZ, cX, cY, cZ, except);
                    if (coords != null)
                    {
                        return coords;
                    }
                }
                pass--;
                steps++;
                return getNearestEmptyChunkCoordinatesSpiral(world, origX, origZ, i, cY, j, radius, except, steps, pass, type);
            }
        }

        return null;
    }

    public static int[] canDeploySleepingBag(World world, EntityPlayer player, int cX, int cY, int cZ, boolean isTile)
    {
        int switchBy = -1;
        if (isTile)
        {
            TileAdventureBackpack te = (TileAdventureBackpack) world.getTileEntity(cX, cY, cZ);
            if (!te.isSleepingBagDeployed())
                switchBy = world.getBlockMetadata(cX, cY, cZ) & 3;
        }
        else
        {
            int playerDirection = MathHelper.floor_double((double) ((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
            int[] tileSequence = {2, 3, 0, 1};
            for (int i = 0; i < tileSequence.length; i++) // converts to use isTile format
            {
                if (playerDirection == i)
                {
                    switchBy = tileSequence[i];
                    break;
                }
            }
        }
        return getDirectionAndCoordsForSleepingBag(switchBy, world, cX, cY, cZ);
    }

    private static int[] getDirectionAndCoordsForSleepingBag(int switchBy, World world, int cX, int cY, int cZ)
    {
        int direction = -1;
        switch (switchBy)
        {
            case 0:
                --cZ;
                if (world.isAirBlock(cX, cY, cZ) && world.getBlock(cX, cY - 1, cZ).getMaterial().isSolid())
                {
                    if (world.isAirBlock(cX, cY, cZ - 1) && world.getBlock(cX, cY - 1, cZ - 1).getMaterial().isSolid())
                    {
                        direction = 2;
                    }
                }
                break;
            case 1:
                ++cX;
                if (world.isAirBlock(cX, cY, cZ) && world.getBlock(cX, cY - 1, cZ).getMaterial().isSolid())
                {
                    if (world.isAirBlock(cX + 1, cY, cZ) && world.getBlock(cX + 1, cY - 1, cZ).getMaterial().isSolid())
                    {
                        direction = 3;
                    }
                }
                break;
            case 2:
                ++cZ;
                if (world.isAirBlock(cX, cY, cZ) && world.getBlock(cX, cY - 1, cZ).getMaterial().isSolid())
                {
                    if (world.isAirBlock(cX, cY, cZ + 1) && world.getBlock(cX, cY - 1, cZ + 1).getMaterial().isSolid())
                    {
                        direction = 0;
                    }
                }
                break;
            case 3:
                --cX;
                if (world.isAirBlock(cX, cY, cZ) && world.getBlock(cX, cY - 1, cZ).getMaterial().isSolid())
                {
                    if (world.isAirBlock(cX - 1, cY, cZ) && world.getBlock(cX - 1, cY - 1, cZ).getMaterial().isSolid())
                    {
                        direction = 1;
                    }
                }
                break;
            default:
                break;
        }
        return new int[]{direction, cX, cY, cZ};
    }

    public static boolean spawnSleepingBag(EntityPlayer player, World world, int meta, int cX, int cY, int cZ)
    {
        Block sleepingBag = ModBlocks.blockSleepingBag;
        if (world.setBlock(cX, cY, cZ, sleepingBag, meta, 3))
        {
            world.playSoundAtEntity(player, Block.soundTypeCloth.func_150496_b(), 0.5f, 1.0f);
            switch (meta & 3)
            {
                case 0:
                    ++cZ;
                    break;
                case 1:
                    --cX;
                    break;
                case 2:
                    --cZ;
                    break;
                case 3:
                    ++cX;
                    break;
            }
            return world.setBlock(cX, cY, cZ, sleepingBag, meta + 8, 3);
        }
        return false;
    }

    /**
     * Compares two coordinates. Heh.
     *
     * @param cX1 First coordinate X.
     * @param cY1 First coordinate Y.
     * @param cZ1 First coordinate Z.
     * @param cX2 Second coordinate X.
     * @param cY2 Second coordinate Y.
     * @param cZ2 Second coordinate Z. I really didn't need to type all that, its obvious.
     * @return If both coordinates are the same, returns true. This is the least helpful javadoc ever.
     */
    private static boolean areCoordinatesTheSame(int cX1, int cY1, int cZ1, int cX2, int cY2, int cZ2)
    {
        return (cX1 == cX2 && cY1 == cY2 && cZ1 == cZ2);
    }

    private static boolean areCoordinatesTheSame2D(int cX1, int cZ1, int cX2, int cZ2)
    {
        return (cX1 == cX2 && cZ1 == cZ2);
    }
}
