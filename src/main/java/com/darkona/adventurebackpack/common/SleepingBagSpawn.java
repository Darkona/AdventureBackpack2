package com.darkona.adventurebackpack.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Keeps a player's spawn point when sleeping bags do not set it (sleepingbag.setsSpawn = false).
 * <p>
 * Vanilla moves the spawn to the bed when the player wakes up, after the wake-up event, so the old spawn is
 * remembered when the player lies down and put back on the first tick the player is awake. Server only, keyed by
 * UUID, forgotten on logout. The tick check is one {@code isEmpty()} while nobody sleeps in a bag.
 */
public final class SleepingBagSpawn
{
    private static final class Memo
    {
        final ChunkCoordinates spawn;
        final boolean forced;
        final int dimension;

        Memo(ChunkCoordinates spawn, boolean forced, int dimension)
        {
            this.spawn = spawn;
            this.forced = forced;
            this.dimension = dimension;
        }
    }

    private static final Map<UUID, Memo> SLEEPERS = new HashMap<UUID, Memo>();

    private SleepingBagSpawn() {}

    /** The player lies down in a bag: remember the spawn they had, unless they already lie in one. */
    public static synchronized void remember(EntityPlayer player)
    {
        if (SLEEPERS.containsKey(player.getUniqueID())) return;
        int dim = player.dimension;
        ChunkCoordinates spawn = player.getBedLocation(dim);
        SLEEPERS.put(player.getUniqueID(), new Memo(spawn == null ? null : new ChunkCoordinates(spawn), player.isSpawnForced(dim), dim));
    }

    /** Player tick (server): once the player is awake again, put the old spawn back. */
    public static void restoreWhenAwake(EntityPlayer player)
    {
        if (SLEEPERS.isEmpty() || player.isPlayerSleeping()) return;
        restoreNow(player);
    }

    /** Logout (even while asleep): put the old spawn back now and forget the player. */
    public static void restoreNow(EntityPlayer player)
    {
        Memo memo;
        synchronized (SleepingBagSpawn.class)
        {
            memo = SLEEPERS.remove(player.getUniqueID());
        }
        if (memo != null)
        {
            player.setSpawnChunk(memo.spawn, memo.forced, memo.dimension);
        }
    }
}
