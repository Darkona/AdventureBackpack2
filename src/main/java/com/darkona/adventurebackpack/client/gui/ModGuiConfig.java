package com.darkona.adventurebackpack.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.reference.ModInfo;

/**
 * Created by Darkona on 10/10/2014.
 */
public class ModGuiConfig extends GuiConfig
{

    public ModGuiConfig(GuiScreen guiScreen)
    {
        super(guiScreen, getConfigElements(), ModInfo.MOD_ID, false, false, ModInfo.MOD_NAME);
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> configElements = new ArrayList<IConfigElement>();

        List<String> topCategories = Arrays.asList("gameplay", "graphics", "sound", "items", "worldgen");
        for (String categoryName : topCategories)
        {
            ConfigCategory category = ConfigHandler.config.getCategory(categoryName);
            configElements.add(new ConfigElement(category));
        }
        return configElements;
    }

}

