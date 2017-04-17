package com.darkona.adventurebackpack.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import com.darkona.adventurebackpack.config.ConfigHandler;

/**
 * Created by Ugachaga on 24.03.2017.
 */

public class EnchUtils
{
    static
    {
        setSoulBoundID();
    }

    // -3 - disabled by config
    // -2 - EnderIO not found
    // -1 - enchantment not found
    private static int soulBoundID;

    public static int getSoulBoundID()
    {
        return soulBoundID;
    }

    private static void setSoulBoundID()
    {
        if (!ConfigHandler.allowSoulBound)
        {
            soulBoundID = -3;
            return;
        }
        if (!ConfigHandler.IS_ENDERIO)
        {
            soulBoundID = -2;
            return;
        }
        for (Enchantment ench : Enchantment.enchantmentsList)
        {
            if (ench != null && ench.getName().equals("enchantment.enderio.soulBound"))
            {
                soulBoundID = ench.effectId;
                return;
            }
        }
        soulBoundID = -1;
    }

    public static boolean isSoulBounded(ItemStack stack)
    {
        NBTTagList stackEnch = stack.getEnchantmentTagList();
        if (getSoulBoundID() >= 0 && stackEnch != null)
        {
            for (int i = 0; i < stackEnch.tagCount(); i++)
            {
                int id = stackEnch.getCompoundTagAt(i).getInteger("id");
                if (id == getSoulBoundID())
                    return true;
            }
        }
        return false;
    }

    public static boolean isSoulBook(ItemStack book)
    {
        if (getSoulBoundID() >= 0 && book.hasTagCompound())
        {
            NBTTagCompound bookData = book.stackTagCompound;
            if (bookData.hasKey("StoredEnchantments"))
            {
                NBTTagList bookEnch = bookData.getTagList("StoredEnchantments", NBT.TAG_COMPOUND);
                if (!bookEnch.getCompoundTagAt(1).getBoolean("id")) // only pure soulbook allowed
                {
                    int id = bookEnch.getCompoundTagAt(0).getInteger("id");
                    if (id == getSoulBoundID())
                        return true;
                }
            }
        }
        return false;
    }

}