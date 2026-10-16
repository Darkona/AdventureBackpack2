package com.darkona.adventurebackpack.common;

import java.util.List;
import java.util.UUID;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

public final class SpiderTaming
{
    private SpiderTaming() {}

    public static boolean canTameMore(int owned, int max)
    {
        return max > 0 && owned < max;
    }

    public static boolean rollTames(float roll, double chance)
    {
        return roll < chance;
    }

    public static int count(World world, UUID owner)
    {
        return SpiderLedgerData.get(world).ledger().count(owner);
    }

    public static boolean canTameMore(World world, UUID owner, int max)
    {
        return SpiderLedgerData.get(world).ledger().canTameMore(owner, max);
    }

    public static void add(World world, UUID owner, UUID spider, int dim, int chunkX, int chunkZ)
    {
        SpiderLedgerData data = SpiderLedgerData.get(world);
        if (data.ledger().add(owner, spider, dim, chunkX, chunkZ)) data.markDirty();
    }

    public static void touch(World world, UUID owner, UUID spider, int dim, int chunkX, int chunkZ)
    {
        SpiderLedgerData data = SpiderLedgerData.get(world);
        if (data.ledger().touch(owner, spider, dim, chunkX, chunkZ)) data.markDirty();
    }

    public static void forget(World world, UUID owner, UUID spider)
    {
        SpiderLedgerData data = SpiderLedgerData.get(world);
        if (data.ledger().forget(owner, spider)) data.markDirty();
    }

    public static int verify(World world, final UUID owner)
    {
        if (owner == null) return 0;
        SpiderLedgerData data = SpiderLedgerData.get(world);
        int removed = data.ledger().prune(owner, new SpiderLedger.GhostCheck()
        {
            @Override
            public boolean isGhost(SpiderLedger.Entry entry)
            {
                return isGhostEntry(owner, entry);
            }
        });
        if (removed > 0) data.markDirty();
        return removed;
    }

    private static boolean isGhostEntry(UUID owner, SpiderLedger.Entry entry)
    {
        WorldServer world = DimensionManager.getWorld(entry.dim);
        if (world == null) return false;
        IChunkProvider provider = world.getChunkProvider();
        boolean loadedHere = false;
        if (!provider.chunkExists(entry.chunkX, entry.chunkZ))
        {
            if (!ConfigHandler.SPIDER_VERIFY_LOADS_CHUNKS || !(provider instanceof ChunkProviderServer)) return false;
            ((ChunkProviderServer) provider).loadChunk(entry.chunkX, entry.chunkZ);
            if (!provider.chunkExists(entry.chunkX, entry.chunkZ)) return false;
            loadedHere = true;
        }
        boolean ghost = !chunkHoldsSpider(world.getChunkFromChunkCoords(entry.chunkX, entry.chunkZ), owner, entry.spider);
        if (loadedHere)
        {
            ((ChunkProviderServer) provider).unloadChunksIfNotNearSpawn(entry.chunkX, entry.chunkZ);
        }
        return ghost;
    }

    private static boolean chunkHoldsSpider(Chunk chunk, UUID owner, UUID spider)
    {
        for (List<?> section : chunk.entityLists)
        {
            for (Object o : section)
            {
                Entity entity = (Entity) o;
                if (entity instanceof EntityFriendlySpider && !entity.isDead && spider.equals(entity.getUniqueID()))
                {
                    return owner.equals(((EntityFriendlySpider) entity).getOwnerId());
                }
            }
        }
        return false;
    }
}
