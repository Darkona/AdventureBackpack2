package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

/**
 * Created by Darkona on 10/10/2014.
 */
public class ModGuiConfig extends GuiConfig
{

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public ModGuiConfig(GuiScreen guiScreen)
    {
        super(guiScreen,
                new ConfigElement(ConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                ModInfo.MOD_ID,
                false,
                false,
                GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
    }
}
