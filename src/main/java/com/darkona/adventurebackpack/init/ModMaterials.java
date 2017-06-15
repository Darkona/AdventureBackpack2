package com.darkona.adventurebackpack.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

/**
 * Created on 11/10/2014
 *
 * @author Darkona
 */
public class ModMaterials
{
    public static Item.ToolMaterial ruggedIron = EnumHelper.addToolMaterial("RUGGED_IRON", 2, 350, 6.5F, 5.2F, 10);
    public static ItemArmor.ArmorMaterial ruggedLeather = EnumHelper.addArmorMaterial("RUGGED_LEATHER", 15, new int[]{2, 5, 4, 2}, 12);
}
