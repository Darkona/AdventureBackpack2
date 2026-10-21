package com.darkona.adventurebackpack.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CampfireRespawn
{
    private static final class Memo
    {
        final ChunkCoordinates bed;
        final boolean forced;
        final int dimension;

        Memo(ChunkCoordinates bed, boolean forced, int dimension)
        {
            this.bed = bed;
            this.forced = forced;
            this.dimension = dimension;
        }
    }

    private static final Map<UUID, Memo> PENDING = new HashMap<UUID, Memo>();

    private CampfireRespawn() {}

    public static synchronized void redirect(EntityPlayer player, ChunkCoordinates campfire)
    {
        int dim = player.dimension;
        ChunkCoordinates bed = player.getBedLocation(dim);
        PENDING.put(player.getUniqueID(), new Memo(bed == null ? null : new ChunkCoordinates(bed), player.isSpawnForced(dim), dim));
        player.setSpawnChunk(campfire, false, dim);
    }

    public static void restore(EntityPlayer player)
    {
        Memo memo;
        synchronized (CampfireRespawn.class)
        {
            memo = PENDING.remove(player.getUniqueID());
        }
        if (memo != null)
        {
            player.setSpawnChunk(memo.bed, memo.forced, memo.dimension);
        }
    }
}
