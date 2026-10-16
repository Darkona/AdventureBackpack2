package com.darkona.adventurebackpack.client;

import com.darkona.adventurebackpack.config.ConfigHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

@SideOnly(Side.CLIENT)
public final class TooltipLines
{
    private TooltipLines() {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void add(List tooltip, String text)
    {
        tooltip.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(text, ConfigHandler.TOOLTIP_WIDTH));
    }

    public static void addTranslated(List tooltip, String key)
    {
        add(tooltip, StatCollector.translateToLocal(key));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean details(List tooltip, String key)
    {
        if (GuiScreen.isShiftKeyDown())
        {
            addTranslated(tooltip, key);
            return true;
        }
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("adventurebackpack:tooltip.holdShift"));
        return false;
    }
}
