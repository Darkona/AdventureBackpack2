package adventurebackpack.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * The ability of the backpack a player wears is about to run its worn hook ({@link IBackpackAbility#onWornTick}). It is
 * posted on {@code MinecraftForge.EVENT_BUS}, on the logical server, only for a colour that has a worn ability and only
 * while the player's abilities are on (config {@code enabled.abilities} and the player's own switch).
 * <p>
 * Cost warning: the hook runs every server tick for every player wearing such a backpack, so this event can be posted
 * twenty times a second per player. To keep the mod free of that cost it is not posted at all until an addon calls
 * {@link AdventureBackpackAPI#enableAbilityEvents()}: do it once, in preInit or init, if your addon listens to this
 * event. Keep the listener cheap: compare, return, never search or allocate.
 * <p>
 * The event is cancelable: when a listener cancels it, the ability does not run for that tick (it runs again on the next
 * one unless canceled again). Abilities that renew an effect only now and then simply lose that one call.
 */
@Cancelable
public class BackpackAbilityEvent extends PlayerEvent
{
    /** The world of the player. */
    public final World world;
    /** The colour name of the backpack, as in {@link AdventureBackpackAPI#registerAbility}, for example {@code "Bat"}. */
    public final String colorName;
    /** The worn backpack. The mod's own copy: do not keep it or change it unless you mean to. */
    public final ItemStack backpack;

    public BackpackAbilityEvent(EntityPlayer player, World world, String colorName, ItemStack backpack)
    {
        super(player);
        this.world = world;
        this.colorName = colorName;
        this.backpack = backpack;
    }
}
