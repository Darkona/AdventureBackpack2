package com.darkona.adventurebackpack;

import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.references.ModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Created by Darkona on 11/10/2014.
 */
public class CreativeTabAB {

    public static final CreativeTabs LMRB_TAB = new CreativeTabs(ModInfo.MOD_ID.toLowerCase()) {
        @Override
        public Item getTabIconItem() {
            return ModItems.machete;
        }
    };

}
