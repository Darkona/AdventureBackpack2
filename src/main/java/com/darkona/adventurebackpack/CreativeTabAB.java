package com.darkona.adventurebackpack;

import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.reference.ModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by Darkona on 11/10/2014.
 */
public class CreativeTabAB {

    public static final CreativeTabs ADVENTURE_BACKPACK_CREATIVE_TAB = new CreativeTabs(ModInfo.MOD_ID.toLowerCase()) {
        @Override
        public Item getTabIconItem() {
            return ModItems.machete;
        }
    };

}
