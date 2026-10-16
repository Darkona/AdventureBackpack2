package adventurebackpack.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * The ability of one backpack colour. Each hook has an empty default: an ability implements only the moments it
 * cares about, and the registry only calls the hooks that were overridden.
 * <p>
 * Abilities are shared by every backpack of their colour, so an implementation holds no per-backpack or per-player
 * state: that goes in the stack's NBT or in the world clock.
 */
public interface IBackpackAbility
{
    /**
     * Every tick while a player wears the backpack (server and client, abilities enabled and switched on for the
     * player). Tier 3 of the performance contract: return early, no allocation when nothing changed.
     */
    default void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
    }

    /**
     * Every tick while the backpack is placed in the world as a block. The tile is an Adventure Backpack tile entity;
     * an implementation that needs its tanks casts it.
     */
    default void onPlacedTick(World world, TileEntity backpackTile)
    {
    }

    /**
     * When the backpack stops giving its worn ability: it is taken off, or the wearer switches abilities off. Undo
     * here what {@link #onWornTick} leaves on the player (potion effects, for example).
     */
    default void onRemoval(EntityPlayer player, World world, ItemStack backpack)
    {
    }

    /**
     * The window of every backpack of this colour, worn, held or placed: return a bigger (or different) shape to give
     * the backpack more storage. {@code configured} is what the config gives this colour; the default keeps it. The
     * mod asks once per colour when the config is read (Tier 1), so the answer must depend only on the colour.
     * <p>
     * A backpack never loses slots: one that already holds more storage than the new shape keeps its size. Client
     * and server must answer the same, like every config value of the window. Since API 1.2.0.
     * <p>
     * {@link BackpackShape#withQuiverSlots} gives the backpack a quiver in the place of its right tank (the Skeleton
     * Backpack has one by config); 0 quiver slots keeps the two tanks.
     */
    default BackpackShape getShape(BackpackShape configured)
    {
        return configured;
    }
}
