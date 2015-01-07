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
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Calendar;

/**
 * Created by Darkona on 12/10/2014.
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
    public static int[] calculateEaster(int year){


        int     a = year % 19,
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

        int[] result = {n, p};
        return result ;
    }

    public static String getHoliday(){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH) + 1,
                day = calendar.get(Calendar.DAY_OF_MONTH);

        if(AdventureBackpack.instance.chineseNewYear) return "ChinaNewYear";
        if(AdventureBackpack.instance.hannukah) return "Hannukah";
        if (month == Utils.calculateEaster(year)[0] && day == Utils.calculateEaster(year)[1])return "Easter";
        String dia = "Standard";
        if(month == 1)
        {
            if (day == 1) dia = "NewYear";
            if (day == 28) dia = "Challenger";
        }
        if (month == 2)
        {
            if(day == 1) dia =  "Columbia";
            if(day == 14)dia =  "Valentines";
            if(day == 23)dia =  "Fatherland";
        }
        if (month == 3){
            if (day == 17)dia =  "Patrick";
        }
        if (month == 4)
        {
            if (day == 1) dia = "Fools";
            if (day == 25) dia = "Italy";
        }
        if (month == 5)
        {
            if(day == 8 || day == 9 || day == 10) dia = "Liberation";
        }
        if (month == 6){}
        if (month == 7)
        {
            if (day == 4) dia = "USA";
            if (day == 24) dia = "Bolivar";
            if (day == 14) dia = "Bastille";
        }
        if (month == 8){}
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
            if(day >=22 && day <= 26) dia = "Christmas";
            if (day == 31) dia = "NewYear";
        }
        //LogHelper.info("Today is: " + day + "/" + month + "/" + year + ". Which means today is: " + dia);
        return dia;

    }
    public static int isBlockRegisteredAsFluid(Block block)
    {
        /*
         * for (Map.Entry<String,Fluid> fluid :
		 * getRegisteredFluids().entrySet()) { int ID =
		 * (fluid.getValue().getBlockID() == BlockID) ? fluid.getValue().getID()
		 * : -1; if (ID > 0) return ID; }
		 */
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
        // System.out.println("Item class name is: "+cont.getItem().getClass().getName());

        try
        {
            // Industrialcraft cells
            // if (apis.ic2.api.item.Items.getItem("cell").getClass().isInstance(cont.getItem()))
            // {
            //     valid = false;
            // }
            // Forestry capsules
            if (java.lang.Class.forName("forestry.core.items.ItemLiquidContainer").isInstance(cont.getItem()))
            {
                valid = false;
            }
        } catch (Exception oops)
        {

        }
        // Others

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

    public static ChunkCoordinates findBlock3D(World world, int x, int y, int z, Block block, int range, int verticalRange)
    {
        for(int vertical = y - verticalRange; vertical <= vertical + verticalRange; vertical++)
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

    public static String printCoordinates(int x, int y, int z)
    {
        return "X= " + x + ", Y= " + y + ", Z= " + z;
    }

    /**
     * @param seconds
     * @return
     */
    public static int secondsToTicks(int seconds)
    {
        return seconds * 20;
    }

    public static int secondsToTicks(float seconds)
    {
        return (int)seconds*20;
    }
    public static boolean whereTheHellAmI()
    {
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        if (side == Side.SERVER)
        {
            return true;
        } else
        {
            return false;
        }
    }
}
