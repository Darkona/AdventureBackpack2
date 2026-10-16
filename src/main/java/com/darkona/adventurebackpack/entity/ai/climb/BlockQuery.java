package com.darkona.adventurebackpack.entity.ai.climb;

public interface BlockQuery
{
    boolean solid(int x, int y, int z);

    boolean passable(int x, int y, int z);

    boolean hazard(int x, int y, int z);

    boolean liquid(int x, int y, int z);

    boolean loaded(int x, int z);
}
