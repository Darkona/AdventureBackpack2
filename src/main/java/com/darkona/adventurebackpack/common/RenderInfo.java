package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.layout.classic.ClassicSlots;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.WidgetLayouts;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.item.ItemFlamethrowerPack;
import com.darkona.adventurebackpack.item.ItemScubaGear;
import com.darkona.adventurebackpack.inventory.InventoryFlamethrowerPack;
import com.darkona.adventurebackpack.inventory.InventoryScubaGear;
import com.darkona.adventurebackpack.item.ItemSteamJetpack;
import com.darkona.adventurebackpack.util.FluidMigration;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;

public final class RenderInfo
{
    public static final int KIND_OTHER = 0;
    public static final int KIND_BACKPACK = 1;
    public static final int KIND_COPTER = 2;
    public static final int KIND_JETPACK = 3;
    public static final int KIND_SCUBA = 4;
    public static final int KIND_FLAMETHROWER = 5;

    public static final String STANDARD = "Standard";

    private static final int NBT_COMPOUND = 10;

    public int kind = KIND_OTHER;
    public String colorName = STANDARD;
    public boolean blend;
    public boolean pigNose;
    public boolean villagerNose;
    public boolean ocelotNose;
    public FluidStack leftFluid;
    public int leftCapacity;
    public FluidStack rightFluid;
    public int rightCapacity;
    public ItemStack upperTool;
    public ItemStack lowerTool;
    public boolean quiver;
    public int quiverLevel;
    public boolean copterOn;
    public boolean jetpackBurning;
    public boolean jetpackOn, jetpackInUse, jetpackBoiling, jetpackLeaking;
    public FluidStack steamFluid;
    public int steamCapacity;
    public Object texture;

    private int version;
    private final Object[] keys = new Object[9];
    private int keyKind = -1;

    public int version()
    {
        return version;
    }

