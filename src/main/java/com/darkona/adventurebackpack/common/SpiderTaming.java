package com.darkona.adventurebackpack.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Which tamed spiders each online player owns: the only source of truth for "is this spider still tamed".
 * <p>
 * Server side and in memory on purpose: a tamed spider is released when its owner dies, logs out or changes
 * dimension, so nothing here has to survive the owner leaving. The owner's entry is cleared on those events; a tamed
 * spider whose id is no longer listed under its owner releases itself on its next check, even if it was in an
 * unloaded chunk at the time (or the server restarted). Keyed by UUID, so no player or entity is kept alive.
 */
public final class SpiderTaming
{
    private static final Map<UUID, Set<UUID>> OWNED = new HashMap<UUID, Set<UUID>>();

    private SpiderTaming() {}

    /** True when a new spider may be tamed: {@code max} 0 turns taming off. */
    public static boolean canTameMore(int owned, int max)
    {
        return max > 0 && owned < max;
    }

    /** True when a roll in [0, 1) tames the spider with the given chance (1 always tames, 0 never). */
    public static boolean rollTames(float roll, double chance)
    {
        return roll < chance;
    }

    public static synchronized int count(UUID owner)
    {
        Set<UUID> spiders = owner == null ? null : OWNED.get(owner);
        return spiders == null ? 0 : spiders.size();
    }

    public static synchronized void add(UUID owner, UUID spider)
    {
        Set<UUID> spiders = OWNED.get(owner);
        if (spiders == null)
        {
            spiders = new HashSet<UUID>(4);
            OWNED.put(owner, spiders);
        }
        spiders.add(spider);
    }

    /** True while the spider is listed under this owner. A spider without an owner is never tamed. */
    public static synchronized boolean isTamed(UUID owner, UUID spider)
    {
        if (owner == null || spider == null) return false;
        Set<UUID> spiders = OWNED.get(owner);
        return spiders != null && spiders.contains(spider);
    }

    /** The spider died or was released. */
    public static synchronized void forget(UUID owner, UUID spider)
    {
        if (owner == null || spider == null) return;
        Set<UUID> spiders = OWNED.get(owner);
        if (spiders == null) return;
        spiders.remove(spider);
        if (spiders.isEmpty()) OWNED.remove(owner);
    }

    /** The owner left (death, logout, login after a restart, dimension change): all of their spiders go free. */
    public static synchronized void releaseAll(UUID owner)
    {
        if (owner != null) OWNED.remove(owner);
    }

    /** For tests. */
    static synchronized void clear()
    {
        OWNED.clear();
    }
}
