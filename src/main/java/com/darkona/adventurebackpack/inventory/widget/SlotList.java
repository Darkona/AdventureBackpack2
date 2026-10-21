package com.darkona.adventurebackpack.inventory.widget;

public final class SlotList
{
    public final String key;
    public final String countKey;
    public final boolean slotTags;
    public final boolean dropWhenEmpty;

    public SlotList(String key, String countKey, boolean slotTags, boolean dropWhenEmpty)
    {
        this.key = key;
        this.countKey = countKey;
        this.slotTags = slotTags;
        this.dropWhenEmpty = dropWhenEmpty;
    }

    public static SlotList of(String key)
    {
        return new SlotList(key, null, true, false);
    }
}
