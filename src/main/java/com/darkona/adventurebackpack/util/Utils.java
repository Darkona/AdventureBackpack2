package com.darkona.adventurebackpack.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
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

    public static String printCoordinates(int cX, int cY, int cZ)
    {
        return "X= " + cX + ", Y= " + cY + ", Z= " + cZ;
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

    /**
     * Seriously why doesn't Java's instanceof check for null?
     *
     * @return true if the object is not null and is an instance of the supplied class.
     */
    public static boolean notNullAndInstanceOf(Object object, Class clazz) //TODO double check this
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
        }
        else
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

    public static boolean isDimensionAllowed(int dimensionID)
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

    public static int[] createSlotArray(int first, int count)
    {
        int[] slots = new int[count];
        for (int i = first; i < first + count; i++)
        {
            slots[i - first] = i;
        }
        return slots;
    }
}
