package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.block.TileGear;
import com.darkona.adventurebackpack.common.layout.gear.GearArrangement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerScubaGear extends ContainerGear
{
    private final InventoryScubaGear gear;

    public ContainerScubaGear(EntityPlayer player, InventoryScubaGear inventory, TileGear tile)
    {
        this(player, inventory, false, tile);
    }

    public ContainerScubaGear(EntityPlayer player, InventoryScubaGear inventory, boolean wearing)
    {
        this(player, inventory, wearing, null);
    }

    private ContainerScubaGear(EntityPlayer player, InventoryScubaGear inventory, boolean wearing, TileGear tile)
    {
        super(player, inventory, GearArrangement.scuba(), wearing, tile);
        this.gear = inventory;
    }

    @Override
    protected ItemStack parentStack()
    {
        return gear.getParentItemStack();
    }

    @Override
    public void refresh()
    {
        gear.openInventory();
    }
}
