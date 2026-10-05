package com.darkona.adventurebackpack.item;

/**
 * The Crossbow Bolt ({@code adventurebackpack:bolt}): ammunition for the Crossbow Magazine. A magazine of bolts fires
 * bolts that hit harder than a magazine of arrows ({@code crossbow.ammo.boltDamageMultiplier}). It does nothing by
 * itself; which items are ammunition is {@code crossbow.ammo.list}.
 */
public class ItemCrossbowBolt extends ItemAB
{
    public ItemCrossbowBolt()
    {
        super();
        setUnlocalizedName("bolt");
        setMaxStackSize(64);
    }
}
