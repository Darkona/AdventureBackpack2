package com.darkona.adventurebackpack.common;

import net.minecraftforge.fluids.FluidContainerRegistry;

/**
 * Created by Darkona on 11/10/2014.
 */
public class Constants
{

    public static final int inventorySize = 22;
    public static final int bucket = FluidContainerRegistry.BUCKET_VOLUME;
    public static final int basicTankCapacity = bucket * 4;
    public static final int advancedTankCapacity = bucket * 8;
    public static final int heroicTankCapacity = bucket * 12;

    //Inventory Special Slots
    public static final int upperTool = 16;
    public static final int lowerTool = 17;

    public static final int bucketInLeft = 18;
    public static final int bucketOutLeft = 19;
    public static final int bucketInRight = 20;
    public static final int bucketOutRight = 21;

}
