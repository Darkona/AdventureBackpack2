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
        super(MapColor.clothColor);
    }

    /**
     * Returns if blocks of these materials are liquids.
     */
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

    /**
     * Will prevent grass from growing on dirt underneath and kill any grass below it if it returns true
     */
    @Override
    public boolean getCanBlockGrass()
    {
        return super.getCanBlockGrass();
    }

    /**
     * Returns if this material is considered solid or not
     */
    @Override
    public boolean blocksMovement()
    {
        return super.blocksMovement();
    }

    /**
     * Makes blocks with this material require the correct tool to be harvested.
     */
    @Override
    protected Material setRequiresTool()
    {
        return super.setRequiresTool();
    }

    /**
     * Set the canBurn bool to True and return the current object.
     */
    @Override
    protected Material setBurning()
    {
        return super.setBurning();
    }

    /**
     * Returns if the block can burn or not.
     */
    @Override
    public boolean getCanBurn()
    {
        return false;
    }

    /**
     * Sets {@link #replaceable} to true.
     */
    @Override
    public Material setReplaceable()
    {
        return super.setReplaceable();
    }

    /**
     * Returns whether the material can be replaced by other blocks when placed - eg snow, vines and tall grass.
     */
    @Override
    public boolean isReplaceable()
    {
        return false;
    }
    /**
     * Returns true if the material can be harvested without a tool (or with the wrong tool)
     */
    @Override
    public boolean isToolNotRequired()
    {
        return true;
    }

    /**
     * This type of material can't be pushed, but pistons can move over it.
     */
    @Override
    protected Material setNoPushMobility()
    {
        return super.setNoPushMobility();
    }

    /**
     * @see #isAdventureModeExempt()
     */
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

    /**
     * Returns true if blocks with this material can always be mined in adventure mode.
     */
    @Override
    public boolean isAdventureModeExempt()
    {
        return true;
    }

    /**
     * This type of material can't be pushed, and pistons are blocked to move.
     */
    @Override
    protected Material setImmovableMobility()
    {
        return super.setImmovableMobility();
    }

    /**
     * Returns the mobility information of the material, 0 = free, 1 = can't push but can move over, 2 = total
     * immobility and stop pistons.
     */
    @Override
    public int getMaterialMobility()
    {
        return super.getMaterialMobility();
    }

    /**
     * Indicate if the material is opaque
     */
    @Override
    public boolean isOpaque()
    {
        return super.isOpaque();
    }
}
