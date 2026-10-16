package com.darkona.adventurebackpack.inventory;

import adventurebackpack.api.BackpackShape;
import com.darkona.adventurebackpack.common.AbilityRegistry;
import com.darkona.adventurebackpack.config.ConfigHandler;

import java.util.concurrent.ConcurrentHashMap;

public final class BackpackLayouts
{
    private static final ConcurrentHashMap<String, BackpackShape> SHAPES = new ConcurrentHashMap<String, BackpackShape>();
    private static volatile int generation = Integer.MIN_VALUE;

    private BackpackLayouts()
    {
    }

    public static void invalidate()
    {
        SHAPES.clear();
    }

    public static BackpackShape shape(String colorName)
    {
        int now = ConfigHandler.BACKPACK_TYPES_GENERATION;
        if (now != generation)
        {
            SHAPES.clear();
            generation = now;
        }
        String key = colorName == null ? "" : colorName;
        BackpackShape shape = SHAPES.get(key);
        if (shape == null)
        {
            shape = resolve(key);
            SHAPES.put(key, shape);
        }
        return shape;
    }

    private static BackpackShape resolve(String colorName)
    {
        BackpackShape configured = new BackpackShape(ConfigHandler.backpackRows(colorName), ConfigHandler.backpackColumns(colorName),
                ConfigHandler.backpackQuiverSlots(colorName));
        BackpackShape asked = AbilityRegistry.INSTANCE.shape(colorName, configured);
        int rows = BackpackLayout.clamp(asked.getRows(), BackpackLayout.MIN_ROWS, BackpackLayout.MAX_ROWS);
        return new BackpackShape(rows, BackpackLayout.clamp(asked.getColumns(), BackpackLayout.MIN_COLUMNS, BackpackLayout.MAX_COLUMNS),
                BackpackLayout.quiverFor(rows, asked.getQuiverSlots()));
    }

    public static int quiver(String colorName)
    {
        return shape(colorName).getQuiverSlots();
    }

    public static int storage(String colorName, int saved)
    {
        BackpackShape s = shape(colorName);
        return BackpackLayout.storageFor(s.getRows(), s.getColumns(), saved);
    }

    public static BackpackLayout layout(String colorName, int storage)
    {
        BackpackShape s = shape(colorName);
        return BackpackLayout.forStorage(s.getRows(), s.getColumns(), storage, s.getQuiverSlots());
    }
}
