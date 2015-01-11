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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

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

    @Override
    public EnumAction getItemUseAction(ItemStack p_77661_1_)
    {
        return EnumAction.none;
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

        //stack.stackTagCompound.setBoolean("Shot",!(count % 5 == 0 || (count +1) %5 == 0 || (count-1)%5 == 0));
        byte pulled = stack.stackTagCompound.getByte("Shot");
        if(pulled > 0)stack.stackTagCompound.setByte("Shot", (byte) (pulled - 1));
        if(count % 5 == 0)
        {
            shootArrow(stack,player.worldObj,player);
            stack.stackTagCompound.setByte("Shot",(byte)3);
        }
        if(count <= 0)player.stopUsingItem();
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean current)
    {

    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        player.setItemInUse(stack,getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 9;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int counter)
    {
        stack.stackTagCompound.setByte("Shot", (byte) 0);
    }

    public void shootArrow(ItemStack stack, World world, EntityPlayer player)
    {
        int j = 1000;

        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, j);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return;
        }

        boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

        if (flag || player.inventory.hasItem(Items.arrow))
        {
            float f = (float) j / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;

            if ((double) f < 0.1D)
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
                entityarrow.setDamage(entityarrow.getDamage() + (double) k * 0.5D + 0.5D);
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

            world.playSoundAtEntity(player, "adventurebackpack:crossbowshot", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

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
