package com.darkona.adventurebackpack.util;

import com.darkona.adventurebackpack.AdventureBackpack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Calendar;
import java.util.UUID;

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

    public static int[] calculateEaster(int year)
    {
        int a = year % 19,
                b = year / 100,
                c = year % 100,
                d = b / 4,
                e = b % 4,
                g = (8 * b + 13) / 25,
                h = (19 * a + b - d - g + 15) % 30,
                j = c / 4,
                k = c % 4,
                m = (a + 11 * h) / 319,
                r = (2 * e + 2 * j - k - h + m + 32) % 7,
                n = (h - m + r + 90) / 25,
                p = (h - m + r + n + 19) % 32;

        return new int[]{n, p};
    }

    public static String getHoliday()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH) + 1,
                day = calendar.get(Calendar.DAY_OF_MONTH);

        if (AdventureBackpack.instance.chineseNewYear) return "ChinaNewYear";
        if (AdventureBackpack.instance.hannukah) return "Hannukah";
        if (month == Utils.calculateEaster(year)[0] && day == Utils.calculateEaster(year)[1]) return "Easter";
        String dia = "Standard";
        if (month == 1)
        {
            if (day == 1) dia = "NewYear";
            if (day == 28) dia = "Shuttle";
        }
        if (month == 2)
        {
            if (day == 1) dia = "Shuttle";
            if (day == 14) dia = "Valentines";
            if (day == 23) dia = "Fatherland";
        }
        if (month == 3)
        {
            if (day == 17) dia = "Patrick";
        }
        if (month == 4)
        {
            if (day == 1) dia = "Fools";
            if (day == 25) dia = "Italy";
        }
        if (month == 5)
        {
            if (day == 8 || day == 9 || day == 10) dia = "Liberation";
        }
        if (month == 6)
        {
        }
        if (month == 7)
        {
            if (day == 4) dia = "USA";
            if (day == 24) dia = "Bolivar";
            if (day == 14) dia = "Bastille";
        }
        if (month == 8)
        {
        }
        if (month == 9)
        {
            if (day == 19) dia = "Pirate";
        }
        if (month == 10)
        {
            if (day == 3) dia = "Germany";
            if (day == 12) dia = "Columbus";
            if (day == 31) dia = "Halloween";
        }
        if (month == 11)
        {
            if (day == 2) dia = "Muertos";
        }
        if (month == 12)
        {
            if (day >= 22 && day <= 26) dia = "Christmas";
            if (day == 31) dia = "NewYear";
        }
        return dia;
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

    public static boolean shouldGiveEmpty(ItemStack cont)
    {
        boolean valid = true;

        try
        {
            if (java.lang.Class.forName("forestry.core.items.ItemLiquidContainer").isInstance(cont.getItem()))
            {
                valid = false;
            }
        } catch (Exception oops)
        {
        }

        return valid;
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

    public static String capitalize(String s)
    {
        return s.substring(0, 1).toUpperCase().concat(s.substring(1));
    }

    public static int getOppositeCardinalFromMeta(int meta)
    {
        return (meta % 2 == 0) ? (meta == 0) ? 2 : 0 : ((meta + 1) % 4) + 1;
    }

    public static MovingObjectPosition getMovingObjectPositionFromPlayersHat(World world, EntityPlayer player, boolean flag, double reach)
    {
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
        return world.func_147447_a(
                vecPlayer, vecPoint, flag, !flag, flag);
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

    public static boolean isFreeSpace(IBlockAccess world, int X, int Y, int Z)
    {
        Block block = world.getBlock(X, Y, Z);
        return block.isAir(world, X, Y, Z)
                || (block.isReplaceable(world, X, Y, Z) && !block.getMaterial().isLiquid());
    }

    private static boolean isBackpackSpot(IBlockAccess world, int X, int Y, int Z)
    {
        return world.isSideSolid(X, Y - 1, Z, ForgeDirection.UP, true) && isFreeSpace(world, X, Y, Z);
    }

    private static boolean isPlayerSpot(IBlockAccess world, int X, int Y, int Z)
    {
        return world.isSideSolid(X, Y - 1, Z, ForgeDirection.UP, true) && world.isAirBlock(X, Y, Z) && world.isAirBlock(X, Y + 1, Z);
    }

    public static ChunkCoordinates getNearestEmptyChunkCoordinatesSpiral(final IBlockAccess world, final int X, final int Y, final int Z, int radius, int vertical, final boolean except, final boolean type)
    {
        long found = SpiralSearch.find(X, Y, Z, radius, vertical, new SpiralSearch.Cell()
        {
            @Override
            public boolean accept(int x, int y, int z)
            {
                if (except && x == X && z == Z && (y == Y || y == Y + 1)) return false;
                return type ? isPlayerSpot(world, x, y, z) : isBackpackSpot(world, x, y, z);
            }
        });
        return found == SpiralSearch.NONE ? null
                : new ChunkCoordinates(SpiralSearch.unpackX(found), SpiralSearch.unpackY(found), SpiralSearch.unpackZ(found));
    }

    public static boolean notNullAndInstanceOf(Object object, Class clazz)
    {
        return object != null && clazz.isInstance(object);
    }

    public static String getFirstWord(String text)
    {
        if (text.indexOf(' ') > -1)
        {
            String firstWord = text.substring(0, text.indexOf(' '));
            String secondWord = text.substring(text.indexOf(' ') + 1);
            return firstWord.equals("Molten") ? secondWord : firstWord;
        } else
        {
            return text;
        }
    }

    public static UUID parseUUIDOrNull(String text)
    {
        if (text == null || text.isEmpty()) return null;
        try
        {
            return UUID.fromString(text);
        } catch (IllegalArgumentException e)
        {
            return null;
        }
    }
}
