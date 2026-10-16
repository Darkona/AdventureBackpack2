package com.darkona.adventurebackpack.playerProperties;

import com.darkona.adventurebackpack.common.Constants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The players near a wearer get what their client draws and runs, not the inventory nor the counters. */
class ObserverViewTest
{
    private static NBTTagCompound item(int slot, int id)
    {
        NBTTagCompound item = new NBTTagCompound();
        item.setShort("id", (short) id);
        item.setByte("Count", (byte) 1);
        item.setByte("Slot", (byte) slot);
        return item;
    }

    private static NBTTagCompound wornBackpack(int... slots)
    {
        NBTTagList items = new NBTTagList();
        for (int slot : slots) items.appendTag(item(slot, 257 + slot));
        NBTTagCompound tank = new NBTTagCompound();
        tank.setString("FluidName", "water");
        tank.setInteger("Amount", 4000);
        NBTTagCompound bd = new NBTTagCompound();
        bd.setTag("ABPItems", items);
        bd.setTag("leftTank", tank);
        bd.setString("colorName", "Pig");
        bd.setTag("extendedProperties", new NBTTagCompound());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("backpackData", bd);
        NBTTagCompound stack = new NBTTagCompound();
        stack.setShort("id", (short) 4100);
        stack.setByte("Count", (byte) 1);
        stack.setTag("tag", tag);
        return stack;
    }

    private static NBTTagList items(NBTTagCompound view)
    {
        return view.getCompoundTag("tag").getCompoundTag("backpackData").getTagList("ABPItems", 10);
    }

    @Test
    void backpackKeepsOnlyTheToolsOfItsInventory()
    {
        NBTTagCompound full = wornBackpack(0, 5, Constants.upperTool, Constants.lowerTool);
        NBTTagCompound before = (NBTTagCompound) full.copy();
        NBTTagCompound view = ObserverView.wearable(full);

        NBTTagList kept = items(view);
        assertEquals(2, kept.tagCount());
        assertEquals(Constants.upperTool, kept.getCompoundTagAt(0).getByte("Slot"));
        assertEquals(Constants.lowerTool, kept.getCompoundTagAt(1).getByte("Slot"));
        NBTTagCompound bd = view.getCompoundTag("tag").getCompoundTag("backpackData");
        assertEquals("Pig", bd.getString("colorName"));
        assertEquals(4000, bd.getCompoundTag("leftTank").getInteger("Amount"));
        assertTrue(bd.hasKey("extendedProperties"), "abilities on every client read it");
        assertEquals(4100, view.getShort("id"));
        assertEquals(before, full, "the owner's data is not changed");
    }

    @Test
    void movingItemsInsideTheBackpackChangesNothingForThePlayersNear()
    {
        NBTTagCompound a = ObserverView.wearable(wornBackpack(0, Constants.upperTool));
        NBTTagCompound b = ObserverView.wearable(wornBackpack(7, 12, Constants.upperTool));
        assertEquals(a, b);
        NBTTagCompound c = ObserverView.wearable(wornBackpack(0, Constants.lowerTool));
        assertNotEquals(a, c, "a tool on the model did change");
    }

    @Test
    void jetpackSendsTheFlagsNotTheCounters()
    {
        NBTTagCompound jd = new NBTTagCompound();
        jd.setTag("waterTank", new NBTTagCompound());
        jd.setTag("steamTank", new NBTTagCompound());
        jd.setInteger("temperature", 87);
        jd.setInteger("coolTicks", 3000);
        jd.setInteger("burnTicks", 57);
        jd.setInteger("currentBurn", 1600);
        jd.setBoolean("status", true);
        jd.setBoolean("inUse", true);
        jd.setBoolean("boiling", true);
        jd.setTag("inventory", new NBTTagList());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("jetpackData", jd);
        NBTTagCompound stack = new NBTTagCompound();
        stack.setTag("tag", tag);

        NBTTagCompound view = ObserverView.wearable(stack).getCompoundTag("tag").getCompoundTag("jetpackData");
        assertEquals(1, view.getInteger("burnTicks"));
        assertTrue(view.getBoolean("status") && view.getBoolean("inUse") && view.getBoolean("boiling"));
        assertTrue(view.hasKey("waterTank") && view.hasKey("steamTank"));
        assertFalse(view.hasKey("temperature") || view.hasKey("coolTicks") || view.hasKey("currentBurn") || view.hasKey("inventory"));

        // the burn counter going down every tick is the same view: no packet to the players near
        jd.setInteger("burnTicks", 56);
        jd.setInteger("temperature", 88);
        assertEquals(view, ObserverView.wearable(stack).getCompoundTag("tag").getCompoundTag("jetpackData"));
        jd.setInteger("burnTicks", 0);
        assertEquals(0, ObserverView.wearable(stack).getCompoundTag("tag").getCompoundTag("jetpackData").getInteger("burnTicks"));
    }

    @Test
    void otherGearAndTheOtherPropertiesGoWhole()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("fuelTank", new NBTTagCompound());
        tag.setByte("status", (byte) 1);
        NBTTagCompound copter = new NBTTagCompound();
        copter.setShort("id", (short) 4101);
        copter.setTag("tag", tag);
        assertEquals(copter, ObserverView.wearable(copter));

        NBTTagCompound props = new NBTTagCompound();
        props.setTag("wearable", wornBackpack(3, Constants.upperTool));
        props.setBoolean("abilitiesOff", true);
        props.setBoolean("forceCampFire", false);
        NBTTagCompound view = ObserverView.properties(props);
        assertTrue(view.getBoolean("abilitiesOff"));
        assertTrue(view.hasKey("forceCampFire"));
        assertEquals(1, items(view.getCompoundTag("wearable")).tagCount());

        NBTTagCompound none = new NBTTagCompound();
        none.setBoolean("forceCampFire", true);
        assertEquals(none, ObserverView.properties(none));
    }

    /** The quiver's stacks stay with the owner; the players near only get its fill level, for the arrows on the model. */
    @Test
    void theQuiverReachesThePlayersNearAsItsFillLevelOnly()
    {
        NBTTagCompound full = wornBackpack(Constants.upperTool);
        NBTTagCompound bd = full.getCompoundTag("tag").getCompoundTag("backpackData");
        bd.setString("colorName", "Skeleton");
        NBTTagList quiver = new NBTTagList();
        quiver.appendTag(item(0, 262));
        bd.setTag("ABPQuiver", quiver);
        bd.setByte("quiverFill", (byte) 2);
        NBTTagCompound view = ObserverView.wearable(full);
        NBTTagCompound viewData = view.getCompoundTag("tag").getCompoundTag("backpackData");
        assertFalse(viewData.hasKey("ABPQuiver"));
        assertEquals(2, viewData.getByte("quiverFill"));

        // More arrows at the same level: nothing changes for them, so nothing is sent
        NBTTagCompound more = (NBTTagCompound) full.copy();
        more.getCompoundTag("tag").getCompoundTag("backpackData").getTagList("ABPQuiver", 10).appendTag(item(1, 262));
        assertEquals(view, ObserverView.wearable(more));
    }
}
