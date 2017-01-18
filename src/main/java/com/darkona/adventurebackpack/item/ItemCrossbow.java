package com.darkona.adventurebackpack.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created on 05/01/2015
 *
 * @author Darkona
 */
public class ItemCrossbow extends ItemAB
{

    public ItemCrossbow()
    {
        super();
        setFull3D();
        setUnlocalizedName("clockworkCrossbow");
        setMaxStackSize(1);
    }

    /**
     * Returns true if players can use this item to affect the world (e.g. placing blocks, placing ender eyes in portal)
     * when not in creative
     */
    @Override
    public boolean canItemEditBlocks()
    {
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    @Override
    public boolean isRepairable()
    {
        return super.isRepairable();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack p_77661_1_)
    {
        return EnumAction.bow;
    }

    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     *
     * @param stack
     * @param world
     * @param player
     */
    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
        super.onCreated(stack, world, player);

        NBTTagCompound xbowProps = new NBTTagCompound();

        xbowProps.setBoolean("Loaded", false);
        xbowProps.setShort("Magazine", (short) 0);
        xbowProps.setBoolean("Charging", false);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
    {

    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean current)
    {
        if (!stack.hasTagCompound())
        {
            stack.stackTagCompound = new NBTTagCompound();
            stack.stackTagCompound.setByte("Shot", (byte) 0);
            stack.stackTagCompound.setInteger("Reloading", 0);
        }

        if (current)
        {
            byte shot = stack.stackTagCompound.getByte("Shot");
            int reloading = stack.stackTagCompound.getInteger("Reloading");
            if (shot > 0) stack.stackTagCompound.setByte("Shot", (byte) (shot - 1));
            if (reloading > 0) stack.stackTagCompound.setInteger("Reloading", reloading - 1);
            if (entity instanceof EntityPlayer)
            {
                //((EntityPlayer)entity).setItemInUse(stack,2);
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (!stack.stackTagCompound.hasKey("Reloading")) stack.stackTagCompound.setInteger("Reloading", 0);
        int reloading = stack.stackTagCompound.getInteger("Reloading");
        if (reloading <= 0)
        {
            shootArrow(stack, player.worldObj, player, 1000);
            stack.stackTagCompound.setByte("Shot", (byte) 4);
            int reloadTime = 20;
            stack.stackTagCompound.setInteger("Reloading", reloadTime);

        }
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int counter)
    {
    }

    public void shootArrow(ItemStack stack, World world, EntityPlayer player, int count)
    {
        int j = count;

        boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

        if (flag || player.inventory.hasItem(Items.arrow))
        {
            float f = j / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;

            if (f < 0.1D)
            {
                return;
            }

            if (f > 1.0F)
            {
                f = 1.0F;
            }

            EntityArrow entityarrow = new EntityArrow(world, player, f * 3.0F);
            f = 1.0f;

            int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

            if (k > 0)
            {
                entityarrow.setDamage(entityarrow.getDamage() + k * 0.5D + 0.5D);
            }

            int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

            if (l > 0)
            {
                entityarrow.setKnockbackStrength(l);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0)
            {
                entityarrow.setFire(100);
            }

            world.playSoundAtEntity(player, "adventurebackpack:crossbowshot", 0.5F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (flag)
            {
                entityarrow.canBePickedUp = 2;
            } else
            {
                player.inventory.consumeInventoryItem(Items.arrow);
            }

            if (!world.isRemote)
            {
                world.spawnEntityInWorld(entityarrow);
            }
        }
    }
}
