package com.darkona.adventurebackpack.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GuiTankMathTest {
    @Test
    void emptyTankTopIsTheBottom() {
        assertEquals(107, GuiTank.fluidTop(7, 100, 0, 40));
    }

    @Test
    void fullTankTopIsTheTop() {
        assertEquals(7, GuiTank.fluidTop(7, 100, 4000, 40));
    }

    @Test
    void overfullIsClampedToTheTank() {
        assertEquals(7, GuiTank.fluidTop(7, 100, 99999, 40));
    }

    @Test
    void zeroLiquidPerPixelDoesNotDivideByZero() {
        assertEquals(7, GuiTank.fluidTop(7, 100, 10, 0));
    }
}
