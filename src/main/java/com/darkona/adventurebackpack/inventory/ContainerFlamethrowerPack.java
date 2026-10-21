package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.block.TileGear;
import com.darkona.adventurebackpack.common.layout.gear.GearArrangement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerFlamethrowerPack extends ContainerGear
{
    private final InventoryFlamethrowerPack gear;

    public ContainerFlamethrowerPack(EntityPlayer player, InventoryFlamethrowerPack inventory, TileGear tile)
    {
        this(player, inventory, false, tile);
    }

    public ContainerFlamethrowerPack(EntityPlayer player, InventoryFlamethrowerPack inventory, boolean wearing)
    {
        this(player, inventory, wearing, null);
    }

    private ContainerFlamethrowerPack(EntityPlayer player, InventoryFlamethrowerPack inventory, boolean wearing, TileGear tile)
    {
        super(player, inventory, GearArrangement.flamethrower(), wearing, tile);
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
