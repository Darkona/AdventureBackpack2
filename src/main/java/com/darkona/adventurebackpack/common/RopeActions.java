package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.common.layout.classic.ClassicSlots;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public final class RopeActions
{
    private RopeActions() {}

    public static boolean place(EntityPlayer player, ItemStack held, World world, int x, int y, int z, int side)
    {
        Block clicked = world.getBlock(x, y, z);
        boolean extend = clicked == ModBlocks.blockRope;
        int meta;
        int px = x;
        int pz = z;
        int targetY;
        int existing = 0;
        if (extend)
        {
            meta = world.getBlockMetadata(x, y, z);
            int bottom = y;
            int top = y;
            while (bottom > 1 && y - bottom < RopeRules.MAX_LENGTH && world.getBlock(x, bottom - 1, z) == ModBlocks.blockRope) bottom--;
            while (top - y < RopeRules.MAX_LENGTH && world.getBlock(x, top + 1, z) == ModBlocks.blockRope) top++;
            targetY = bottom - 1;
            existing = top - bottom + 1;
        } else
        {
            meta = RopeRules.attachMeta(side);
            if (meta == RopeRules.NONE) return false;
            px = x + RopeRules.startDx(side);
            pz = z + RopeRules.startDz(side);
            targetY = y + RopeRules.startDy(side);
        }
        if (targetY < 1 || !player.canPlayerEdit(px, targetY, pz, side, held)) return false;
        int free = 0;
        while (free < RopeRules.PER_USE && targetY - free >= 1 && isFree(world, px, targetY - free, pz)) free++;
        if (free == 0) return false;
        if (!extend && !ModBlocks.blockRope.supported(world, px, targetY, pz, meta)) return false;
        boolean creative = player.capabilities.isCreativeMode;
        int n = RopeRules.segmentsToPlace(existing, free, creative ? 0 : available(player, held), creative);
        if (n <= 0) return false;
        for (int i = 0; i < n; i++) world.setBlock(px, targetY - i, pz, ModBlocks.blockRope, meta, 3);
        world.playSoundEffect(px + 0.5, targetY + 0.5, pz + 0.5, ModBlocks.blockRope.stepSound.func_150496_b(), 1F, 1F);
        int used = RopeRules.consumed(n, creative);
        if (used > 0) take(player, held, used);
        return true;
    }

    public static int collect(EntityPlayer player, World world, int x, int y, int z)
    {
        int count = 0;
        int bottom = y;
        while (bottom > 1 && y - bottom < RopeRules.MAX_LENGTH && world.getBlock(x, bottom - 1, z) == ModBlocks.blockRope) bottom--;
        for (int yy = bottom; yy <= y; yy++)
        {
            world.setBlockToAir(x, yy, z);
            count++;
        }
        if (count > 0 && !player.capabilities.isCreativeMode) give(player, count);
        return count;
    }

    public static void give(EntityPlayer player, int amount)
    {
        int left = amount;
        if (Wearing.isWearingBackpack(player))
        {
            InventoryBackpack backpack = Wearing.getBackpackInv(player, true);
            int index = ropeIndex(backpack);
            ItemStack stored = backpack.getStackInSlot(index);
            int stock = stored == null ? 0 : stored.stackSize;
            int room = RopeRules.toSlot(true, stock, left);
            if (room > 0)
            {
                if (stored == null) backpack.setInventorySlotContents(index, new ItemStack(ModItems.rope, room));
                else
                {
                    stored.stackSize += room;
                    backpack.setInventorySlotContents(index, stored);
                }
                left -= room;
            }
        }
        while (left > 0)
        {
            int chunk = Math.min(left, ModItems.rope.getItemStackLimit());
            ItemStack stack = new ItemStack(ModItems.rope, chunk);
            player.inventory.addItemStackToInventory(stack);
            if (stack.stackSize > 0) player.dropPlayerItemWithRandomChoice(stack, false);
            left -= chunk;
        }
        player.inventory.markDirty();
    }

    private static boolean isFree(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        return block != ModBlocks.blockRope && (block.isAir(world, x, y, z) || block.isReplaceable(world, x, y, z));
    }

    private static int ropeIndex(InventoryBackpack backpack)
    {
        return ClassicSlots.ropeIndex(backpack.getLayout());
    }

    private static int available(EntityPlayer player, ItemStack held)
    {
        int total = isRope(held) ? held.stackSize : 0;
        if (Wearing.isWearingBackpack(player))
        {
            InventoryBackpack backpack = Wearing.getBackpackInv(player, true);
            ItemStack stored = backpack.getStackInSlot(ropeIndex(backpack));
            if (isRope(stored)) total += stored.stackSize;
        }
        for (ItemStack stack : player.inventory.mainInventory)
        {
            if (isRope(stack) && stack != held) total += stack.stackSize;
        }
        return total;
    }

    private static void take(EntityPlayer player, ItemStack held, int amount)
    {
        int left = amount;
        boolean fromHand = isRope(held);
        if (fromHand)
        {
            int t = Math.min(left, held.stackSize);
            held.stackSize -= t;
            left -= t;
        }
        if (Wearing.isWearingBackpack(player))
        {
            InventoryBackpack backpack = Wearing.getBackpackInv(player, true);
            int index = ropeIndex(backpack);
            ItemStack stored = backpack.getStackInSlot(index);
            if (left > 0 && isRope(stored))
            {
                int t = Math.min(left, stored.stackSize);
                backpack.decrStackSize(index, t);
                left -= t;
            }
            if (fromHand) HandRefill.refill(held, backpack, index);
        }
        ItemStack[] main = player.inventory.mainInventory;
        for (int i = 0; i < main.length && left > 0; i++)
        {
            ItemStack stack = main[i];
            if (!isRope(stack) || stack == held) continue;
            int t = Math.min(left, stack.stackSize);
            stack.stackSize -= t;
            if (stack.stackSize <= 0) main[i] = null;
            left -= t;
        }
        player.inventory.markDirty();
    }

    private static boolean isRope(ItemStack stack)
    {
        return stack != null && stack.getItem() == ModItems.rope;
    }
}
