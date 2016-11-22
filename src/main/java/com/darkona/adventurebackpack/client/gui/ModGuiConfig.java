package com.darkona.adventurebackpack.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.reference.ModInfo;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

/**
 * Created by Darkona on 10/10/2014.
 */
public class ModGuiConfig extends GuiConfig
{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ModGuiConfig(GuiScreen guiScreen)
    {
        super(guiScreen,
        	getConfigElements(),
                ModInfo.MOD_ID,
                false,
                false,
                GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
    }

    private static List<IConfigElement> getConfigElements()
    {
	List<IConfigElement> catGraphics = new ArrayList<IConfigElement>();
	List<IConfigElement> catStatus = new ArrayList<IConfigElement>();
	List<IConfigElement> catTanks = new ArrayList<IConfigElement>();
	List<IConfigElement> catSound = new ArrayList<IConfigElement>();
	List<IConfigElement> catItems = new ArrayList<IConfigElement>();
	List<IConfigElement> catDisallowed = new ArrayList<IConfigElement>();
	List<IConfigElement> catRecipes = new ArrayList<IConfigElement>();
	List<IConfigElement> catWorldGen = new ArrayList<IConfigElement>();
	List<IConfigElement> catCommon = new ArrayList<IConfigElement>();
	//Pattern commaDelimitedPattern = Pattern.compile("([A-Za-z]+((,){1}( )*|$))+?");

	//Graphics category
	catStatus.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CAT_STATUS)).getChildElements());
	catGraphics.add(new DummyCategoryElement("Status Overlay", "client.gui.ModGuiConfig.catStatus", catStatus));
	catTanks.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CAT_TANKS)).getChildElements());
	catGraphics.add(new DummyCategoryElement("Backpack Overlay", "client.gui.ModGuiConfig.catTanks", catTanks));
	catGraphics.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CAT_GRAPHICS)).getChildElements());
	catCommon.add(new DummyCategoryElement("Graphics", "client.gui.ModGuiConfig.catGraphics", catGraphics));
	//Sound category
	catSound.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CAT_SOUND)).getChildElements());
	catCommon.add(new DummyCategoryElement("Sound", "client.gui.ModGuiConfig.catSound", catSound));
	//Items category
	catRecipes.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CAT_RECIPES)).getChildElements());
	catItems.add(new DummyCategoryElement("Item Recipes", "client.gui.ModGuiConfig.catRecipes", catRecipes));
	catDisallowed.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CAT_DISALLOWED)).getChildElements());
	catItems.add(new DummyCategoryElement("Disallowed Items", "client.gui.ModGuiConfig.catDisallowed", catDisallowed));
	catItems.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CAT_ITEMS)).getChildElements());
	catCommon.add(new DummyCategoryElement("Items", "client.gui.ModGuiConfig.catItems", catItems));
	//WorldGen category
	catWorldGen.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CAT_WORLDGEN)).getChildElements());
	catCommon.add(new DummyCategoryElement("WorldGen", "client.gui.ModGuiConfig.catWorldGen", catWorldGen));
	//Common category
	catCommon.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CAT_COMMON)).getChildElements());

	return catCommon;
    }

}

