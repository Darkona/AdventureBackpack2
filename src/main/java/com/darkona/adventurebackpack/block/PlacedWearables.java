package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.util.BackpackUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public final class PlacedWearables
{
    private PlacedWearables() {}

    public static int placeY(World world, int x, int y, int z, int side)
    {
        if (side != 1 || y <= 0 || y >= world.getHeight()) return -1;
        if (world.getBlock(x, y, z).getMaterial().isSolid()) ++y;
        return y <= 0 || y >= world.getHeight() ? -1 : y;
    }

    public static int facing(float yaw)
    {
        return MathHelper.floor_double((yaw * 4F) / 360F + 0.5D) & 3;
    }

    public static boolean spawnItem(World world, int x, int y, int z, ItemStack stack)
    {
        EntityItem droppedItem = new EntityItem(world, x + world.rand.nextFloat(), y + world.rand.nextFloat(), z + world.rand.nextFloat(), stack);
        float mult = 0.05F;
        droppedItem.motionX = (-0.5F + world.rand.nextFloat()) * mult;
        droppedItem.motionY = (4 + world.rand.nextFloat()) * mult;
        droppedItem.motionZ = (-0.5F + world.rand.nextFloat()) * mult;
        return world.spawnEntityInWorld(droppedItem);
    }

    public static boolean equipOrGive(World world, EntityPlayer player, int x, int y, int z, ItemStack stack)
    {
        if (BackpackUtils.equipWearable(stack, player) != BackpackUtils.reasons.SUCCESFUL)
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.equipped"));
            if (!player.inventory.addItemStackToInventory(stack))
            {
                return spawnItem(world, x, y, z, stack);
            }
        }
        return true;
    }
}
