package com.darkona.adventurebackpack.entity.ai.climb;

import java.util.HashMap;
import java.util.Map;

/** A test world: flat ground at y 63 everywhere, plus the blocks a test sets. */
final class GridQuery implements BlockQuery
{
    static final char AIR = '.', SOLID = '#', LAVA = 'L', WATER = '~';

    private final Map<Long, Character> blocks = new HashMap<Long, Character>();
    private int unloadedFromX = Integer.MAX_VALUE;

    GridQuery set(int x, int y, int z, char block)
    {
        blocks.put(ClimbPlan.key(x, z, y), block);
        return this;
    }

    /** Solid from y0 to y1 (inclusive) in every column of the box. */
    GridQuery box(int x0, int z0, int x1, int z1, int y0, int y1, char block)
    {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++)
                for (int y = y0; y <= y1; y++)
                    set(x, y, z, block);
        return this;
    }

    /** Columns with x at or above this are in unloaded chunks. */
    GridQuery unloadedFrom(int x)
    {
        unloadedFromX = x;
        return this;
    }

    private char at(int x, int y, int z)
    {
        Character c = blocks.get(ClimbPlan.key(x, z, y));
        if (c != null) return c;
        return y <= 63 ? SOLID : AIR;
    }

    @Override
    public boolean solid(int x, int y, int z)
    {
        return at(x, y, z) == SOLID;
    }

    @Override
    public boolean passable(int x, int y, int z)
    {
        return !solid(x, y, z);
    }

    @Override
    public boolean hazard(int x, int y, int z)
    {
        return at(x, y, z) == LAVA;
    }

    @Override
    public boolean liquid(int x, int y, int z)
    {
        char c = at(x, y, z);
        return c == LAVA || c == WATER;
    }

    @Override
    public boolean loaded(int x, int z)
    {
        return x < unloadedFromX;
    }
}
