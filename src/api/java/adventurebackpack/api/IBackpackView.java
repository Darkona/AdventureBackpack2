package adventurebackpack.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * A read-only snapshot of a worn backpack, taken when {@link AdventureBackpackAPI#getWornBackpack} is called. It does
 * not follow later changes of the backpack and writing to what it returns changes nothing: every stack it hands out is
 * a copy. Ask again when you need fresh values, and not every tick: building a snapshot reads the backpack's NBT.
 */
public interface IBackpackView
{
    /** A copy of the backpack's item stack. */
    ItemStack getStack();

    /** The colour name of the backpack, which is the key of its ability (for example {@code "Cow"}). */
    String getColorName();

    /** The number of tanks of the backpack: tank 0 is the left one and tank 1 the right one. */
    int getTankCount();

    /** A copy of the fluid in the tank, or {@code null} when it is empty. */
    FluidStack getTankFluid(int tank);

    /** The capacity of the tank in millibuckets. */
    int getTankCapacity(int tank);

    /**
     * The number of slots. Slots 0 to 38 are storage, 39 and 40 the two tool slots, 41 to 44 the bucket slots of the
     * tanks (in and out of the left tank, then of the right one), and from 45 on the extra storage of a bigger
     * backpack (see {@link BackpackShape}). A smaller backpack has fewer storage slots; the numbers above do not move.
     */
    int getSlotCount();

    /** A copy of the stack in the slot, or {@code null} when it is empty. */
    ItemStack getSlot(int slot);
}
