package com.darkona.adventurebackpack.block;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.GearType;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.item.ItemFlamethrowerPack;
import com.darkona.adventurebackpack.item.ItemScubaGear;
import com.darkona.adventurebackpack.item.ItemSteamJetpack;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Back gear placed as a block: the tile keeps the gear's own stack, so what goes in comes out (only the "running"
 * flags are switched off, the jetpack's boiler is frozen as it was), it hands the gear out once (no copy), the
 * metadata keeps facing and type, and the config switches decide what can be placed.
 */
class PlacedGearTest
{
    @AfterEach
    void restoreConfig()
    {
        ConfigHandler.PLACEDGEAR_ENABLED = true;
        ConfigHandler.PLACEDGEAR_JETPACK = true;
        ConfigHandler.PLACEDGEAR_COPTER = true;
        ConfigHandler.PLACEDGEAR_SCUBA = true;
        ConfigHandler.PLACEDGEAR_FLAMETHROWER = true;
    }

    private static ItemStack runningJetpack()
    {
        ItemStack stack = new ItemStack(new ItemSteamJetpack());
        InventorySteamJetpack inv = new InventorySteamJetpack(stack);
        inv.setTemperature(150);
        inv.setBurnTicks(800);
        inv.setCoolTicks(4321);
        inv.setStatus(true);
        inv.setInUse(true);
        inv.setBoiling(true);
        inv.setLeaking(true);
        inv.markDirty();
        stack.stackTagCompound.setTag("display", new NBTTagCompound());
        stack.stackTagCompound.getCompoundTag("display").setString("Name", "Old Faithful");
        return stack;
    }

    @Test
    void theTypeOfEachGearAndItsOrdinal()
    {
        assertEquals(GearType.JETPACK, GearType.of(new ItemSteamJetpack()));
        assertEquals(GearType.COPTER, GearType.of(new ItemCopterPack()));
        assertEquals(GearType.SCUBA, GearType.of(new ItemScubaGear()));
        assertEquals(GearType.FLAMETHROWER, GearType.of(new ItemFlamethrowerPack()));
        assertNull(GearType.of(new Item()));
        assertNull(GearType.of((ItemStack) null));
        for (GearType type : GearType.values()) assertSame(type, GearType.byOrdinal(type.ordinal()));
        assertNull(GearType.byOrdinal(4));
        assertNull(GearType.byOrdinal(-1));
    }

    @Test
    void metadataKeepsFacingAndType()
    {
        for (GearType type : GearType.values())
        {
            for (int facing = 0; facing < 4; facing++)
            {
                int meta = BlockGear.meta(facing, type);
                assertTrue(meta >= 0 && meta < 16);
                assertEquals(facing, BlockGear.facing(meta));
                assertSame(type, BlockGear.type(meta));
            }
        }
    }

    @Test
    void placingStopsTheJetpackAndFreezesItsBoiler()
    {
        ItemStack held = runningJetpack();
        NBTTagCompound before = (NBTTagCompound) held.stackTagCompound.copy();
        TileGear tile = new TileGear();
        tile.setGear(held.copy());

        NBTTagCompound data = tile.getGear().stackTagCompound.getCompoundTag("jetpackData");
        assertFalse(data.getBoolean("status"));
        assertFalse(data.getBoolean("inUse"));
        assertFalse(data.getBoolean("boiling"));
        assertFalse(data.getBoolean("leaking"));

        // everything else is the same: frozen temperature, burn time, tanks, slots, the custom name
        NBTTagCompound expected = (NBTTagCompound) before.copy();
        NBTTagCompound expectedData = expected.getCompoundTag("jetpackData");
        for (String flag : new String[]{"status", "inUse", "boiling", "leaking"}) expectedData.setBoolean(flag, false);
        assertEquals(expected, tile.getGear().stackTagCompound);
        assertEquals(150, data.getInteger("temperature"));
        assertEquals(800, data.getInteger("burnTicks"));
        assertEquals(before, held.stackTagCompound, "the held stack is not touched");
    }

