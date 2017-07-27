package com.darkona.adventurebackpack.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * Created on 24/12/2014
 *
 * @author Darkona
 */
public class BackpackMaterial extends Material
{
    public BackpackMaterial()
    {
        super(MapColor.brownColor);
    }

    @Override
    public boolean getCanBlockGrass()
    {
        return false;
    }

    @Override
    protected Material setBurning()
    {
        return this;
    }

    @Override
    public boolean getCanBurn()
    {
        return false;
    }

    @Override
    public boolean isReplaceable()
    {
        return false;
    }

    @Override
    public boolean isToolNotRequired()
    {
        return true;
    }

    @Override
    public boolean isAdventureModeExempt()
    {
        return true;
    }
}
