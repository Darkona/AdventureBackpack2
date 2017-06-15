package com.darkona.adventurebackpack.item;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.entity.EntityInflatableBoat;

/**
 * Created on 11/10/2014
 *
 * @author Darkona
 */
public class ItemComponent extends ItemAB
{
    private HashMap<String, IIcon> componentIcons = new HashMap<String, IIcon>();
    private String[] names = {
            "sleepingBag",
            "backpackTank",
            "hoseHead",
            "macheteHandle",
            "copterEngine",
            "copterBlades",
            "inflatableBoat",
            "inflatableBoatMotorized",
            "hydroBlades",
    };

    public ItemComponent()
    {
        setNoRepair();
        setHasSubtypes(true);
        setMaxStackSize(16);
        this.setUnlocalizedName("backpackComponent");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {

        for (String name : names)
        {
            IIcon temporalIcon = iconRegister.registerIcon(super.getUnlocalizedName(name).substring(this.getUnlocalizedName().indexOf(".") + 1));
            componentIcons.put(name, temporalIcon);
        }

        itemIcon = iconRegister.registerIcon(super.getUnlocalizedName("sleepingBag").substring(this.getUnlocalizedName().indexOf(".") + 1));
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
        return super.getIcon(stack, renderPass, player, usingItem, useRemaining);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        return componentIcons.get(names[damage - 1]);

    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(names[getDamage(stack) - 1]);

    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list)
    {
        for (int i = 1; i <= names.length; i++)
        {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int meta, float f1, float f2, float f3)
    {
        return false;
        /*if (itemStack.getItemDamage() != 1) return true;
        if (world.isRemote)
        {
            return true;
        } else if (meta != 1)
        {
            return false;
        } else
        {
           /* ++y;
            BlockSleepingBag blockbed = ModBlocks.blockSleepingBag;
            int i1 = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            byte b0 = 0;
            byte b1 = 0;
            if (i1 == 0)
            {
                b1 = 1;
            }
            if (i1 == 1)
            {
                b0 = -1;
            }
            if (i1 == 2)
            {
                b1 = -1;
            }
            if (i1 == 3)
            {
                b0 = 1;
            }
            if (player.canPlayerEdit(x, y, z, meta, itemStack) && player.canPlayerEdit(x + b0, y, z + b1, meta, itemStack))
            {
                if (world.isAirBlock(x, y, z) && world.isAirBlock(x + b0, y, z + b1) && World.doesBlockHaveSolidTopSurface(world, x, y - 1, z) && World.doesBlockHaveSolidTopSurface(world, x + b0, y - 1, z + b1))
                {
                    world.setBlock(x, y, z, blockbed, i1, 3);
                    if (world.getBlock(x, y, z) == blockbed)
                    {
                        world.setBlock(x + b0, y, z + b1, blockbed, i1 + 8, 3);
                    }
                    --itemStack.stackSize;
                    return true;
                } else
                {
                    return false;
                }
            } else
            {
                return false;
            }
        }*/
    }

    private ItemStack placeBoat(ItemStack stack, World world, EntityPlayer player, boolean motorized)
    {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f + 1.62D - (double) player.yOffset;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        MovingObjectPosition movingobjectposition = world.rayTraceBlocks(vec3, vec31, true);

        if (movingobjectposition == null)
        {
            return stack;
        } else
        {
            Vec3 vec32 = player.getLook(f);
            boolean flag = false;
            float f9 = 1.0F;
            List list = world.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(vec32.xCoord * d3, vec32.yCoord * d3, vec32.zCoord * d3).expand((double) f9, (double) f9, (double) f9));
            int i;

            for (i = 0; i < list.size(); ++i)
            {
                Entity entity = (Entity) list.get(i);

                if (entity.canBeCollidedWith())
                {
                    float f10 = entity.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double) f10, (double) f10, (double) f10);

                    if (axisalignedbb.isVecInside(vec3))
                    {
                        flag = true;
                    }
                }
            }

            if (flag)
            {
                return stack;
            } else
            {
                if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    i = movingobjectposition.blockX;
                    int j = movingobjectposition.blockY;
                    int k = movingobjectposition.blockZ;

                    if (world.getBlock(i, j, k) == Blocks.snow_layer)
                    {
                        --j;
                    }

                    EntityInflatableBoat inflatableBoat = new EntityInflatableBoat(world, i + 0.5, j + 1.0, k + 0.5, motorized);

                    inflatableBoat.rotationYaw = (float) (((MathHelper.floor_double((double) (player.rotationYaw * 4.0 / 360.0) + 0.5D) & 3) - 1) * 90);
                    if (!world.getCollidingBoundingBoxes(inflatableBoat, inflatableBoat.boundingBox.expand(-0.1, -0.1, -0.1)).isEmpty())
                    {
                        return stack;
                    }

                    if (!world.isRemote)
                    {
                        world.spawnEntityInWorld(inflatableBoat);
                    }

                    if (!player.capabilities.isCreativeMode)
                    {
                        --stack.stackSize;
                    }
                }
                return stack;
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (stack.getItemDamage() == 7) return placeBoat(stack, world, player, false);
        if (stack.getItemDamage() == 8) return placeBoat(stack, world, player, true);
        return stack;
    }
}