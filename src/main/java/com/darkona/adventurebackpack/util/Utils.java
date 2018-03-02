package com.darkona.adventurebackpack.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
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
    public static boolean inServer()
    {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
    }

    public static boolean inClient()
    {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
    }

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

    public static int[] createSlotArray(int first, int count)
    {
        int[] slots = new int[count];
        for (int i = first; i < first + count; i++)
        {
            slots[i - first] = i;
        }
        return slots;
    }

    private static final EnumChatFormatting[] RAINBOW_SEQUENCE = {EnumChatFormatting.RED, EnumChatFormatting.GOLD,
            EnumChatFormatting.YELLOW, EnumChatFormatting.GREEN, EnumChatFormatting.AQUA, EnumChatFormatting.BLUE,
            EnumChatFormatting.DARK_PURPLE};

    public static String makeItRainbow(String theString)
    {
        int len = theString.length();
        StringBuilder rainbowed = new StringBuilder( len * 3);
        for (int i = 0; i < len; i++)
        {
            rainbowed.append(RAINBOW_SEQUENCE[i % RAINBOW_SEQUENCE.length]).append(theString.charAt(i));
        }
        System.out.println(rainbowed.length());
        return rainbowed.toString();
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
                result += makeItRainbow(skinName);
                break;
            case SQUID:
                result += EnumChatFormatting.DARK_AQUA + skinName;
                break;
            case QUARTZ:
                result += makeWhiteAnimation(skinName);
                break;
            default:
                result += skinName;
                break;
        }
        return result;
    }

    private static String makeWhiteAnimation(String string)
    {
        return animateString(string, EnumChatFormatting.GRAY, EnumChatFormatting.WHITE);
    }

    private static String animateString(String stringIn, EnumChatFormatting regular, EnumChatFormatting bold)
    {
        int len = stringIn.length();
        int time = Math.abs((int) Minecraft.getMinecraft().theWorld.getWorldTime());
        int charID = time % len;

        int n = 10;
        int phaseFactor = time % (n * len); // makes n phases with len length
        int phase = 1 + phaseFactor / len;

        if (phase < 3)
        {
            return decorateCharInString(stringIn, charID, regular, bold, phase % 2 != 0);
        }
        return stringIn;
    }

    private static String decorateCharInString(String stringIn, int charID, EnumChatFormatting regular, EnumChatFormatting bold, boolean dir)
    {
        int len = stringIn.length();
        StringBuilder decorated = new StringBuilder();
        for (int i = dir ? 0 : len - 1; dir ? i < len : i >= 0; i = dir ? ++i : --i)
        {
            if (i == charID)
                decorated.append(bold);
            else if (i == (dir ? charID + 1 : charID - 1) && regular != null)
                decorated.append(regular);
            decorated.append(stringIn.charAt(dir ? i : len - 1 - i));
        }
        return decorated.toString();
    }
}
