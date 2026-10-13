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

/**
 * The tamed spiders of each player, kept like wolves: a tamed spider stays tamed when its owner logs out, dies,
 * changes dimension or the server restarts. Only its death or its owner setting it free removes it.
 * <p>
 * The count behind the limit (abilities.spider.maxTamed) lives in {@link SpiderLedgerData}, saved with the world. The
 * spider entity is the truth and the ledger only counts: a spider writes itself back every second, and entries of
 * spiders that are gone (removed by a world edit, for example) are dropped by {@link #verify} on login and when the
 * limit blocks a tame. Server side only.
 */
public final class SpiderTaming
{
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

    /** The spider's heartbeat: lists it again if missing and keeps its chunk up to date. */
    public static void touch(World world, UUID owner, UUID spider, int dim, int chunkX, int chunkZ)
    {
        SpiderLedgerData data = SpiderLedgerData.get(world);
        if (data.ledger().touch(owner, spider, dim, chunkX, chunkZ)) data.markDirty();
    }

    /** The spider died or was set free. Idempotent. */
    public static void forget(World world, UUID owner, UUID spider)
    {
        SpiderLedgerData data = SpiderLedgerData.get(world);
        if (data.ledger().forget(owner, spider)) data.markDirty();
    }

    /**
     * Drops the owner's entries of spiders that no longer exist. For each entry: when its chunk is loaded (or, with
     * abilities.spider.verifyLoadsChunks, can be loaded) and the spider is not there, alive and still theirs, the entry
     * goes. A spider whose dimension or chunk cannot be looked at is kept: it may be alive. Runs on login and when the
     * limit blocks a tame, never per tick.
     */
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
        // getWorld, not worldServerForDimension: an unloaded dimension is left alone instead of loaded
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
            // queue it for unloading again, or it would stay loaded with nobody near
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
