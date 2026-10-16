package com.darkona.adventurebackpack.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import com.darkona.adventurebackpack.util.Resources;
import net.minecraft.util.ResourceLocation;

/** Armour texture strings are cached now (audit P12) and stay the ones built per frame before. */
class ArmorTextureTest
{
    @Test
    void sameTexturePathAsBeforeBuiltOnce()
    {
        String before = new ResourceLocation("adventurebackpack", "textures/models/" + "adventureHat").toString() + "_texture.png";
        assertEquals(before, ArmorAB.buildArmorTexture("item.adventurebackpack:adventureHat"));
        assertEquals("adventurebackpack:textures/models/adventureHat_texture.png", before);

        String suit = Resources.modelTextureResourceString("adventureSuit_texture.png");
        assertEquals(new ResourceLocation("adventurebackpack", "textures/models/adventureSuit_texture.png").toString(), suit);
        assertSame(suit, Resources.modelTextureResourceString("adventureSuit_texture.png"), "built once, then the same string");
    }
}
