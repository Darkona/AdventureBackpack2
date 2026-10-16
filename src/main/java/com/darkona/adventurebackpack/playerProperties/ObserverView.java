package com.darkona.adventurebackpack.playerProperties;

import com.darkona.adventurebackpack.common.Constants;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Set;

public final class ObserverView
{
    private static final int NBT_COMPOUND = 10;
    private static final String[] JETPACK_KEPT = {"waterTank", "steamTank", "status", "inUse", "boiling", "leaking"};

    private ObserverView() {}

    public static NBTTagCompound wearable(NBTTagCompound itemTag)
    {
        NBTTagCompound view = copyExcept(itemTag, "tag");
        if (!itemTag.hasKey("tag", NBT_COMPOUND)) return view;
        NBTTagCompound tag = itemTag.getCompoundTag("tag");
        NBTTagCompound tagView;
        if (tag.hasKey("backpackData", NBT_COMPOUND))
        {
            NBTTagCompound data = tag.getCompoundTag("backpackData");
            NBTTagCompound dataView = copyExcept(data, "ABPItems", "ABPQuiver");
            dataView.setTag("ABPItems", toolsOnly(data.getTagList("ABPItems", NBT_COMPOUND)));
            tagView = copyExcept(tag, "backpackData");
            tagView.setTag("backpackData", dataView);
        } else if (tag.hasKey("jetpackData", NBT_COMPOUND))
        {
            NBTTagCompound data = tag.getCompoundTag("jetpackData");
            NBTTagCompound dataView = new NBTTagCompound();
            for (String key : JETPACK_KEPT)
            {
                NBTBase value = data.getTag(key);
                if (value != null) dataView.setTag(key, value.copy());
            }
            dataView.setInteger("burnTicks", data.getInteger("burnTicks") > 0 ? 1 : 0);
            tagView = copyExcept(tag, "jetpackData");
            tagView.setTag("jetpackData", dataView);
        } else
        {
            tagView = (NBTTagCompound) tag.copy();
        }
        view.setTag("tag", tagView);
        return view;
    }

    public static NBTTagCompound properties(NBTTagCompound ownerData)
    {
        NBTTagCompound view = copyExcept(ownerData, "wearable");
        if (ownerData.hasKey("wearable", NBT_COMPOUND)) view.setTag("wearable", wearable(ownerData.getCompoundTag("wearable")));
        return view;
    }

    private static NBTTagCompound copyExcept(NBTTagCompound source, String skipped)
    {
        return copyExcept(source, skipped, skipped);
    }

    @SuppressWarnings("unchecked")
    private static NBTTagCompound copyExcept(NBTTagCompound source, String skipped, String alsoSkipped)
    {
        NBTTagCompound copy = new NBTTagCompound();
        for (String key : (Set<String>) source.func_150296_c())
        {
            if (!key.equals(skipped) && !key.equals(alsoSkipped)) copy.setTag(key, source.getTag(key).copy());
        }
        return copy;
    }

    private static NBTTagList toolsOnly(NBTTagList items)
    {
        NBTTagList tools = new NBTTagList();
        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            int slot = item.getByte("Slot");
            if (slot == Constants.upperTool || slot == Constants.lowerTool) tools.appendTag(item.copy());
        }
        return tools;
    }
}
