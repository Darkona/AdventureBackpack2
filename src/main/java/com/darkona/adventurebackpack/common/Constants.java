package com.darkona.adventurebackpack.common;

import net.minecraftforge.fluids.FluidContainerRegistry;

/**
 * Created on 11/10/2014.
 *
 * @author Javier Darkona
 */
public class Constants
{
    public static final int BUCKET = FluidContainerRegistry.BUCKET_VOLUME;

    public static final int INVENTORY_SIZE = 45;
    public static final String COMPOUND_TAG = "backpackData";
    public static final String INVENTORY = "ABPItems";

    // Inventory Special Slots
    public static final int END_OF_INVENTORY = INVENTORY_SIZE - 7; // 0 included
    public static final int INVENTORY_MAIN_SIZE = END_OF_INVENTORY + 1; // 0 excluded

    public static final int UPPER_TOOL = END_OF_INVENTORY + 1;
    public static final int LOWER_TOOL = UPPER_TOOL + 1;

    public static final int BUCKET_IN_LEFT = LOWER_TOOL + 1;
    public static final int BUCKET_OUT_LEFT = BUCKET_IN_LEFT + 1;
    public static final int BUCKET_IN_RIGHT = BUCKET_OUT_LEFT + 1;
    public static final int BUCKET_OUT_RIGHT = BUCKET_IN_RIGHT + 1;

    // Tanks
    public static final String RIGHT_TANK = "rightTank";
    public static final String LEFT_TANK = "leftTank";
    public static final int BASIC_TANK_CAPACITY = BUCKET * 4;
    public static final int ADVANCED_TANK_CAPACITY = BUCKET * 8; // upgrade system?
    public static final int HEROIC_TANK_CAPACITY = BUCKET * 12;

    // Jetpack
    public static final int JETPACK_INVENTORY_SIZE = 3;
    public static final int JETPACK_BUCKET_IN = 0;
    public static final int JETPACK_BUCKET_OUT = 1;
    public static final int JETPACK_FUEL_SLOT = 2;

    public static final String JETPACK_COMPOUND_TAG = "jetpackData";
    public static final String JETPACK_INVENTORY = "inventory";
    public static final String JETPACK_STEAM_TANK = "steamTank";
    public static final String JETPACK_WATER_TANK = "waterTank";
    public static final int JETPACK_STEAM_CAPACITY = BUCKET * 12;
    public static final int JETPACK_WATER_CAPACITY = BUCKET * 6;

    public static final int JETPACK_MAX_TEMPERATURE = 200;

    // Copter
    public static final int COPTER_INVENTORY_SIZE = 2;
    public static final int COPTER_BUCKET_IN = 0;
    public static final int COPTER_BUCKET_OUT = 1;

    public static final String COPTER_FUEL_TANK = "fuelTank";
    public static final int COPTER_FUEL_CAPACITY = BUCKET * 6;


    //
    public static final String[] NIGHTVISION_BACKPACKS = {"Bat", "Squid", "Dragon"};

}
