package adventurebackpack.api;

import net.minecraft.item.ItemStack;

public interface BackpackWidget
{
    int NO_SHIFT = Integer.MIN_VALUE;
    int DEFAULT_STACK_LIMIT = 64;

    String getId();

    int getSlots();

    boolean accepts(ItemStack stack);

    default int getStackLimit()
    {
        return DEFAULT_STACK_LIMIT;
    }

    default int getShiftPriority()
    {
        return NO_SHIFT;
    }

    default ItemStack getGhostIcon()
    {
        return null;
    }
}
