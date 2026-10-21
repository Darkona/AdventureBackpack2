package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TintTest
{
    @Test
    void whiteLeavesTheTextureAsItIs()
    {
        assertEquals(1f, Tint.red(Tint.WHITE));
        assertEquals(1f, Tint.green(Tint.WHITE));
        assertEquals(1f, Tint.blue(Tint.WHITE));
        assertEquals(0x123456, Tint.multiply(0x123456, Tint.WHITE));
    }

    @Test
    void channelsUnpackScaleAndMultiply()
    {
        assertEquals(0x33 / 255f, Tint.green(0x113355));
        assertEquals(0xE6E6E6, Tint.scale(Tint.WHITE, 0.9f));
        assertEquals(0xFFFFFF, Tint.scale(0xF0F0F0, 2f));
        assertEquals(0x800000, Tint.multiply(0xFF8000, 0x800000));
        assertEquals(0x402000, Tint.multiply(0xFF8000, 0x404040));
        assertEquals(0x000000, Tint.multiply(0xFFFFFF, 0));
    }
}