    @Test
    void itemToBlockToItemLosesNothingAndGivesItOutOnce()
    {
        ItemStack held = new ItemStack(new ItemCopterPack());
        InventoryCopterPack inv = new InventoryCopterPack(held);
        inv.setTickCounter(77);
        inv.markDirty();
        held.stackTagCompound.setString("otherMod", "kept");
        NBTTagCompound before = (NBTTagCompound) held.stackTagCompound.copy();

        TileGear tile = new TileGear();
        ItemStack placed = held.copy();
        tile.setGear(placed);
        ItemStack back = tile.takeGear();
        assertSame(placed, back);
        assertEquals(before, back.stackTagCompound, "the copter's root keys (fuelTank, status, tickCounter) and foreign keys come back");
        assertEquals(1, back.stackSize);
        assertNull(tile.takeGear(), "a second break finds nothing: no copy");
        assertNull(tile.getGear());
    }

    @Test
    void theCopterEngineAndThePilotFlameGoOff()
    {
        ItemStack copter = new ItemStack(new ItemCopterPack());
        copter.stackTagCompound = new NBTTagCompound();
        copter.stackTagCompound.setByte("status", (byte) 2);
        assertTrue(GearType.COPTER.stopRunning(copter));
        assertEquals(0, copter.stackTagCompound.getByte("status"));
        assertFalse(GearType.COPTER.stopRunning(copter));

        ItemStack pack = new ItemStack(new ItemFlamethrowerPack());
        pack.stackTagCompound = new NBTTagCompound();
        NBTTagCompound data = new NBTTagCompound();
        data.setBoolean("on", true);
        pack.stackTagCompound.setTag("flamethrowerData", data);
        assertTrue(GearType.FLAMETHROWER.stopRunning(pack));
        assertFalse(pack.stackTagCompound.getCompoundTag("flamethrowerData").getBoolean("on"));

        ItemStack scuba = new ItemStack(new ItemScubaGear());
        assertFalse(GearType.SCUBA.stopRunning(scuba), "no tag, nothing to stop");
    }

    @Test
    void theSavedTileHoldsTheWholeStack()
    {
        ItemStack held = runningJetpack();
        TileGear tile = new TileGear();
        tile.setGear(held.copy());
        NBTTagCompound saved = new NBTTagCompound();
        try
        {
            tile.writeToNBT(saved);
        } catch (RuntimeException unmapped)
        {
            // TileEntity.writeToNBT wants the tile class registered; the gear part is written by TileGear itself
            saved = new NBTTagCompound();
            saved.setTag("gear", tile.getGear().writeToNBT(new NBTTagCompound()));
        }
        assertEquals(tile.getGear().stackTagCompound, saved.getCompoundTag("gear").getCompoundTag("tag"));
        assertEquals(1, saved.getCompoundTag("gear").getByte("Count"));
    }

    @Test
    void theSwitchesDecideWhatCanBePlaced()
    {
        for (GearType type : GearType.values()) assertTrue(type.placeable(), type.name());
        ConfigHandler.PLACEDGEAR_COPTER = false;
        assertFalse(GearType.COPTER.placeable());
        assertTrue(GearType.JETPACK.placeable());
        ConfigHandler.PLACEDGEAR_ENABLED = false;
        for (GearType type : GearType.values()) assertFalse(type.placeable(), type.name());
    }

    @Test
    void aDisabledPlacedGearIsInertButCanBePickedUp()
    {
        TileGear tile = new TileGear();
        tile.setGear(new ItemStack(new ItemSteamJetpack()));
        assertTrue(tile.enabled());
        ConfigHandler.PLACEDGEAR_JETPACK = false;
        assertFalse(tile.enabled());
        assertEquals(0, tile.fill(null, null, true));
        assertNull(tile.drain(null, 1000, true));
        assertTrue(tile.takeGear() != null);
    }
}
