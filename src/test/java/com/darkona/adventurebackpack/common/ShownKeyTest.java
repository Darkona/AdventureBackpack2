package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** ShownKey says "changed" on the first call and when any part of the key is another object or number. */
class ShownKeyTest
{
    @Test
    void changesByIdentityAndNumber()
    {
        ShownKey key = new ShownKey();
        Object stack = new Object();
        String lang = "en_US";
        assertTrue(key.changed(stack, 3, lang), "first call");
        assertFalse(key.changed(stack, 3, lang));
        assertTrue(key.changed(stack, 4, lang), "the version moved");
        assertFalse(key.changed(stack, 4, lang));
        assertTrue(key.changed(new Object(), 4, lang), "another stack");
        Object other = new Object();
        key.changed(other, 4, lang);
        assertTrue(key.changed(other, 4, new String("en_US")), "another language object");
        assertTrue(key.changed(null, 0, null));
        assertFalse(key.changed(null, 0, null), "null is a key too");
        key.reset();
        assertTrue(key.changed(null, 0, null), "after reset");
    }
}
