package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.config.ConfigHandler;
import org.junit.jupiter.api.Test;

class AirCompressorRulesTest
{
    // defaults of the scuba.compressor keys
    private static final int CAPACITY = 16000;
    private static final int STROKE = 100;
    private static final int PER_FUEL_TICK = 10;
    private static final int FILL_PER_TICK = 50;
    private static final int INTERVAL = 10;

    @Test
    void defaultsInTheConfigFieldsMatchTheseNumbers()
    {
        assertEquals(CAPACITY, ConfigHandler.COMPRESSOR_TANK_CAPACITY);
        assertEquals(STROKE, ConfigHandler.COMPRESSOR_HAND_PUMP_AMOUNT);
        assertEquals(PER_FUEL_TICK, ConfigHandler.COMPRESSOR_AIR_PER_FUEL_TICK);
        assertEquals(FILL_PER_TICK, ConfigHandler.COMPRESSOR_FILL_PER_TICK);
        assertEquals(INTERVAL, ConfigHandler.COMPRESSOR_INTERVAL_TICKS);
        assertTrue(ConfigHandler.COMPRESSOR_NEEDS_OPEN_AIR);
    }

    @Test
    void handPumpAddsOneStrokeUpToTheCapacity()
    {
        assertEquals(100, AirCompressorRules.pumpAmount(STROKE, 0, CAPACITY));
        assertEquals(40, AirCompressorRules.pumpAmount(STROKE, CAPACITY - 40, CAPACITY));
        assertEquals(0, AirCompressorRules.pumpAmount(STROKE, CAPACITY, CAPACITY));
        assertEquals(0, AirCompressorRules.pumpAmount(0, 0, CAPACITY), "0 turns the pump off");
        assertEquals(0, AirCompressorRules.pumpAmount(STROKE, CAPACITY + 5, CAPACITY), "never negative");
    }

    @Test
    void sixtyStrokesFillOneGear()
    {
        assertEquals(60, 2 * ConfigHandler.SCUBA_TANK_CAPACITY / STROKE);
    }

    @Test
    void pumpWaitsForItsCooldown()
    {
        assertTrue(AirCompressorRules.pumpReady(100, 96, 4));
        assertFalse(AirCompressorRules.pumpReady(100, 97, 4));
        assertTrue(AirCompressorRules.pumpReady(5, 1000, 4), "the clock went backwards: a new world");
        assertTrue(AirCompressorRules.pumpReady(100, 100, 0));
    }

    @Test
    void oneCoalFillsTheWholeTank()
    {
        int coal = 1600;
        int stored = 0;
        while (coal > 0)
        {
            int used = AirCompressorRules.burnTicksUsed(coal, INTERVAL);
            stored += AirCompressorRules.furnaceAir(used, PER_FUEL_TICK, true, stored, CAPACITY);
            coal -= used;
        }
        assertEquals(CAPACITY, stored);
    }

    @Test
    void furnaceNeedsOpenAirAndRoom()
    {
        assertEquals(100, AirCompressorRules.furnaceAir(10, PER_FUEL_TICK, true, 0, CAPACITY));
        assertEquals(0, AirCompressorRules.furnaceAir(10, PER_FUEL_TICK, false, 0, CAPACITY), "buried or under water");
        assertEquals(30, AirCompressorRules.furnaceAir(10, PER_FUEL_TICK, true, CAPACITY - 30, CAPACITY));
        assertEquals(0, AirCompressorRules.furnaceAir(10, 0, true, 0, CAPACITY));
        assertEquals(3, AirCompressorRules.burnTicksUsed(3, INTERVAL), "never burns more than it has");
        assertEquals(0, AirCompressorRules.burnTicksUsed(0, INTERVAL));
    }

    @Test
    void lightsNewFuelOnlyWhenItCanMakeAir()
    {
        assertTrue(AirCompressorRules.shouldLightFuel(0, PER_FUEL_TICK, true, 0, CAPACITY));
        assertFalse(AirCompressorRules.shouldLightFuel(5, PER_FUEL_TICK, true, 0, CAPACITY), "still burning");
        assertFalse(AirCompressorRules.shouldLightFuel(0, PER_FUEL_TICK, false, 0, CAPACITY), "no open air");
        assertFalse(AirCompressorRules.shouldLightFuel(0, PER_FUEL_TICK, true, CAPACITY, CAPACITY), "full tank keeps its coal");
        assertFalse(AirCompressorRules.shouldLightFuel(0, 0, true, 0, CAPACITY), "furnace turned off");
    }

    @Test
    void fillsTheGearWithWhatIsLeftAndWhatFits()
    {
        assertEquals(500, AirCompressorRules.fillAmount(FILL_PER_TICK, INTERVAL, CAPACITY, 6000));
        assertEquals(120, AirCompressorRules.fillAmount(FILL_PER_TICK, INTERVAL, 120, 6000), "only the air there is");
        assertEquals(70, AirCompressorRules.fillAmount(FILL_PER_TICK, INTERVAL, CAPACITY, 70), "only the room the gear has");
        assertEquals(0, AirCompressorRules.fillAmount(FILL_PER_TICK, INTERVAL, 0, 6000));
        assertEquals(0, AirCompressorRules.fillAmount(FILL_PER_TICK, INTERVAL, CAPACITY, 0));
        assertEquals(0, AirCompressorRules.fillAmount(FILL_PER_TICK, INTERVAL, -3, -3));
        // an empty gear fills in six seconds
        int room = 2 * ConfigHandler.SCUBA_TANK_CAPACITY, ticks = 0;
        while (room > 0)
        {
            room -= AirCompressorRules.fillAmount(FILL_PER_TICK, INTERVAL, CAPACITY, room);
            ticks += INTERVAL;
        }
        assertEquals(6, ticks / 20);
    }

    @Test
    void parsesAirItems()
    {
        AirCompressorRules.AirItem cell = AirCompressorRules.parseAirItem(" ic2:airCell = 1000 > ic2:cell ");
        assertNotNull(cell);
        assertEquals("ic2:airCell", cell.full);
        assertEquals(1000, cell.amount);
        assertEquals("ic2:cell", cell.empty);

        AirCompressorRules.AirItem noEmpty = AirCompressorRules.parseAirItem("item:gregtech:gt.metaitem.01:30000=2000");
        assertNotNull(noEmpty);
        assertEquals("item:gregtech:gt.metaitem.01:30000", noEmpty.full);
        assertNull(noEmpty.empty);

        assertNull(AirCompressorRules.parseAirItem(null));
        assertNull(AirCompressorRules.parseAirItem(""));
        assertNull(AirCompressorRules.parseAirItem("ic2:airCell"));
        assertNull(AirCompressorRules.parseAirItem("ic2:airCell="));
        assertNull(AirCompressorRules.parseAirItem("=1000"));
        assertNull(AirCompressorRules.parseAirItem("ic2:airCell=lots"));
        assertNull(AirCompressorRules.parseAirItem("ic2:airCell=0"));
        assertNull(AirCompressorRules.parseAirItem("ic2:airCell=1000>"));
    }

    @Test
    void breathableNamesIgnoreCase()
    {
        String[] names = {"air", " oxygen "};
        assertTrue(AirCompressorRules.isBreathable("air", names));
        assertTrue(AirCompressorRules.isBreathable("Oxygen", names));
        assertFalse(AirCompressorRules.isBreathable("water", names));
        assertFalse(AirCompressorRules.isBreathable(null, names));
        assertFalse(AirCompressorRules.isBreathable("air", null));
    }
}
