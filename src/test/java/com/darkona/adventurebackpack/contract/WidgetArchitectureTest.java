package com.darkona.adventurebackpack.contract;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The widget core knows only generic widgets and their registry: no backpack type and no concrete widget (storage,
 * tools, tanks, quiver, rope, crafting, buttons) may be named in it. A new backpack or accessory is a new widget type
 * and arrangement, never a change in these files.
 */
class WidgetArchitectureTest
{
    private static final Path MAIN = Paths.get("src/main/java/com/darkona/adventurebackpack");

    static final String[] CORE = {
            "common/layout/WidgetLayout.java",
            "inventory/widget/WidgetType.java",
            "inventory/widget/WidgetTypes.java",
            "compat/invtweaks/BackpackSections.java",
            "inventory/widget/WidgetInventory.java",
            "inventory/widget/WidgetStore.java",
            "inventory/widget/SlotList.java",
            "inventory/BackpackNbt.java",
            "inventory/HiddenTank.java",
            "inventory/InventoryBackpack.java",
            "block/TileAdventureBackpack.java",
            "common/layout/StorageIndex.java",
            "common/layout/BackpackSpec.java",
            "inventory/ContainerBackpack.java",
            "client/gui/GuiAdvBackpack.java",
            "client/gui/widget/BackgroundComposer.java",
            "client/gui/widget/WidgetSkin.java",
            "client/gui/widget/WidgetSkins.java",
            "client/gui/widget/FrameSkin.java",
            "client/gui/widget/WindowSkin.java",
            "client/gui/widget/BackpackButton.java",
            "network/LayoutSyncPacket.java",
            "inventory/widget/ExternalWidgets.java",
            "inventory/widget/ExternalWidgetType.java",
            "inventory/widget/SlotWidget.java",
            "inventory/InventoryActions.java",
    };

    static final String[] FORBIDDEN = {
            ".classic.", "ClassicWidgets", "ClassicArrangement", "BackpackLayout", "Skeleton", "IronGolem",
            "SlotTool", "SlotFluid", "SlotQuiver", "SlotRope", "SlotBackpack", "SlotCrafting", "Quiver.", "ItemRope", "RopeRules",
    };

    @Test
    void theWidgetCoreNamesNoConcreteWidgetOrBackpack() throws IOException
    {
        List<String> found = new ArrayList<String>();
        for (String file : CORE)
        {
            String code = SourceScan.strip(new String(Files.readAllBytes(MAIN.resolve(file)), StandardCharsets.UTF_8));
            for (String name : FORBIDDEN)
            {
                if (code.contains(name)) found.add(file + " names " + name);
            }
        }
        assertTrue(found.isEmpty(), found.toString());
    }
}
