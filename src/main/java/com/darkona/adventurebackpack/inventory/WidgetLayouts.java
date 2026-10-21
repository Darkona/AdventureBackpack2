package com.darkona.adventurebackpack.inventory;

import adventurebackpack.api.BackpackShape;
import com.darkona.adventurebackpack.common.AbilityRegistry;
import com.darkona.adventurebackpack.common.layout.BackpackSpec;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.layout.classic.ClassicArrangement;
import com.darkona.adventurebackpack.common.layout.classic.ClassicComposition;
import com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.widget.ExternalWidgetType;
import com.darkona.adventurebackpack.inventory.widget.ExternalWidgets;
import com.darkona.adventurebackpack.inventory.widget.classic.ClassicWidgetTypes;
import com.darkona.adventurebackpack.reference.BackpackNames;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class WidgetLayouts
{
    private static final ConcurrentHashMap<String, BackpackSpec> SPECS = new ConcurrentHashMap<String, BackpackSpec>();
    private static final ConcurrentHashMap<String, WidgetLayout> WIDGETS = new ConcurrentHashMap<String, WidgetLayout>();
    private static volatile Map<String, BackpackSpec> remote = Collections.emptyMap();
    private static volatile int generation = Integer.MIN_VALUE;

    static
    {
        ClassicWidgetTypes.install();
    }

    private WidgetLayouts()
    {
    }

    public static void invalidate()
    {
        SPECS.clear();
        WIDGETS.clear();
    }

    public static void useServerSpecs(Map<String, BackpackSpec> specs)
    {
        remote = Collections.unmodifiableMap(new LinkedHashMap<String, BackpackSpec>(specs));
        invalidate();
    }

    public static void forgetServerSpecs()
    {
        if (remote.isEmpty()) return;
        remote = Collections.emptyMap();
        invalidate();
    }

    public static Map<String, BackpackSpec> localSpecs()
    {
        Map<String, BackpackSpec> specs = new LinkedHashMap<String, BackpackSpec>();
        for (String name : BackpackNames.backpackNames) specs.put(name, resolve(name));
        return specs;
    }

    public static BackpackSpec spec(String colorName)
    {
        int now = ConfigHandler.BACKPACK_TYPES_GENERATION;
        if (now != generation)
        {
            invalidate();
            generation = now;
        }
        String key = colorName == null ? "" : colorName;
        BackpackSpec sent = remote.get(key);
        if (sent != null) return sent;
        BackpackSpec spec = SPECS.get(key);
        if (spec == null)
        {
            spec = resolve(key);
            SPECS.put(key, spec);
        }
        return spec;
    }

    private static BackpackSpec resolve(String colorName)
    {
        BackpackShape configured = new BackpackShape(ConfigHandler.backpackRows(colorName), ConfigHandler.backpackColumns(colorName),
                ConfigHandler.backpackQuiverSlots(colorName));
        BackpackShape asked = AbilityRegistry.INSTANCE.shape(colorName, configured);
        int n = 0;
        int[] kinds = new int[asked.getWidgets().size()];
        int[] slots = new int[kinds.length];
        for (String id : asked.getWidgets())
        {
            ExternalWidgetType type = ExternalWidgets.get(id);
            if (type == null) continue;
            kinds[n] = type.kind();
            slots[n++] = type.slots();
        }
        return ClassicComposition.of(asked.getRows(), asked.getColumns(), asked.getQuiverSlots(), Arrays.copyOf(kinds, n), Arrays.copyOf(slots, n));
    }

    public static int quiver(String colorName)
    {
        return spec(colorName).paramOf(ClassicWidgets.QUIVER, 0);
    }

    public static int storage(String colorName, int saved)
    {
        BackpackSpec s = spec(colorName);
        return ClassicArrangement.storageFor(s.rows(), s.columns(), saved);
    }

    public static WidgetLayout widgets(String colorName, int storage)
    {
        BackpackSpec s = spec(colorName);
        String key = (colorName == null ? "" : colorName) + '/' + storage;
        WidgetLayout layout = WIDGETS.get(key);
        if (layout == null)
        {
            layout = ClassicArrangement.forStorage(s, storage);
            WIDGETS.put(key, layout);
        }
        return layout;
    }
}
