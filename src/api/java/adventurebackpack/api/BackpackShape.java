package adventurebackpack.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The size of a backpack window: rows and columns of its storage grid, and the slots of its quiver. The 3x3 crafting
 * grid takes the bottom right corner of that grid, so a backpack has {@code rows * columns - 9} storage slots; the
 * classic backpack is 6 rows of 8 columns (39 slots). The two tanks, the tool slots, the buttons and the player
 * inventory are placed around the grid by the mod, so a bigger shape needs no other change.
 * <p>
 * A shape with quiver slots (the Skeleton Backpack's, by default) has one tank: the quiver takes the place of the
 * right tank and its bucket slots, and its slots only take the arrows of the quiver's item list. 0 means no quiver.
 * The quiver (the three-argument constructor, {@link #getQuiverSlots} and {@link #withQuiverSlots}) is there since
 * API 1.3.0.
 * <p>
 * Immutable: the {@code with} methods return a new shape. The mod keeps every shape inside its own limits (see the
 * configuration page of the wiki), whatever an ability asks for.
 */
public final class BackpackShape
{
    /** Cells of the storage grid taken by the crafting grid. */
    public static final int CRAFTING_CELLS = 9;

    private final int rows;
    private final int columns;
    private final int quiverSlots;
    private final List<String> widgets;

    public BackpackShape(int rows, int columns)
    {
        this(rows, columns, 0);
    }

    /** Since API 1.3.0. */
    public BackpackShape(int rows, int columns, int quiverSlots)
    {
        this(rows, columns, quiverSlots, Collections.<String>emptyList());
    }

    private BackpackShape(int rows, int columns, int quiverSlots, List<String> widgets)
    {
        this.rows = rows;
        this.columns = columns;
        this.quiverSlots = quiverSlots;
        this.widgets = widgets;
    }

    public List<String> getWidgets()
    {
        return widgets;
    }

    public BackpackShape withWidget(String widgetId)
    {
        if (widgetId == null || widgetId.isEmpty()) throw new IllegalArgumentException("a widget needs an id");
        if (widgets.contains(widgetId)) return this;
        List<String> more = new ArrayList<String>(widgets);
        more.add(widgetId);
        return new BackpackShape(rows, columns, quiverSlots, Collections.unmodifiableList(more));
    }

    public int getRows()
    {
        return rows;
    }

    public int getColumns()
    {
        return columns;
    }

    /** Slots of the quiver that takes the place of the right tank; 0 for two tanks and no quiver. Since API 1.3.0. */
    public int getQuiverSlots()
    {
        return quiverSlots;
    }

    /** Storage slots of this shape: the grid minus the crafting corner. */
    public int getStorageSlots()
    {
        return rows * columns - CRAFTING_CELLS;
    }

    public BackpackShape withRows(int newRows)
    {
        return new BackpackShape(newRows, columns, quiverSlots, widgets);
    }

    public BackpackShape withColumns(int newColumns)
    {
        return new BackpackShape(rows, newColumns, quiverSlots, widgets);
    }

    /** The same grid with a quiver of this many slots in the place of the right tank (0: two tanks, no quiver). Since API 1.3.0. */
    public BackpackShape withQuiverSlots(int slots)
    {
        return new BackpackShape(rows, columns, slots, widgets);
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof BackpackShape)) return false;
        BackpackShape s = (BackpackShape) o;
        return s.rows == rows && s.columns == columns && s.quiverSlots == quiverSlots && s.widgets.equals(widgets);
    }

    @Override
    public int hashCode()
    {
        return ((rows * 31 + columns) * 31 + quiverSlots) * 31 + widgets.hashCode();
    }

    @Override
    public String toString()
    {
        String base = quiverSlots == 0 ? rows + "x" + columns : rows + "x" + columns + "+quiver" + quiverSlots;
        return widgets.isEmpty() ? base : base + widgets;
    }
}
