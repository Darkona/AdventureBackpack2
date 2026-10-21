package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Pins the slots of the real backpack container: for each container index, what kind of slot it is, which index of
 * its inventory it shows and where it is drawn. Built without starting Minecraft (the player is allocated without
 * its constructor and the backpack is a stub), so it checks the container code itself, not a copy of it.
 */
class ContainerBackpackSlotsTest
{
    /** Builds the container on a stub backpack of the given colour; the crafting result is not looked up. */
    static ContainerBackpack build(String colorName) throws Exception
    {
        return build(colorName, new java.util.HashMap<Integer, ItemStack>());
    }

    /** As {@link #build(String)}, with a backpack whose slots are kept in {@code contents} (inventory index to stack). */
    static ContainerBackpack build(String colorName, java.util.Map<Integer, ItemStack> contents) throws Exception
    {
        return build(colorName, contents, 0);
    }

    /** As {@link #build(String, java.util.Map)}, with a backpack that saved {@code storage} storage slots. */
    static ContainerBackpack build(String colorName, java.util.Map<Integer, ItemStack> contents, int storage) throws Exception
    {
        return build(colorName, contents, storage, new com.darkona.adventurebackpack.common.sort.SlotLocks());
    }

    /** As {@link #build(String, java.util.Map, int)}, with the lock state of the backpack given. */
    static ContainerBackpack build(String colorName, java.util.Map<Integer, ItemStack> contents, int storage, com.darkona.adventurebackpack.common.sort.SlotLocks locks) throws Exception
    {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        sun.misc.Unsafe unsafe = (sun.misc.Unsafe) f.get(null);
        EntityPlayer player = (EntityPlayer) unsafe.allocateInstance(EntityOtherPlayerMP.class);
        player.inventory = new InventoryPlayer(player);
        IInventoryAdventureBackpack backpack = (IInventoryAdventureBackpack) Proxy.newProxyInstance(
                ContainerBackpackSlotsTest.class.getClassLoader(), new Class<?>[]{IInventoryAdventureBackpack.class},
                (proxy, method, args) -> {
                    switch (method.getName())
                    {
                        case "getColorName": return colorName;
                        case "getStackInSlot": return contents.get(args[0]);
                        case "getInventoryStackLimit": return 64;
                        case "getStorageSize": return storage;
                        case "getLocks": return locks;
                        case "getInventory": return null;
                        case "setInventorySlotContentsNoSave":
                        case "setInventorySlotContents":
                            if (args[1] == null) contents.remove(args[0]);
                            else contents.put((Integer) args[0], (ItemStack) args[1]);
                            return null;
                        case "getInventoryName": return "Adventure Backpack";
                        case "hashCode": return System.identityHashCode(proxy);
                        case "equals": return proxy == args[0];
                        default:
                            Class<?> r = method.getReturnType();
                            if (r == boolean.class) return false;
                            if (r == int.class) return 0;
                            return null;
                    }
                });
        return new ContainerBackpack(player, backpack, ContainerBackpack.SOURCE_WEARING)
        {
            @Override
            public void onCraftMatrixChanged(IInventory inventory)
            {
            }
        };
    }

    /** One line per container slot: kind, index in its inventory, x, y. */
    static List<String> describe(ContainerBackpack container)
    {
        List<String> out = new ArrayList<String>();
        for (Object o : container.inventorySlots)
        {
            Slot s = (Slot) o;
            String kind;
            if (s instanceof SlotCrafting) kind = "result";
            else if (s instanceof SlotTool) kind = "tool";
            else if (s instanceof SlotFluid) kind = "bucket";
            else if (s instanceof SlotQuiver) kind = "quiver";
            else if (s instanceof SlotRope) kind = "rope";
            else if (s instanceof SlotBackpack) kind = "storage";
            else if (s.inventory == container.craftMatrix) kind = "craft";
            else if (s.inventory instanceof InventoryPlayer) kind = "player";
            else kind = "?" + s.getClass().getSimpleName();
            out.add(s.slotNumber + " " + kind + " " + s.getSlotIndex() + " " + s.xDisplayPosition + "," + s.yDisplayPosition);
        }
        return out;
    }

