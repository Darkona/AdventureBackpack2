package com.darkona.adventurebackpack.common;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class SpiderLedgerData extends WorldSavedData
{
    public static final String NAME = "adventurebackpack_spiders";

    private static MapStorage cachedStorage;
    private static SpiderLedgerData cached;

    private SpiderLedger ledger = new SpiderLedger();

    public SpiderLedgerData(String name)
    {
        super(name);
    }

    public SpiderLedger ledger()
    {
        return ledger;
    }

    public static SpiderLedgerData get(World world)
    {
        MapStorage storage = world.mapStorage;
        if (storage == cachedStorage && cached != null) return cached;
        SpiderLedgerData data = (SpiderLedgerData) storage.loadData(SpiderLedgerData.class, NAME);
        if (data == null)
        {
            data = new SpiderLedgerData(NAME);
            storage.setData(NAME, data);
        }
        cachedStorage = storage;
        cached = data;
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        ledger = new SpiderLedger();
        NBTTagList list = tag.getTagList("Spiders", 10);
        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound e = list.getCompoundTagAt(i);
            ledger.add(new UUID(e.getLong("OwnerMost"), e.getLong("OwnerLeast")),
                    new UUID(e.getLong("SpiderMost"), e.getLong("SpiderLeast")),
                    e.getInteger("Dim"), e.getInteger("ChunkX"), e.getInteger("ChunkZ"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID, List<SpiderLedger.Entry>> owner : ledger.owners())
        {
            for (SpiderLedger.Entry entry : owner.getValue())
            {
                NBTTagCompound e = new NBTTagCompound();
                e.setLong("OwnerMost", owner.getKey().getMostSignificantBits());
                e.setLong("OwnerLeast", owner.getKey().getLeastSignificantBits());
                e.setLong("SpiderMost", entry.spider.getMostSignificantBits());
                e.setLong("SpiderLeast", entry.spider.getLeastSignificantBits());
                e.setInteger("Dim", entry.dim);
                e.setInteger("ChunkX", entry.chunkX);
                e.setInteger("ChunkZ", entry.chunkZ);
                list.appendTag(e);
            }
        }
        tag.setTag("Spiders", list);
    }
}
