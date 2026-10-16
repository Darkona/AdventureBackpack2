package com.darkona.adventurebackpack.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public static synchronized void remember(EntityPlayer player)
    {
        if (SLEEPERS.containsKey(player.getUniqueID())) return;
        int dim = player.dimension;
        ChunkCoordinates spawn = player.getBedLocation(dim);
        SLEEPERS.put(player.getUniqueID(), new Memo(spawn == null ? null : new ChunkCoordinates(spawn), player.isSpawnForced(dim), dim));
    }

    public static void restoreWhenAwake(EntityPlayer player)
    {
        if (SLEEPERS.isEmpty() || player.isPlayerSleeping()) return;
        restoreNow(player);
    }

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
