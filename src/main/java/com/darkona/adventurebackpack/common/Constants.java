package com.darkona.adventurebackpack.common;

import net.minecraftforge.fluids.FluidContainerRegistry;

/**
 * Created on 11/10/2014.
 *
 * @author Javier Darkona
 */
public class Constants
{

    public static final int inventorySize = 45;
    public static final int bucket = FluidContainerRegistry.BUCKET_VOLUME;

    // Inventory Special Slots
    private static final int endOfInventory = inventorySize - 7;
    public static final int inventoryMainSize = endOfInventory + 1;
    public static final int upperTool = endOfInventory + 1;
    public static final int lowerTool = upperTool + 1;

    public static final int bucketInLeft = lowerTool + 1;
    public static final int bucketOutLeft = bucketInLeft + 1;
    public static final int bucketInRight = bucketOutLeft + 1;
    public static final int bucketOutRight = bucketInRight + 1;

    // Tanks
    public static final int basicTankCapacity = bucket * 4;
    public static final int heroicTankCapacity = bucket * 12;
    public static final int advancedTankCapacity = bucket * 8;

    //IDEA add ghost slot(s) for properly handle stacks of fluid containers

    // Jetpack
    public static final int jetpackFuel = 2;
    public static final int jetpackSteamTankCapacity = bucket * 12;
    public static final int jetpackWaterTankCapacity = bucket * 6;

    // Copter
    public static final int copterTankCapacity = bucket * 6;

}
