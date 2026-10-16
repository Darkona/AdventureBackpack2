package com.darkona.adventurebackpack.common;

import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.WeakHashMap;

public final class RenderInfoCache
{
    private static final Map<ItemStack, RenderInfo> CACHE = new WeakHashMap<ItemStack, RenderInfo>();
    private static final RenderInfo EMPTY = new RenderInfo();

    private RenderInfoCache() {}

    public static RenderInfo get(ItemStack stack)
    {
        if (stack == null) return EMPTY;
        int kind = RenderInfo.kindOf(stack);
        RenderInfo info = CACHE.get(stack);
        if (info == null)
        {
            info = new RenderInfo();
            CACHE.put(stack, info);
        } else if (info.matches(stack.stackTagCompound, kind))
        {
            return info;
        }
        return info.read(stack.stackTagCompound, kind);
    }
}
