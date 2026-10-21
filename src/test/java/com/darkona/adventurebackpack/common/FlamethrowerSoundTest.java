package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FlamethrowerSoundTest
{
    @Test
    void lightingFiringAndPuttingOutEachMakeOneEvent()
    {
        FlamethrowerSound.State state = new FlamethrowerSound.State();
        assertEquals(0, state.update(100, false, false), "first sight is silent");
        assertEquals(FlamethrowerSound.IGNITE, state.update(101, true, false));
        assertEquals(0, state.update(102, true, false), "a lit pilot stays quiet");
        assertEquals(FlamethrowerSound.FIRE_START, state.update(103, true, true));
        assertEquals(0, state.update(104, true, true), "one loop per use, not one per tick");
        assertEquals(0, state.update(105, true, false), "releasing only lets the loop stop itself");
        assertEquals(FlamethrowerSound.PUT_OUT, state.update(106, false, false));
    }

    @Test
    void runningDryWhileFiringPutsOutAtOnce()
    {
        FlamethrowerSound.State state = new FlamethrowerSound.State();
        state.update(1, true, true);
        assertEquals(FlamethrowerSound.PUT_OUT, state.update(2, false, false));
    }

    @Test
    void aGapInTheTicksStartsOverWithoutAFalseIgniteOrPutOut()
    {
        FlamethrowerSound.State state = new FlamethrowerSound.State();
        state.update(10, true, false);
        assertEquals(0, state.update(50, false, false), "pack taken off and put back on");
        assertEquals(0, state.update(80, true, false), "a player coming into sight with the pilot lit");
        assertEquals(FlamethrowerSound.FIRE_START, state.update(200, true, true), "a player coming into sight while firing gets the loop");
    }

    @Test
    void theLoopNeedsTheSoundOnAVolumeALivingPlayerAndTheNozzleFiring()
    {
        assertTrue(FlamethrowerSound.loopContinues(true, 1.0F, true, true));
        assertFalse(FlamethrowerSound.loopContinues(false, 1.0F, true, true), "sound off");
        assertFalse(FlamethrowerSound.loopContinues(true, 0.0F, true, true), "volume zero");
        assertFalse(FlamethrowerSound.loopContinues(true, 1.0F, false, true), "dead");
        assertFalse(FlamethrowerSound.loopContinues(true, 1.0F, true, false), "released or pack off");
    }

    @Test
    void firingNeedsTheLitPilotTheNozzleAndTheUse()
    {
        assertTrue(FlamethrowerSound.firing(true, true, true));
        assertFalse(FlamethrowerSound.firing(false, true, true));
        assertFalse(FlamethrowerSound.firing(true, false, true));
        assertFalse(FlamethrowerSound.firing(true, true, false));
    }

    @Test
    void theDryClickIsOnlyForAWornPackWithThePilotOut()
    {
        assertTrue(FlamethrowerSound.dryTrigger(true, true, false));
        assertFalse(FlamethrowerSound.dryTrigger(true, true, true));
        assertFalse(FlamethrowerSound.dryTrigger(true, false, false));
        assertFalse(FlamethrowerSound.dryTrigger(false, true, false));
    }

    @Test
    void theVolumeIsClampedBetweenZeroAndOne()
    {
        assertEquals(0.0F, FlamethrowerSound.master(-1.0), 1e-6);
        assertEquals(0.0F, FlamethrowerSound.master(Double.NaN), 1e-6);
        assertEquals(0.4F, FlamethrowerSound.master(0.4), 1e-6);
        assertEquals(1.0F, FlamethrowerSound.master(3.0), 1e-6);
    }
}