    private static void add(List<String> out, String kind, int index, int x, int y)
    {
        out.add(out.size() + " " + kind + " " + index + " " + x + "," + y);
    }

    /**
     * The slots of the backpack window as they were before the layout refactor, re-ordered so the 36 player slots
     * come last (main inventory, then hotbar, as vanilla containers do): Mouse Tweaks and Inventory Tweaks expect it.
     * Positions and inventory indices are unchanged.
     */
    static List<String> expectedClassicSlots(boolean playerFirst)
    {
        List<String> e = new ArrayList<String>();
        if (playerFirst)
        {
            for (int col = 0; col < 9; col++) add(e, "player", col, 44 + 18 * col, 183);
            for (int row = 0; row < 3; row++)
                for (int col = 0; col < 9; col++) add(e, "player", 9 + col + row * 9, 44 + 18 * col, 125 + 18 * row);
        }
        int slot = 0;
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 8; col++) add(e, "storage", slot++, 62 + 18 * col, 7 + 18 * row);
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 5; col++) add(e, "storage", slot++, 62 + 18 * col, 61 + 18 * row);
        add(e, "tool", 39, 44, 79);
        add(e, "tool", 40, 44, 97);
        add(e, "bucket", 41, 6, 7);
        add(e, "bucket", 42, 6, 37);
        add(e, "bucket", 43, 226, 7);
        add(e, "bucket", 44, 226, 37);
        add(e, "rope", 45, 44, 43);
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++) add(e, "craft", col + row * 3, 152 + 18 * col, 61 + 18 * row);
        add(e, "result", 0, 226, 97);
        if (playerFirst) return e;
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++) add(e, "player", 9 + col + row * 9, 44 + 18 * col, 125 + 18 * row);
        for (int col = 0; col < 9; col++) add(e, "player", col, 44 + 18 * col, 183);
        return e;
    }

    @Test
    void theStandardBackpackKeepsItsSlotsWithThePlayerLast() throws Exception
    {
        assertEquals(expectedClassicSlots(false), describe(build("Standard")));
    }

    /**
     * Inventory Tweaks is a compile-only dependency, absent here as in a pack without it: the annotated container
     * still loads, builds its slots and hands out its annotations (the JVM skips the ones whose type is missing).
     */
    @Test
    void theContainerWorksWithoutInventoryTweaks() throws Exception
    {
        org.junit.jupiter.api.Assertions.assertThrows(ClassNotFoundException.class,
                () -> Class.forName("invtweaks.api.container.ChestContainer"));
        assertEquals(92, build("Standard").inventorySlots.size());
        assertEquals(0, ContainerBackpack.class.getAnnotations().length);
        assertEquals(0, ContainerBackpack.class.getMethod("invTweaksSections").getAnnotations().length);
        assertEquals(0, ContainerBackpack.class.getMethod("invTweaksRowSize").getAnnotations().length);
    }

    /**
     * Shift-click out of the window fills the player's slots in the old order: matching stacks first, hotbar before
     * main inventory, then the first empty hotbar slot. Tried from the crafting grid, whose slots need no running
     * game; the storage, tool and bucket slots take the same path (their onSlotChanged asks FML for the side).
     */
    @Test
    void shiftClickOutOfTheWindowStillFillsTheHotbarFirst() throws Exception
    {
        ContainerBackpack c = build("Standard");
        EntityPlayer player = c.player;
        Item item = new Item();
        int craft = BackpackLayout.view(c).craftStart();
        c.craftMatrix.setInventorySlotContents(0, new ItemStack(item, 10));
        player.inventory.mainInventory[20] = new ItemStack(item, 60);
        c.transferStackInSlot(player, craft);
        assertEquals(64, player.inventory.mainInventory[20].stackSize);
        assertEquals(6, player.inventory.mainInventory[0].stackSize);
        assertEquals(null, c.craftMatrix.getStackInSlot(0));

        player.inventory.mainInventory[0] = new ItemStack(new Item(), 1);
        c.craftMatrix.setInventorySlotContents(4, new ItemStack(item, 7));
        c.transferStackInSlot(player, craft + 4);
        assertEquals(7, player.inventory.mainInventory[1].stackSize);
    }
}
