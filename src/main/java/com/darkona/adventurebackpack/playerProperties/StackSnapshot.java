package com.darkona.adventurebackpack.playerProperties;

import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

final class StackSnapshot
{
    private boolean taken = false;
    private ItemStack stack;
    private Item item;
    private int damage, size;
    private Object[] refs = new Object[64];
    private int[] counts = new int[64];
    private int length;

    boolean matches(ItemStack current)
    {
        if (!taken || current != stack) return false;
        if (current == null) return true;
        if (current.getItem() != item || current.getItemDamage() != damage || current.stackSize != size) return false;
        int end = walk(current.stackTagCompound, 0, false);
        return end == length;
    }

    void take(ItemStack current)
    {
        taken = true;
        stack = current;
        if (current == null)
        {
            item = null;
            for (int i = 0; i < length; i++) refs[i] = null;
            length = 0;
            return;
        }
        item = current.getItem();
        damage = current.getItemDamage();
        size = current.stackSize;
        int old = length;
        length = walk(current.stackTagCompound, 0, true);
        for (int i = length; i < old; i++) refs[i] = null;
    }

    void clear()
    {
        taken = false;
        stack = null;
        item = null;
        for (int i = 0; i < length; i++) refs[i] = null;
        length = 0;
    }

    private int walk(NBTBase node, int at, boolean record)
    {
        if (at < 0) return -1;
        int count = 0;
        if (node instanceof NBTTagCompound) count = ((NBTTagCompound) node).func_150296_c().size();
        else if (node instanceof NBTTagList) count = ((NBTTagList) node).tagCount();
        if (record)
        {
            ensure(at);
            refs[at] = node;
            counts[at] = count;
        } else if (at >= length || refs[at] != node || counts[at] != count)
        {
            return -1;
        }
        at++;
        if (node instanceof NBTTagCompound)
        {
            NBTTagCompound compound = (NBTTagCompound) node;
            @SuppressWarnings("unchecked")
            Set<String> keys = (Set<String>) compound.func_150296_c();
            for (String key : keys)
            {
                at = walk(compound.getTag(key), at, record);
                if (at < 0) return -1;
            }
        } else if (node instanceof NBTTagList)
        {
            NBTTagList list = (NBTTagList) node;
            int type = list.func_150303_d();
            if (type == Constants.NBT.TAG_COMPOUND)
            {
                for (int i = 0; i < count; i++)
                {
                    Object element = list.getCompoundTagAt(i);
                    if (record)
                    {
                        ensure(at);
                        refs[at] = element;
                        counts[at] = 0;
                    } else if (at >= length || refs[at] != element)
                    {
                        return -1;
                    }
                    at++;
                }
            } else if (type == Constants.NBT.TAG_STRING)
            {
                for (int i = 0; i < count; i++)
                {
                    String s = list.getStringTagAt(i);
                    if (record)
                    {
                        ensure(at);
                        refs[at] = s;
                        counts[at] = 0;
                    } else if (at >= length || refs[at] != s)
                    {
                        return -1;
                    }
                    at++;
                }
            }
        }
        return at;
    }

    private void ensure(int at)
    {
        if (at < refs.length) return;
        int n = Math.max(at + 1, refs.length * 2);
        Object[] r = new Object[n];
        int[] c = new int[n];
        System.arraycopy(refs, 0, r, 0, refs.length);
        System.arraycopy(counts, 0, c, 0, counts.length);
        refs = r;
        counts = c;
    }
}
