package com.darkona.adventurebackpack.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SpiderLedger
{
    public static final class Entry
    {
        public final UUID spider;
        public int dim;
        public int chunkX;
        public int chunkZ;

        Entry(UUID spider, int dim, int chunkX, int chunkZ)
        {
            this.spider = spider;
            this.dim = dim;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        boolean moveTo(int dim, int chunkX, int chunkZ)
        {
            if (this.dim == dim && this.chunkX == chunkX && this.chunkZ == chunkZ) return false;
            this.dim = dim;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            return true;
        }
    }

    public interface GhostCheck
    {
        boolean isGhost(Entry entry);
    }

    private static final Entry[] NONE = new Entry[0];

    private final Map<UUID, List<Entry>> owned = new HashMap<UUID, List<Entry>>();

    public int count(UUID owner)
    {
        List<Entry> list = owner == null ? null : owned.get(owner);
        return list == null ? 0 : list.size();
    }

    public boolean canTameMore(UUID owner, int max)
    {
        return SpiderTaming.canTameMore(count(owner), max);
    }

    public boolean add(UUID owner, UUID spider, int dim, int chunkX, int chunkZ)
    {
        return touch(owner, spider, dim, chunkX, chunkZ);
    }

    public boolean touch(UUID owner, UUID spider, int dim, int chunkX, int chunkZ)
    {
        if (owner == null || spider == null) return false;
        List<Entry> list = owned.get(owner);
        if (list == null)
        {
            list = new ArrayList<Entry>(2);
            owned.put(owner, list);
        }
        Entry entry = find(list, spider);
        if (entry != null) return entry.moveTo(dim, chunkX, chunkZ);
        list.add(new Entry(spider, dim, chunkX, chunkZ));
        return true;
    }

    public boolean forget(UUID owner, UUID spider)
    {
        if (owner == null || spider == null) return false;
        List<Entry> list = owned.get(owner);
        if (list == null) return false;
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).spider.equals(spider))
            {
                list.remove(i);
                if (list.isEmpty()) owned.remove(owner);
                return true;
            }
        }
        return false;
    }

    public boolean isListed(UUID owner, UUID spider)
    {
        if (owner == null || spider == null) return false;
        List<Entry> list = owned.get(owner);
        return list != null && find(list, spider) != null;
    }

    public Entry[] entriesOf(UUID owner)
    {
        List<Entry> list = owner == null ? null : owned.get(owner);
        return list == null ? NONE : list.toArray(new Entry[list.size()]);
    }

    public int prune(UUID owner, GhostCheck check)
    {
        int removed = 0;
        for (Entry entry : entriesOf(owner))
        {
            if (check.isGhost(entry) && forget(owner, entry.spider)) removed++;
        }
        return removed;
    }

    public List<String> toLines()
    {
        List<String> lines = new ArrayList<String>();
        for (Map.Entry<UUID, List<Entry>> e : owned.entrySet())
        {
            for (Entry entry : e.getValue())
            {
                lines.add(e.getKey() + ";" + entry.spider + ";" + entry.dim + ";" + entry.chunkX + ";" + entry.chunkZ);
            }
        }
        return lines;
    }

    public static SpiderLedger fromLines(List<String> lines)
    {
        SpiderLedger ledger = new SpiderLedger();
        for (String line : lines)
        {
            String[] parts = line == null ? null : line.split(";");
            if (parts == null || parts.length != 5) continue;
            try
            {
                ledger.add(UUID.fromString(parts[0]), UUID.fromString(parts[1]), Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
            }
            catch (IllegalArgumentException ignored)
            {
            }
        }
        return ledger;
    }

    public Iterable<Map.Entry<UUID, List<Entry>>> owners()
    {
        return owned.entrySet();
    }

    private static Entry find(List<Entry> list, UUID spider)
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).spider.equals(spider)) return list.get(i);
        }
        return null;
    }
}
