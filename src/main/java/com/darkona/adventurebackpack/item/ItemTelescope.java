package com.darkona.adventurebackpack.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTelescope extends ItemAB
{
    public static final float ZOOM = 0.1F;
    private static final int MAX_USE_TICKS = 72000;

    public ItemTelescope()
    {
        super();
        setUnlocalizedName("telescope");
        setMaxStackSize(1);
        setFull3D();
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.none;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return MAX_USE_TICKS;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        player.setItemInUse(stack, MAX_USE_TICKS);
        return stack;
    }
}
