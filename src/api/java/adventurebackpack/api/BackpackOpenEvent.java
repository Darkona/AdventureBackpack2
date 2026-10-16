package adventurebackpack.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * A player is about to open the inventory screen of a backpack. It is posted on {@code MinecraftForge.EVENT_BUS}, on the
 * logical server only, once per opening (a right click, the key binding or the button of the screen): it is a player
 * action and never a per-tick event. Other screens (copter pack, jetpack, scuba gear, flamethrower pack, fuel station)
 * do not post it.
 * <p>
 * The event is cancelable: when a listener cancels it, the screen does not open. Tell the player why (a chat message)
 * if you cancel it.
 */
@Cancelable
public class BackpackOpenEvent extends PlayerEvent
{
    /** Where the backpack is when the player opens it. */
    public enum Source
    {
        /** The backpack worn on the back, opened with the key binding. */
        WEARING,
        /** The backpack the player holds in the hand. */
        HOLDING,
        /** A backpack placed as a block in the world. */
        PLACED
    }

    /** The world of the player. */
    public final World world;
    /** Where the backpack is. */
    public final Source source;
    /** The worn or held backpack, or {@code null} for {@link Source#PLACED}. Do not change it. */
    public final ItemStack backpack;
    /** The block position of a {@link Source#PLACED} backpack; the player's own block position otherwise. */
    public final int x, y, z;

    public BackpackOpenEvent(EntityPlayer player, World world, Source source, ItemStack backpack, int x, int y, int z)
    {
        super(player);
        this.world = world;
        this.source = source;
        this.backpack = backpack;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