    public static int kindOf(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null) return KIND_OTHER;
        if (stack.getItem() instanceof ItemAdventureBackpack) return KIND_BACKPACK;
        if (stack.getItem() instanceof ItemCopterPack) return KIND_COPTER;
        if (stack.getItem() instanceof ItemSteamJetpack) return KIND_JETPACK;
        if (stack.getItem() instanceof ItemScubaGear) return KIND_SCUBA;
        if (stack.getItem() instanceof ItemFlamethrowerPack) return KIND_FLAMETHROWER;
        return KIND_OTHER;
    }

    public boolean matches(NBTTagCompound root, int kind)
    {
        if (kind != keyKind || keys[0] != root) return false;
        if (root == null) return true;
        switch (kind)
        {
            case KIND_BACKPACK:
            {
                NBTBase data = root.getTag("backpackData");
                if (keys[1] != data) return false;
                if (!(data instanceof NBTTagCompound)) return true;
                NBTTagCompound bd = (NBTTagCompound) data;
                return keys[2] == bd.getTag("ABPItems") && keys[3] == bd.getTag("leftTank")
                        && keys[4] == bd.getTag("rightTank") && keys[5] == bd.getTag("colorName")
                        && keys[6] == bd.getTag("quiverFill");
            }
            case KIND_COPTER:
                return keys[1] == root.getTag("fuelTank") && keys[2] == root.getTag("status");
            case KIND_JETPACK:
            {
                NBTBase data = root.getTag("jetpackData");
                if (keys[1] != data) return false;
                if (!(data instanceof NBTTagCompound)) return true;
                NBTTagCompound jd = (NBTTagCompound) data;
                return keys[2] == jd.getTag("waterTank") && keys[3] == jd.getTag("burnTicks")
                        && keys[4] == jd.getTag("steamTank") && keys[5] == jd.getTag("status")
                        && keys[6] == jd.getTag("inUse") && keys[7] == jd.getTag("boiling") && keys[8] == jd.getTag("leaking");
            }
            case KIND_SCUBA:
            case KIND_FLAMETHROWER:
            {
                NBTBase data = root.getTag(kind == KIND_SCUBA ? InventoryScubaGear.TAG : InventoryFlamethrowerPack.TAG);
                if (keys[1] != data) return false;
                if (!(data instanceof NBTTagCompound)) return true;
                NBTTagCompound td = (NBTTagCompound) data;
                return keys[2] == td.getTag("leftTank") && keys[3] == td.getTag("rightTank");
            }
            default:
                return true;
        }
    }

    public RenderInfo read(NBTTagCompound root, int kind)
    {
        clear();
        version++;
        this.kind = kind;
        keyKind = kind;
        keys[0] = root;
        if (root == null) return this;
        switch (kind)
        {
            case KIND_BACKPACK:
                readBackpack(root);
                break;
            case KIND_COPTER:
                keys[1] = root.getTag("fuelTank");
                keys[2] = root.getTag("status");
                leftFluid = readFluid(root.getCompoundTag("fuelTank"), false);
                leftCapacity = ConfigHandler.COPTER_TANK_CAPACITY;
                copterOn = root.hasKey("status") && root.getByte("status") != ItemCopterPack.OFF_MODE;
                break;
            case KIND_JETPACK:
            {
                NBTBase data = root.getTag("jetpackData");
                keys[1] = data;
                leftCapacity = ConfigHandler.JETPACK_WATER_CAPACITY;
                steamCapacity = ConfigHandler.JETPACK_STEAM_CAPACITY;
                if (data instanceof NBTTagCompound)
                {
                    NBTTagCompound jd = (NBTTagCompound) data;
                    keys[2] = jd.getTag("waterTank");
                    keys[3] = jd.getTag("burnTicks");
                    keys[4] = jd.getTag("steamTank");
                    keys[5] = jd.getTag("status");
                    keys[6] = jd.getTag("inUse");
                    keys[7] = jd.getTag("boiling");
                    keys[8] = jd.getTag("leaking");
                    leftFluid = readFluid(jd.getCompoundTag("waterTank"), false);
                    steamFluid = readFluid(jd.getCompoundTag("steamTank"), false);
                    jetpackBurning = jd.getInteger("burnTicks") > 0;
                    jetpackOn = jd.getBoolean("status");
                    jetpackInUse = jd.getBoolean("inUse");
                    jetpackBoiling = jd.getBoolean("boiling");
                    jetpackLeaking = jd.getBoolean("leaking");
                }
                break;
            }
            case KIND_SCUBA:
            case KIND_FLAMETHROWER:
            {
                NBTBase data = root.getTag(kind == KIND_SCUBA ? InventoryScubaGear.TAG : InventoryFlamethrowerPack.TAG);
                keys[1] = data;
                leftCapacity = rightCapacity = kind == KIND_SCUBA ? ConfigHandler.SCUBA_TANK_CAPACITY : ConfigHandler.FLAMETHROWER_TANK_CAPACITY;
                if (data instanceof NBTTagCompound)
                {
                    NBTTagCompound td = (NBTTagCompound) data;
                    keys[2] = td.getTag("leftTank");
                    keys[3] = td.getTag("rightTank");
                    leftFluid = readFluid(td.getCompoundTag("leftTank"), false);
                    rightFluid = readFluid(td.getCompoundTag("rightTank"), false);
                }
                break;
            }
            default:
                break;
        }
        return this;
    }

    private void readBackpack(NBTTagCompound root)
    {
        leftCapacity = ConfigHandler.BACKPACK_TANK_CAPACITY;
        rightCapacity = ConfigHandler.BACKPACK_TANK_CAPACITY;
        NBTBase data = root.getTag("backpackData");
        keys[1] = data;
        if (data instanceof NBTTagCompound)
        {
            NBTTagCompound bd = (NBTTagCompound) data;
            keys[2] = bd.getTag("ABPItems");
            keys[3] = bd.getTag("leftTank");
            keys[4] = bd.getTag("rightTank");
            keys[5] = bd.getTag("colorName");
            keys[6] = bd.getTag("quiverFill");
            quiverLevel = bd.getByte("quiverFill");
            String name = bd.getString("colorName");
            if (name != null && !name.isEmpty()) colorName = name;
            leftFluid = readFluid(bd.getCompoundTag("leftTank"), true);
            rightFluid = readFluid(bd.getCompoundTag("rightTank"), true);
            WidgetLayout layout = WidgetLayouts.widgets(colorName, WidgetLayouts.storage(colorName, 0));
            int upperSlot = ClassicSlots.toolIndex(layout, 0);
            int lowerSlot = ClassicSlots.toolIndex(layout, 1);
            NBTTagList items = bd.getTagList("ABPItems", NBT_COMPOUND);
            for (int i = 0; i < items.tagCount(); i++)
            {
                NBTTagCompound item = items.getCompoundTagAt(i);
                int slot = item.getByte("Slot");
                if (slot == upperSlot) upperTool = ItemStack.loadItemStackFromNBT(item);
                else if (slot == lowerSlot) lowerTool = ItemStack.loadItemStackFromNBT(item);
            }
        }
        blend = isBlended(colorName);
        pigNose = colorName.equals("Pig") || colorName.equals("Horse");
        villagerNose = colorName.equals("Villager") || colorName.equals("IronGolem");
        ocelotNose = colorName.equals("Ocelot");
        quiver = WidgetLayouts.quiver(colorName) > 0;
        if (!quiver) quiverLevel = 0;
    }

    public int tankCount()
    {
        switch (kind)
        {
            case KIND_BACKPACK:
            case KIND_JETPACK:
            case KIND_SCUBA:
            case KIND_FLAMETHROWER:
                return 2;
            case KIND_COPTER:
                return 1;
            default:
                return 0;
        }
    }

    public FluidStack tankFluid(int i)
    {
        if (i == 0) return leftFluid;
        return kind == KIND_JETPACK ? steamFluid : rightFluid;
    }

    public int tankCapacity(int i)
    {
        if (i == 0) return leftCapacity;
        return kind == KIND_JETPACK ? steamCapacity : rightCapacity;
    }

    public static boolean isBlended(String color)
    {
        return color.equals("Quartz") || color.equals("Slime") || color.equals("Snow");
    }

    private static FluidStack readFluid(NBTTagCompound tank, boolean migrate)
    {
        if (tank.hasKey("Empty")) return null;
        if (migrate) FluidMigration.migrate(tank);
        return FluidStack.loadFluidStackFromNBT(tank);
    }

    private void clear()
    {
        kind = KIND_OTHER;
        colorName = STANDARD;
        blend = pigNose = villagerNose = ocelotNose = false;
        leftFluid = rightFluid = null;
        leftCapacity = rightCapacity = 0;
        upperTool = lowerTool = null;
        quiver = false;
        quiverLevel = 0;
        copterOn = jetpackBurning = jetpackOn = jetpackInUse = jetpackBoiling = jetpackLeaking = false;
        steamFluid = null;
        steamCapacity = 0;
        texture = null;
        for (int i = 0; i < keys.length; i++) keys[i] = null;
    }
}
