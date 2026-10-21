package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import adventurebackpack.api.BackpackShape;
import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.client.gui.widget.BackgroundComposer;
import com.darkona.adventurebackpack.client.gui.widget.classic.ClassicSkins;
import com.darkona.adventurebackpack.common.AbilityRegistry;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.CRC32;

/**
 * Safety net of the widget refactor: for every shape (rows 4-10, columns 8-12, every quiver size, a few saved storage
 * sizes and the real Standard, Skeleton and bigger example), the container slot table, the inventory indices, the
 * window numbers and the background copy list must stay what they were. Regenerate only on purpose with
 * {@code AB_GOLDEN_WRITE=1}.
 */
class LayoutGoldenTest
{
    private static final File GOLDEN = new File("src/test/resources/layout-golden.txt");
    private static final String PROBE = "LayoutGolden";
    private static BackpackShape asked = new BackpackShape(6, 8);

    private static void registerProbe()
    {
        if (AbilityRegistry.INSTANCE.shapedColors().contains(PROBE)) return;
        AbilityRegistry.INSTANCE.register(PROBE, new IBackpackAbility()
        {
            @Override
            public BackpackShape getShape(BackpackShape configured)
            {
                return asked;
            }
        });
    }

    private static String crc(List<String> lines)
    {
        CRC32 c = new CRC32();
        for (String s : lines) c.update((s + "\n").getBytes(StandardCharsets.UTF_8));
        return Long.toHexString(c.getValue());
    }

    private static String crc(int[] values, int stride, int take)
    {
        CRC32 c = new CRC32();
        for (int i = 0; i < values.length; i += stride)
        {
            for (int j = 0; j < take; j++)
            {
                int v = values[i + j];
                c.update(v >>> 24);
                c.update(v >>> 16);
                c.update(v >>> 8);
                c.update(v);
            }
        }
        return Long.toHexString(c.getValue());
    }

    private static String describe(String key, String color, int storage) throws Exception
    {
        ContainerBackpack c = ContainerBackpackSlotsTest.build(color, new HashMap<Integer, net.minecraft.item.ItemStack>(), storage);
        BackpackLayout l = BackpackLayout.view(c);
        List<String> slots = ContainerBackpackSlotsTest.describe(c);
        ClassicSkins.install();
        int[] bg = BackgroundComposer.compose(l.widgets());
        return key
                + " | slots=" + slots.size() + " " + crc(slots)
                + " | storage=" + l.storage() + " inv=" + l.inventorySize()
                + " quiver=" + (l.quiver() > 0 ? BackpackLayout.quiverIndex(l.storage(), 0) + "+" + l.quiver() : "-")
                + " rope=" + BackpackLayout.ropeIndex(l.storage(), l.quiver())
                + " | win=" + l.width() + "," + l.height() + "," + l.upperHeight() + "," + l.lowerX() + "," + l.lowerWidth()
                + " tanks=" + l.tanks() + "," + l.tankX(1) + "," + l.tankHeight()
                + " btn=" + l.buttonX() + "," + l.buttonY() + " tog=" + l.toggleX() + "," + l.toggleY()
                + " | bg=" + bg.length / BackgroundComposer.STRIDE + " " + crc(bg, BackgroundComposer.STRIDE, 6);
    }

    private static String shape(int rows, int columns, int quiver, int storage) throws Exception
    {
        asked = new BackpackShape(rows, columns, quiver);
        WidgetLayouts.invalidate();
        return describe(rows + "x" + columns + " q" + quiver + " s" + storage, PROBE, storage);
    }

    static List<String> current() throws Exception
    {
        registerProbe();
        BackpackLayoutTest.registerBigExample();
        List<String> out = new ArrayList<String>();
        for (int r = BackpackLayout.MIN_ROWS; r <= BackpackLayout.MAX_ROWS; r++)
        {
            for (int c = BackpackLayout.MIN_COLUMNS; c <= BackpackLayout.MAX_COLUMNS; c++)
            {
                for (int q = 0; q <= BackpackLayout.maxQuiver(r); q++) out.add(shape(r, c, q, 0));
            }
        }
        int[][] stored = {{6, 8, 0, 39}, {6, 8, 0, 47}, {6, 8, 4, 47}, {4, 8, 0, 60}, {4, 12, 3, 80}, {10, 12, 0, 111}, {8, 10, 5, 100}};
        for (int[] s : stored) out.add(shape(s[0], s[1], s[2], s[3]));
        WidgetLayouts.invalidate();
        for (String name : new String[]{"Standard", "Skeleton", "LayoutTestBig"}) out.add(describe(name, name, 0));
        for (String name : com.darkona.adventurebackpack.reference.BackpackNames.backpackNames) out.add(describe("type " + name, name, 0));
        return out;
    }

    @Test
    void everyShapeKeepsItsSlotsIndicesWindowAndBackground() throws Exception
    {
        List<String> now = current();
        if ("1".equals(System.getenv("AB_GOLDEN_WRITE")))
        {
            GOLDEN.getParentFile().mkdirs();
            Files.write(GOLDEN.toPath(), now, StandardCharsets.UTF_8);
        }
        List<String> golden = readGolden();
        assertEquals(golden.size(), now.size(), "number of shapes");
        for (int i = 0; i < golden.size(); i++) assertEquals(golden.get(i), now.get(i));
    }

    private static List<String> readGolden() throws IOException
    {
        return Files.readAllLines(GOLDEN.toPath(), StandardCharsets.UTF_8);
    }
}
