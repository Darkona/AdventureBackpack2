package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import adventurebackpack.api.IAsynchronousInventory;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.layout.gear.GearArrangement;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.junit.jupiter.api.Test;

class ContainerGearTest
{
    private static EntityPlayer player() throws Exception
    {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        sun.misc.Unsafe unsafe = (sun.misc.Unsafe) f.get(null);
        EntityPlayer player = (EntityPlayer) unsafe.allocateInstance(EntityOtherPlayerMP.class);
        player.inventory = new InventoryPlayer(player);
        return player;
    }

    private static ItemStack stack()
    {
        return new ItemStack(new Item());
    }

    private static ContainerGear gear(EntityPlayer player, WidgetLayout layout, ItemStack[] contents)
    {
        IAsynchronousInventory inventory = (IAsynchronousInventory) Proxy.newProxyInstance(
                ContainerGearTest.class.getClassLoader(), new Class<?>[]{IAsynchronousInventory.class},
                (proxy, method, args) -> {
                    switch (method.getName())
                    {
                        case "getStackInSlot": return contents[(Integer) args[0]];
                        case "getSizeInventory": return contents.length;
                        case "getInventoryStackLimit": return 64;
                        case "isItemValidForSlot": return false;
                        case "setInventorySlotContents":
                        case "setInventorySlotContentsNoSave":
                            contents[(Integer) args[0]] = (ItemStack) args[1];
                            return null;
                        case "getStackInSlotOnClosing": return contents[(Integer) args[0]];
                        case "hashCode": return System.identityHashCode(proxy);
                        case "equals": return proxy == args[0];
                        default:
                            Class<?> r = method.getReturnType();
                            if (r == boolean.class) return false;
                            if (r == int.class) return 0;
                            return null;
                    }
                });
        return new ContainerGear(player, inventory, layout, true, null)
        {
            @Override
            protected ItemStack parentStack()
            {
                return null;
            }

            @Override
            public void refresh()
            {
            }
        };
    }

    @Test
    void scubaLayoutPutsTheTwoAirSlotsFirstThenTheSharedPlayerInventory() throws Exception
    {
        ContainerGear c = gear(player(), GearArrangement.scuba(), new ItemStack[2]);
        assertEquals(2 + 27 + 9, c.inventorySlots.size());
        Slot in = c.getSlot(0);
        Slot out = c.getSlot(1);
        assertSame(SlotAirContainer.class, in.getClass());
        assertSame(SlotAirContainer.class, out.getClass());
        assertEquals(0, in.getSlotIndex());
        assertEquals(1, out.getSlotIndex());
        assertEquals(8, in.xDisplayPosition);
        assertEquals(7, in.yDisplayPosition);
        assertEquals(37, out.yDisplayPosition);
        assertEquals(84, c.getSlot(2).yDisplayPosition);
        assertEquals(9, c.getSlot(2).getSlotIndex());
        assertEquals(142, c.getSlot(29).yDisplayPosition);
        assertEquals(0, c.getSlot(29).getSlotIndex());
    }

    @Test
    void jetpackLayoutHasBucketsAndAFuelSlot() throws Exception
    {
        ContainerGear c = gear(player(), GearArrangement.jetpack(), new ItemStack[3]);
        assertEquals(3 + 36, c.inventorySlots.size());
        assertSame(SlotFluid.class, c.getSlot(0).getClass());
        assertSame(SlotFluid.class, c.getSlot(1).getClass());
        assertSame(SlotFuel.class, c.getSlot(2).getClass());
        assertEquals(2, c.getSlot(2).getSlotIndex());
    }

    @Test
    void copterAndFlamethrowerLayoutsUseTheirBucketSlots() throws Exception
    {
        ContainerGear copter = gear(player(), GearArrangement.copter(), new ItemStack[2]);
        assertEquals(2 + 36, copter.inventorySlots.size());
        assertSame(SlotFluid.class, copter.getSlot(0).getClass());
        ContainerGear flame = gear(player(), GearArrangement.flamethrower(), new ItemStack[2]);
        assertEquals(2 + 36, flame.inventorySlots.size());
        assertSame(SlotFuelContainer.class, flame.getSlot(0).getClass());
        assertSame(SlotFuelContainer.class, flame.getSlot(1).getClass());
    }
}
