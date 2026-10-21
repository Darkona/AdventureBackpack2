package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.common.RopeActions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemRope extends ItemAB
{
    public ItemRope()
    {
        super();
        setUnlocalizedName("rope");
    }

    public static boolean is(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof ItemRope;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote) return true;
        return RopeActions.place(player, stack, world, x, y, z, side);
    }
}
