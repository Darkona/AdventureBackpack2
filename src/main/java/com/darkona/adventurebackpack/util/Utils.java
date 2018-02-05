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

import com.darkona.adventurebackpack.reference.BackpackTypes;

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

    public static int secondsToTicks(int seconds)
    {
        return seconds * 20;
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

    public static boolean inServer()
    {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
    }

    private static final EnumChatFormatting[] RAINBOW_SEQUENCE = {EnumChatFormatting.RED, EnumChatFormatting.GOLD,
            EnumChatFormatting.YELLOW, EnumChatFormatting.GREEN, EnumChatFormatting.AQUA, EnumChatFormatting.BLUE,
            EnumChatFormatting.DARK_PURPLE};

    public static String makeItRainbow(String theString)
    {
        StringBuilder rainbowed = new StringBuilder();
        for (int i = 0; i < theString.length(); i++)
        {
            rainbowed.append(RAINBOW_SEQUENCE[i % RAINBOW_SEQUENCE.length]).append(theString.charAt(i));
        }
        return rainbowed.toString();
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

    public static String getColoredSkinName(BackpackTypes type)
    {
        String result = "";
        String skinName = BackpackTypes.getSkinName(type);
        switch (type)
        {
            case BAT:
                result += EnumChatFormatting.DARK_PURPLE + skinName;
                break;
            case DRAGON:
                result += EnumChatFormatting.LIGHT_PURPLE + skinName;
                break;
            case PIGMAN:
                result += EnumChatFormatting.RED + skinName;
                break;
            case RAINBOW:
                result += Utils.makeItRainbow(skinName);
                break;
            case SQUID:
                result += EnumChatFormatting.DARK_AQUA + skinName;
                break;
            default:
                result += skinName;
                break;
        }
        return result;
    }
}
