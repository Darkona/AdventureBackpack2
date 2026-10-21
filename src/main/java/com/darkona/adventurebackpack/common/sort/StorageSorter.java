package com.darkona.adventurebackpack.common.sort;

import java.util.Arrays;
import java.util.Comparator;

public final class StorageSorter
{
    public static final int NO_CATEGORY = Integer.MAX_VALUE;

    private static final Comparator<Entry> ORDER = new Comparator<Entry>()
    {
        @Override
        public int compare(Entry a, Entry b)
        {
            int c = Integer.compare(a.category, b.category);
            if (c != 0) return c;
            c = String.CASE_INSENSITIVE_ORDER.compare(a.name, b.name);
            if (c != 0) return c;
            c = Integer.compare(a.id, b.id);
            if (c != 0) return c;
            c = Integer.compare(a.damage, b.damage);
            return c != 0 ? c : Integer.compare(a.first, b.first);
        }
    };

    private StorageSorter()
    {
    }

    public static final class Entry
    {
        public final int group;
        public final int category;
        public final String name;
        public final int id;
        public final int damage;
        public final int count;
        public final int max;
        public final Object source;
        int first;
        int total;

        public Entry(int group, int category, String name, int id, int damage, int count, int max, Object source)
        {
            this.group = group;
            this.category = category;
            this.name = name;
            this.id = id;
            this.damage = damage;
            this.count = count;
            this.max = Math.max(1, max);
            this.source = source;
        }
    }

    public static final class Plan
    {
        public final Object[] source;
        public final int[] count;
        public final int used;

        Plan(Object[] source, int[] count, int used)
        {
            this.source = source;
            this.count = count;
            this.used = used;
        }
    }

    public static Plan arrange(Entry[] entries, int slots)
    {
        Entry[] heads = new Entry[entries.length];
        int n = 0;
        for (int i = 0; i < entries.length; i++)
        {
            Entry e = entries[i];
            Entry head = null;
            for (int h = 0; h < n; h++)
            {
                if (heads[h].group == e.group)
                {
                    head = heads[h];
                    break;
                }
            }
            if (head == null)
            {
                e.first = i;
                e.total = e.count;
                heads[n++] = e;
            } else
            {
                head.total += e.count;
            }
        }
        Arrays.sort(heads, 0, n, ORDER);
        Object[] source = new Object[slots];
        int[] count = new int[slots];
        int used = 0;
        for (int h = 0; h < n; h++)
        {
            Entry e = heads[h];
            int full = e.total / e.max;
            int rest = e.total % e.max;
            if (used + full + (rest > 0 ? 1 : 0) > slots) return null;
            for (int k = 0; k < full; k++)
            {
                source[used] = e.source;
                count[used++] = e.max;
            }
            if (rest > 0)
            {
                source[used] = e.source;
                count[used++] = rest;
            }
        }
        return new Plan(source, count, used);
    }
}
