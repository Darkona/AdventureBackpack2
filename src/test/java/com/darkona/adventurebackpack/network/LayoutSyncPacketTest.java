package com.darkona.adventurebackpack.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.darkona.adventurebackpack.common.layout.BackpackSpec;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.layout.classic.ClassicArrangement;
import com.darkona.adventurebackpack.common.layout.classic.ClassicComposition;
import com.darkona.adventurebackpack.inventory.WidgetLayouts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

class LayoutSyncPacketTest
{
    @Test
    void theSpecsSurviveTheWire()
    {
        Map<String, BackpackSpec> specs = new LinkedHashMap<String, BackpackSpec>();
        specs.put("Standard", ClassicComposition.of(6, 8, 0));
        specs.put("Skeleton", ClassicComposition.of(7, 9, 4));
        ByteBuf buf = Unpooled.buffer();
        new LayoutSyncPacket.Message(specs).toBytes(buf);
        LayoutSyncPacket.Message read = new LayoutSyncPacket.Message();
        read.fromBytes(buf);
        assertEquals(specs, read.specs);
        assertEquals(0, buf.readableBytes());
    }

    @Test
    void aBrokenSpecIsRefused()
    {
        assertThrows(IllegalArgumentException.class, () -> BackpackSpec.decode(new int[]{6, 8, 3, 1}));
    }

    @Test
    void theClientBuildsTheWindowTheServerSent()
    {
        Map<String, BackpackSpec> specs = new LinkedHashMap<String, BackpackSpec>();
        specs.put("Standard", ClassicComposition.of(7, 9, 0));
        try
        {
            WidgetLayouts.useServerSpecs(specs);
            WidgetLayout sent = WidgetLayouts.widgets("Standard", WidgetLayouts.storage("Standard", 0));
            WidgetLayout expected = ClassicArrangement.arrange(7, 9, 7 * 9 - 9, 0);
            assertEquals(expected.width(), sent.width());
            assertEquals(expected.height(), sent.height());
            assertEquals(expected.inventorySize(), sent.inventorySize());
        } finally
        {
            WidgetLayouts.forgetServerSpecs();
        }
        assertEquals(248, WidgetLayouts.widgets("Standard", 39).width());
    }
}
