package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.block.TileGear;
import com.darkona.adventurebackpack.inventory.ContainerScubaGear;
import com.darkona.adventurebackpack.inventory.InventoryScubaGear;
import com.darkona.adventurebackpack.item.ItemScubaGear;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;

@SideOnly(Side.CLIENT)
public class GuiScubaGear extends GuiGear
{
    private InventoryScubaGear inventory;
    private ItemStack shown;
    private int shownAir = -1;

    public GuiScubaGear(EntityPlayer player, InventoryScubaGear inventory, TileGear tile)
    {
        super(new ContainerScubaGear(player, inventory, tile), player, tile, false, null);
        this.inventory = inventory;
        this.shown = inventory.getParentItemStack();
    }

    public GuiScubaGear(EntityPlayer player, InventoryScubaGear inventory, boolean wearing)
    {
        super(new ContainerScubaGear(player, inventory, wearing), player, null, wearing, null);
        this.inventory = inventory;
        this.shown = inventory.getParentItemStack();
    }

    @Override
    protected FluidTank fluid(int tank)
    {
        return tank == 0 ? inventory.getLeftTank() : inventory.getRightTank();
    }

    private void refresh()
    {
        ItemStack current = tile != null ? tile.getGear() : wearing ? Wearing.getWearingWearable(player) : player.getCurrentEquippedItem();
        if (current != null && current != shown && current.getItem() instanceof ItemScubaGear)
        {
            shown = current;
            inventory.setContainerStack(current);
        }
        int air = inventory.getAir();
        if (air != shownAir)
        {
            shownAir = air;
            setLabels(I18n.format("adventurebackpack:gui.scuba.tanks"), air + "/" + inventory.getCapacity());
        }
    }

    @Override
    protected void update()
    {
        refresh();
    }
}
