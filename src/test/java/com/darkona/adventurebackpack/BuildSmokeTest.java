package com.darkona.adventurebackpack;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class BuildSmokeTest {
    @Test
    void versionIsInjectedByTheBuild() {
        assertFalse(Tags.VERSION.isEmpty());
    }
}
