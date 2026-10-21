package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.block.TileGear;
import com.darkona.adventurebackpack.inventory.ContainerFlamethrowerPack;
import com.darkona.adventurebackpack.inventory.InventoryFlamethrowerPack;
import com.darkona.adventurebackpack.item.ItemFlamethrowerPack;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;

@SideOnly(Side.CLIENT)
public class GuiFlamethrowerPack extends GuiGear
{
    private static final String TOGGLE = "adventurebackpack:gui.toggle.pilot";

    private InventoryFlamethrowerPack inventory;
    private ItemStack shown;
    private int shownFuel = -1;
    private int shownOn = -1;

    public GuiFlamethrowerPack(EntityPlayer player, InventoryFlamethrowerPack inventory, TileGear tile)
    {
        super(new ContainerFlamethrowerPack(player, inventory, tile), player, tile, false, TOGGLE);
        this.inventory = inventory;
        this.shown = inventory.getParentItemStack();
    }

    public GuiFlamethrowerPack(EntityPlayer player, InventoryFlamethrowerPack inventory, boolean wearing)
    {
        super(new ContainerFlamethrowerPack(player, inventory, wearing), player, null, wearing, TOGGLE);
        this.inventory = inventory;
        this.shown = inventory.getParentItemStack();
    }

    @Override
    protected boolean isToggleOn()
    {
        return shownOn == 1;
    }

    @Override
    protected FluidTank fluid(int tank)
    {
        return tank == 0 ? inventory.getLeftTank() : inventory.getRightTank();
    }

    private void refresh()
    {
        ItemStack current = tile != null ? tile.getGear() : wearing ? Wearing.getWearingWearable(player) : player.getCurrentEquippedItem();
        if (current != null && current != shown && current.getItem() instanceof ItemFlamethrowerPack)
        {
            shown = current;
            inventory.setContainerStack(current);
        }
        int fuel = inventory.getFuel();
        int on = inventory.isOn() ? 1 : 0;
        if (fuel != shownFuel || on != shownOn)
        {
            shownFuel = fuel;
            shownOn = on;
            setLabels(I18n.format("adventurebackpack:gui.flamethrower.fuel"), fuel + "/" + inventory.getCapacity(), "",
                    I18n.format(on == 1 ? "adventurebackpack:gui.flamethrower.lit" : "adventurebackpack:gui.flamethrower.out"));
        }
    }

    @Override
    protected void update()
    {
        refresh();
    }
}
