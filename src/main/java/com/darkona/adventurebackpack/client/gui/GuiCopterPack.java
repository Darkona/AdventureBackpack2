package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.block.TileGear;
import com.darkona.adventurebackpack.common.RenderInfoCache;
import com.darkona.adventurebackpack.common.ShownKey;
import com.darkona.adventurebackpack.inventory.ContainerCopter;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

@SideOnly(Side.CLIENT)
public class GuiCopterPack extends GuiGear
{
    private static final String TOGGLE = "adventurebackpack:gui.toggle.copter";

    private InventoryCopterPack inventory;
    private boolean shownEngine;
    private ItemStack shownCopter;
    private ItemStack shownStack;
    private int shownVersion = -1;
    private final ShownKey labelKey = new ShownKey();
    private int shownCapacity = -1;

    public GuiCopterPack(EntityPlayer player, InventoryCopterPack inv, TileGear tile)
    {
        super(new ContainerCopter(player, inv, tile), player, tile, false, TOGGLE);
        this.inventory = inv;
    }

    public GuiCopterPack(EntityPlayer player, InventoryCopterPack inv, boolean wearing)
    {
        super(new ContainerCopter(player, inv, wearing), player, null, wearing, TOGGLE);
        this.inventory = inv;
    }

    @Override
    protected boolean isToggleOn()
    {
        ItemStack copter = Wearing.getWearingCopter(player);
        if (copter != shownCopter)
        {
            shownCopter = copter;
            shownEngine = copter != null && copter.hasTagCompound() && copter.getTagCompound().getByte("status") != ItemCopterPack.OFF_MODE;
        }
        return shownEngine;
    }

    @Override
    protected FluidTank fluid(int tank)
    {
        return inventory.getFuelTank();
    }

    private void refreshShown()
    {
        ItemStack source = tile != null ? tile.getGear() : wearing ? Wearing.getWearingCopter(player) : inventory.getParentItemStack();
        if (source == null) return;
        int version = RenderInfoCache.get(source).version();
        if (source == shownStack && version == shownVersion) return;
        if (source != inventory.getParentItemStack()) inventory.setContainerStack(source);
        inventory.openInventory();
        shownStack = source;
        shownVersion = version;
    }

    private void refreshLabels(FluidTank fuel)
    {
        FluidStack stack = fuel.getFluid();
        Fluid fluid = stack == null ? null : stack.getFluid();
        int amount = stack == null ? 0 : stack.amount;
        boolean fluidChanged = labelKey.changed(fluid, amount, stack);
        if (!fluidChanged && fuel.getCapacity() == shownCapacity) return;
        shownCapacity = fuel.getCapacity();
        String fluidName = fluid == null ? null : fluid.getName();
        String consumption = fluid == null ? "" : GearLabels.consumption(GeneralReference.liquidFuels.containsKey(fluidName),
                GeneralReference.liquidFuels.get(fluidName));
        setLabels(GearLabels.fuelName(fluidName), GearLabels.fuelAmount(stack != null, amount), Integer.toString(shownCapacity), "", consumption);
    }

    @Override
    protected void update()
    {
        refreshShown();
        refreshLabels(inventory.getFuelTank());
    }
}
