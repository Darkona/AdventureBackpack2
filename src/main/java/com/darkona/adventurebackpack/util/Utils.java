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

    public static String makeItRainbow(String stringIn)
    {
        StringBuilder rainbowed = new StringBuilder(stringIn.length() * 3); // special characters = length * 2
        for (int i = 0; i < stringIn.length(); i++)
        {
            rainbowed.append(RAINBOW_SEQUENCE[i % RAINBOW_SEQUENCE.length]).append(stringIn.charAt(i));
        }
        return rainbowed.toString();
    }

    public static String getColoredSkinName(BackpackTypes type)
    {
        String result;
        String name = BackpackTypes.getLocalizedName(type);
        switch (type)
        {
            case BAT:
                result = EnumChatFormatting.DARK_PURPLE + name;
                break;
            case EMERALD:
                result = animateString(name, EnumChatFormatting.GREEN);
                break;
            case DIAMOND:
                result = animateString(name, EnumChatFormatting.AQUA);
                break;
            case DRAGON:
                result = EnumChatFormatting.LIGHT_PURPLE + name;
                break;
            case GOLD:
                result = animateString(name, EnumChatFormatting.YELLOW);
                break;
            case IRON_GOLEM:
                result = EnumChatFormatting.WHITE + name;
                break;
            case OBSIDIAN:
                result = animateString(name, EnumChatFormatting.DARK_PURPLE);
                break;
            case PIGMAN:
                result = EnumChatFormatting.RED + name;
                break;
            case QUARTZ:
                result = animateString(name, EnumChatFormatting.WHITE);
                break;
            case RAINBOW:
                result = makeItRainbow(name);
                break;
            case SQUID:
                result = EnumChatFormatting.DARK_AQUA + name;
                break;
            default:
                result = name;
                break;
        }
        return result;
    }

    private static String animateString(String stringIn, EnumChatFormatting bold)
    {
        return animateString(stringIn, EnumChatFormatting.GRAY, bold);
    }

    private static String animateString(String stringIn, EnumChatFormatting regular, EnumChatFormatting bold)
    {
        int len = stringIn.length();
        int time = Math.abs((int) Minecraft.getMinecraft().theWorld.getWorldTime());

        int k = 1; // animation slowness coefficient, changes charID every k ticks, k = 1 for max speed
        int charID = (time / k) % len ;

        int n = 100 / len; // makes n phases with len length
        int phaseFactor = (time / k) % (len * n);
        int phase = 1 + phaseFactor / len;

        if (phase == 1)
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
