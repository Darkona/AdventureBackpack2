package adventurebackpack.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fluids.Fluid;
import org.junit.jupiter.api.Test;

/** The three events of the API: each one is marked cancelable and carries what the Javadoc promises. Posting needs the running game. */
class ApiEventsTest
{
    @Test
    void everyEventIsCancelable()
    {
        // Forge's class transformer turns @Cancelable into isCancelable(): in a plain unit test only the annotation shows
        assertTrue(FluidDrinkEvent.class.isAnnotationPresent(Cancelable.class));
        assertTrue(BackpackAbilityEvent.class.isAnnotationPresent(Cancelable.class));
        assertTrue(BackpackOpenEvent.class.isAnnotationPresent(Cancelable.class));
        assertFalse(new FluidDrinkEvent(null, null, null, 0).isCanceled());
    }

    @Test
    void theFieldsKeepWhatTheModPosts()
    {
        Fluid fluid = new Fluid("apieventtest");
        FluidDrinkEvent drink = new FluidDrinkEvent(null, null, fluid, 250);
        assertSame(fluid, drink.fluid);
        assertEquals(250, drink.amount);
        BackpackAbilityEvent ability = new BackpackAbilityEvent(null, null, "Dragon", null);
        assertEquals("Dragon", ability.colorName);
        BackpackOpenEvent open = new BackpackOpenEvent(null, null, BackpackOpenEvent.Source.HOLDING, null, 1, 2, 3);
        assertEquals(BackpackOpenEvent.Source.HOLDING, open.source);
        assertEquals(2, open.y);
    }
}
