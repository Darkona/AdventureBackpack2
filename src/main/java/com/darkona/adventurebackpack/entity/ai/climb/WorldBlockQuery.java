package com.darkona.adventurebackpack.entity.ai.climb;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/** {@link BlockQuery} over a live world, by block material: no allocation per question. One per spider. */
public class WorldBlockQuery implements BlockQuery
{
    private final World world;

    public WorldBlockQuery(World world)
    {
        this.world = world;
    }

    @Override
    public boolean solid(int x, int y, int z)
    {
        return world.getBlock(x, y, z).getMaterial().blocksMovement();
    }

    @Override
    public boolean passable(int x, int y, int z)
    {
        return !solid(x, y, z);
    }

    @Override
    public boolean hazard(int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        Material material = block.getMaterial();
        return material == Material.lava || material == Material.fire || block == Blocks.cactus;
    }

    @Override
    public boolean liquid(int x, int y, int z)
    {
        return world.getBlock(x, y, z).getMaterial().isLiquid();
    }

    @Override
    public boolean loaded(int x, int z)
    {
        return world.blockExists(x, 64, z);
    }
}
