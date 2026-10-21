package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.block.TileGear;
import com.darkona.adventurebackpack.common.layout.gear.GearArrangement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerCopter extends ContainerGear
{
    private final InventoryCopterPack gear;

    public ContainerCopter(EntityPlayer player, InventoryCopterPack inventory, TileGear tile)
    {
        this(player, inventory, false, tile);
    }

    public ContainerCopter(EntityPlayer player, InventoryCopterPack inventory, boolean wearing)
    {
        this(player, inventory, wearing, null);
    }

    private ContainerCopter(EntityPlayer player, InventoryCopterPack inventory, boolean wearing, TileGear tile)
    {
        super(player, inventory, GearArrangement.copter(), wearing, tile);
        this.gear = inventory;
    }

    @Override
    protected ItemStack parentStack()
    {
        return gear.getParentItemStack();
    }

    @Override
    protected boolean liveRefresh()
    {
        return true;
    }

    @Override
    protected void takeOut(int slot)
    {
        gear.setInventorySlotContents(slot, null);
    }

    @Override
    public void refresh()
    {
        if (!gear.isCurrentFor(gear.getParentItemStack())) gear.openInventory();
    }
}
