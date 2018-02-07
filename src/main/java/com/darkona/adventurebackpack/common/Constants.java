package com.darkona.adventurebackpack.common;

import net.minecraftforge.fluids.FluidContainerRegistry;

/**
 * Created on 11/10/2014.
 *
 * @author Javier Darkona
 */
public class Constants
{
    public enum Source //TODO move to separate class?
    {
        TILE, HOLDING, WEARING
    }

    // General
    public static final int BUCKET = FluidContainerRegistry.BUCKET_VOLUME;

    // Wearable NBT
    public static final String TAG_WEARABLE_COMPOUND = "wearableData"; //TODO "wearablePack"
    public static final String TAG_INVENTORY = "inventory";

    /**
     *  ### Adventure Backpack ###
     */
    public static final int BASIC_TANK_CAPACITY = BUCKET * 4;
    //public static final int ADVANCED_TANK_CAPACITY = BUCKET * 8; // upgrade system?
    //public static final int HEROIC_TANK_CAPACITY = BUCKET * 12;

    // Inventory Slots
    public static final int INVENTORY_SIZE = 54;
    public static final int INVENTORY_SPECIAL_SIZE = 6; // tooSlot * 2, bucketIn * 2, bucketOut *2
    public static final int INVENTORY_MAIN_SIZE = INVENTORY_SIZE - INVENTORY_SPECIAL_SIZE; // 0 excluded
    public static final int END_OF_INVENTORY = INVENTORY_MAIN_SIZE - 1;                    // 0 included

    public static final int TOOL_UPPER = END_OF_INVENTORY + 1;
    public static final int TOOL_LOWER = TOOL_UPPER + 1;

    public static final int BUCKET_IN_LEFT = TOOL_LOWER + 1;
    public static final int BUCKET_OUT_LEFT = BUCKET_IN_LEFT + 1;
    public static final int BUCKET_IN_RIGHT = BUCKET_OUT_LEFT + 1;
    public static final int BUCKET_OUT_RIGHT = BUCKET_IN_RIGHT + 1;

    // NBT
    public static final String TAG_TYPE = "type";
    public static final String TAG_LEFT_TANK = "leftTank";
    public static final String TAG_RIGHT_TANK = "rightTank";

    // NBT: Extended Properties
    public static final String TAG_HOLDING_SPACE = "holdingSpace";
    public static final String TAG_SLEEPING_IN_BAG = "sleepingInBag";
    public static final String TAG_STORED_SPAWN = "storedSpawn";

    public class Jetpack
    {
        /**
         *  ### Coal Jetpack ###
         */
        public static final int MAX_TEMPERATURE = 200;
        public static final int WATER_CAPACITY = BUCKET * 6;
        public static final int STEAM_CAPACITY = BUCKET * 12;

        // Inventory Slots
        public static final int INVENTORY_SIZE = 3;
        public static final int BUCKET_IN = 0;
        public static final int BUCKET_OUT = 1;
        public static final int FUEL_SLOT = 2;

        // NBT
        public static final String TAG_WATER_TANK = "waterTank";
        public static final String TAG_STEAM_TANK = "steamTank";
    }

    public class Copter
    {
        /**
         *  ### Copter Pack ###
         */
        public static final int FUEL_CAPACITY = BUCKET * 6;

        // Inventory Slots
        public static final int INVENTORY_SIZE = 2;
        public static final int BUCKET_IN = 0;
        public static final int BUCKET_OUT = 1;

        // NBT
        public static final String TAG_STATUS = "status";
        public static final String TAG_FUEL_TANK = "fuelTank";
    }
}
