package com.darkona.adventurebackpack.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.reference.LoadedMods;

/**
 * Created on 24.03.2017
 *
 * @author Ugachaga
 */
public final class EnchUtils
{
    // -3 - disabled by config
    // -2 - EnderIO not found
    // -1 - enchantment not found
    private static final int SOUL_BOUND_ID = setSoulBoundID();

    private EnchUtils() {}

    private static int setSoulBoundID()
    {
        if (!ConfigHandler.allowSoulBound)
            return -3;

        if (!LoadedMods.ENDERIO)
            return -2;

        for (Enchantment ench : Enchantment.enchantmentsList)
            if (ench != null && ench.getName().equals("enchantment.enderio.soulBound"))
                return ench.effectId;

        return -1;
    }

    public static boolean isSoulBounded(ItemStack stack)
    {
        NBTTagList stackEnch = stack.getEnchantmentTagList();
        if (SOUL_BOUND_ID >= 0 && stackEnch != null)
        {
            for (int i = 0; i < stackEnch.tagCount(); i++)
            {
                int id = stackEnch.getCompoundTagAt(i).getInteger("id");
                if (id == SOUL_BOUND_ID)
                    return true;
            }
        }
        return false;
    }

    public static boolean isSoulBook(ItemStack book)
    {
        if (SOUL_BOUND_ID >= 0 && book.hasTagCompound())
        {
            NBTTagCompound bookData = book.stackTagCompound;
            if (bookData.hasKey("StoredEnchantments"))
            {
                NBTTagList bookEnch = bookData.getTagList("StoredEnchantments", NBT.TAG_COMPOUND);
                if (!bookEnch.getCompoundTagAt(1).getBoolean("id")) // only pure soulbook allowed
                {
                    int id = bookEnch.getCompoundTagAt(0).getInteger("id");
                    return id == SOUL_BOUND_ID;
                }
            }
        }
        return false;
    }
}