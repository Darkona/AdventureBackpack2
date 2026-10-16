package com.darkona.adventurebackpack.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BackpackMaterial extends Material
{
    public BackpackMaterial()
    {
        super(MapColor.brownColor);
    }

    @Override
    public boolean isLiquid()
    {
        return false;
    }

    @Override
    public boolean isSolid()
    {
        return true;
    }

    @Override
    public boolean getCanBlockGrass()
    {
        return false;
    }

    @Override
    public boolean blocksMovement()
    {
        return super.blocksMovement();
    }

    @Override
    protected Material setRequiresTool()
    {
        return super.setRequiresTool();
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
    public Material setReplaceable()
    {
        return super.setReplaceable();
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
    protected Material setNoPushMobility()
    {
        return super.setNoPushMobility();
    }

    @Override
    protected Material setAdventureModeExempt()
    {
        return super.setAdventureModeExempt();
    }

    @Override
    public MapColor getMaterialMapColor()
    {
        return super.getMaterialMapColor();
    }

    @Override
    public boolean isAdventureModeExempt()
    {
        return true;
    }

    @Override
    protected Material setImmovableMobility()
    {
        return super.setImmovableMobility();
    }

    @Override
    public int getMaterialMobility()
    {
        return super.getMaterialMobility();
    }

    @Override
    public boolean isOpaque()
    {
        return super.isOpaque();
    }
}
