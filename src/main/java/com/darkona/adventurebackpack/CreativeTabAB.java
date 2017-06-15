package com.darkona.adventurebackpack;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.reference.ModInfo;

/**
 * Created on 11/10/2014.
 *
 * @author Javier Darkona
 */
public class CreativeTabAB
{
    public static final CreativeTabs ADVENTURE_BACKPACK_CREATIVE_TAB = new CreativeTabs(ModInfo.MOD_ID.toLowerCase())
    {
        @Override
        public Item getTabIconItem()
        {
            return ModItems.machete;
        }

        @Override
        public String getTranslatedTabLabel()
        {
            return super.getTranslatedTabLabel();
        }

        @Override
        public String getTabLabel()
        {
            return ModInfo.MOD_ID.toLowerCase();
        }
    };
}
