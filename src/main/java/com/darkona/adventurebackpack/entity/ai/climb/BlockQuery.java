package com.darkona.adventurebackpack.entity.ai.climb;

/** What the climb planner asks about the world, one block at a time; a test grid stands in for the world in tests. */
public interface BlockQuery
{
    /** The block stops movement (a wall, the ground). */
    boolean solid(int x, int y, int z);

    /** A body can be in this block: not solid. */
    boolean passable(int x, int y, int z);

    /** Lava, fire, cactus: never walk into or land on it. */
    boolean hazard(int x, int y, int z);

    boolean liquid(int x, int y, int z);

    /** The chunk holding this column is loaded; outside loaded chunks nothing is walkable. */
    boolean loaded(int x, int z);
}
