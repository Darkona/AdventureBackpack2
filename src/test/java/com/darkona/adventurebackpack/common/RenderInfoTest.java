package com.darkona.adventurebackpack.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RenderInfo is read again only when a tag it reads was written. Tanks are "Empty" here: a named fluid needs the
 * FluidRegistry, which needs the running game.
 */
class RenderInfoTest
{
    private static NBTTagCompound emptyTank()
    {
        NBTTagCompound t = new NBTTagCompound();
        t.setString("Empty", "");
        return t;
    }

    private static NBTTagCompound backpack(String color)
    {
        NBTTagCompound bd = new NBTTagCompound();
        if (color != null) bd.setString("colorName", color);
        bd.setTag("leftTank", emptyTank());
        bd.setTag("rightTank", emptyTank());
        bd.setTag("ABPItems", new NBTTagList());
        bd.setInteger("lastTime", 1);
        NBTTagCompound root = new NBTTagCompound();
        root.setTag("backpackData", bd);
        return root;
    }

    @Test
    void readsTheBackpackLook()
    {
        RenderInfo pig = new RenderInfo().read(backpack("Pig"), RenderInfo.KIND_BACKPACK);
        assertEquals("Pig", pig.colorName);
        assertTrue(pig.pigNose);
        assertFalse(pig.blend || pig.villagerNose || pig.ocelotNose);
        assertNull(pig.leftFluid);
        assertNull(pig.upperTool);

        assertTrue(new RenderInfo().read(backpack("Quartz"), RenderInfo.KIND_BACKPACK).blend);
        assertTrue(new RenderInfo().read(backpack("IronGolem"), RenderInfo.KIND_BACKPACK).villagerNose);
        assertEquals(RenderInfo.STANDARD, new RenderInfo().read(backpack(null), RenderInfo.KIND_BACKPACK).colorName);
        assertEquals(RenderInfo.STANDARD, new RenderInfo().read(null, RenderInfo.KIND_BACKPACK).colorName);
    }

    @Test
    void backpackIsReadAgainOnlyWhenWhatItDrawsIsWritten()
    {
        NBTTagCompound root = backpack("Pig");
        NBTTagCompound bd = root.getCompoundTag("backpackData");
        RenderInfo info = new RenderInfo().read(root, RenderInfo.KIND_BACKPACK);
        assertTrue(info.matches(root, RenderInfo.KIND_BACKPACK));

        bd.setInteger("lastTime", 2); // not drawn
        bd.setTag("extendedProperties", new NBTTagCompound());
        assertTrue(info.matches(root, RenderInfo.KIND_BACKPACK));

        bd.setTag("leftTank", emptyTank()); // a tank written (dirtyTanks)
        assertFalse(info.matches(root, RenderInfo.KIND_BACKPACK));
        info.read(root, RenderInfo.KIND_BACKPACK);

        bd.setTag("ABPItems", new NBTTagList()); // the items written (dirtyInventory)
        assertFalse(info.matches(root, RenderInfo.KIND_BACKPACK));
        info.read(root, RenderInfo.KIND_BACKPACK);

        bd.setString("colorName", "Cow");
        assertFalse(info.matches(root, RenderInfo.KIND_BACKPACK));
        assertEquals("Cow", info.read(root, RenderInfo.KIND_BACKPACK).colorName);

        assertFalse(info.matches(backpack("Cow"), RenderInfo.KIND_BACKPACK), "another stack's tag");
        assertFalse(info.matches(root, RenderInfo.KIND_JETPACK), "another kind");
    }

    @Test
    void jetpackFlagsAndTheirChanges()
    {
        NBTTagCompound jd = new NBTTagCompound();
        jd.setTag("waterTank", emptyTank());
        jd.setTag("steamTank", emptyTank());
        jd.setInteger("burnTicks", 40);
        jd.setBoolean("status", true);
        jd.setBoolean("inUse", false);
        jd.setInteger("temperature", 90);
        NBTTagCompound root = new NBTTagCompound();
        root.setTag("jetpackData", jd);

        RenderInfo info = new RenderInfo().read(root, RenderInfo.KIND_JETPACK);
        assertTrue(info.jetpackBurning);
        assertTrue(info.jetpackOn);
        assertFalse(info.jetpackInUse);
        assertNull(info.steamFluid);

        jd.setInteger("temperature", 91); // shown by the window only
        assertTrue(info.matches(root, RenderInfo.KIND_JETPACK));
        jd.setBoolean("inUse", true);
        assertFalse(info.matches(root, RenderInfo.KIND_JETPACK));
        assertTrue(info.read(root, RenderInfo.KIND_JETPACK).jetpackInUse);
        jd.setInteger("burnTicks", 0);
        assertFalse(info.matches(root, RenderInfo.KIND_JETPACK));
        assertFalse(info.read(root, RenderInfo.KIND_JETPACK).jetpackBurning);
    }

    @Test
    void copterRotorShowsOnlyWhenTheStatusIsOn()
    {
        NBTTagCompound root = new NBTTagCompound();
        root.setTag("fuelTank", emptyTank());
        RenderInfo info = new RenderInfo().read(root, RenderInfo.KIND_COPTER);
        assertFalse(info.copterOn, "no status yet");
        root.setByte("status", (byte) 1);
        assertFalse(info.matches(root, RenderInfo.KIND_COPTER));
        assertTrue(info.read(root, RenderInfo.KIND_COPTER).copterOn);
        root.setByte("status", (byte) 0);
        assertFalse(info.read(root, RenderInfo.KIND_COPTER).copterOn);
    }

    @Test
    void everyReadMovesTheVersionAndForgetsTheTexture()
    {
        NBTTagCompound root = backpack("Pig");
        RenderInfo info = new RenderInfo().read(root, RenderInfo.KIND_BACKPACK);
        int v = info.version();
        info.texture = "cached";
        info.read(root, RenderInfo.KIND_BACKPACK);
        assertEquals(v + 1, info.version());
        assertNull(info.texture);
    }

    /** The Skeleton draws its quiver instead of the right tank, with the arrows of the synced fill level. */
    @Test
    void theSkeletonDrawsItsQuiverFromTheFillLevel()
    {
        NBTTagCompound root = backpack("Skeleton");
        NBTTagCompound bd = root.getCompoundTag("backpackData");
        RenderInfo info = new RenderInfo().read(root, RenderInfo.KIND_BACKPACK);
        assertTrue(info.quiver);
        assertEquals(0, info.quiverLevel);

        bd.setByte("quiverFill", (byte) QuiverRules.LEVEL_SOME); // the quiver written (dirtyInventory)
        assertFalse(info.matches(root, RenderInfo.KIND_BACKPACK));
        info.read(root, RenderInfo.KIND_BACKPACK);
        assertEquals(QuiverRules.LEVEL_SOME, info.quiverLevel);
        assertTrue(info.matches(root, RenderInfo.KIND_BACKPACK));

        // Another backpack has no quiver even with the tag
        NBTTagCompound pig = backpack("Pig");
        pig.getCompoundTag("backpackData").setByte("quiverFill", (byte) 3);
        RenderInfo p = new RenderInfo().read(pig, RenderInfo.KIND_BACKPACK);
        assertFalse(p.quiver);
        assertEquals(0, p.quiverLevel);
    }
}
