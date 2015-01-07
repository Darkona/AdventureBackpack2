package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Darkona on 11/10/2014.
 */
public class ItemComponent extends ItemAB
{

    private IIcon sleepingBagIcon;
    private IIcon backpackTankIcon;
    private IIcon hoseHeadIcon;
    private IIcon macheteHandleIcon;
    private IIcon copterEngineIcon;
    private IIcon copterBladesIcon;


    public ItemComponent()
    {
        setNoRepair();
        setHasSubtypes(true);
        setMaxStackSize(1);
        this.setUnlocalizedName("backpackComponent");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        itemIcon = iconRegister.registerIcon(super.getUnlocalizedName("sleepingBag").substring(this.getUnlocalizedName().indexOf(".") + 1));
        sleepingBagIcon = iconRegister.registerIcon(super.getUnlocalizedName("sleepingBag").substring(this.getUnlocalizedName().indexOf(".") + 1));
        backpackTankIcon = iconRegister.registerIcon(super.getUnlocalizedName("backpackTank").substring(this.getUnlocalizedName().indexOf(".") + 1));
        hoseHeadIcon = iconRegister.registerIcon(super.getUnlocalizedName("hoseHead").substring(this.getUnlocalizedName().indexOf(".") + 1));
        macheteHandleIcon = iconRegister.registerIcon(super.getUnlocalizedName("macheteHandle").substring(this.getUnlocalizedName().indexOf(".") + 1));
        copterEngineIcon = iconRegister.registerIcon(super.getUnlocalizedName("copterEngine").substring(this.getUnlocalizedName().indexOf(".") + 1));
        copterBladesIcon = iconRegister.registerIcon(super.getUnlocalizedName("copterBlades").substring(this.getUnlocalizedName().indexOf(".") + 1));
    }

    /**
     * Player, Render pass, and item usage sensitive version of getIconIndex.
     *
     * @param stack        The item stack to get the icon for. (Usually this, and usingItem will be the same if usingItem is not null)
     * @param renderPass   The pass to get the icon for, 0 is default.
     * @param player       The player holding the item
     * @param usingItem    The item the player is actively using. Can be null if not using anything.
     * @param useRemaining The ticks remaining for the active item.
     * @return The icon index
     */
    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
        return super.getIcon(stack, renderPass, player, usingItem, useRemaining);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        switch (damage)
        {
            case 1:
                return sleepingBagIcon;
            case 2:
                return backpackTankIcon;
            case 3:
                return hoseHeadIcon;
            case 4:
                return macheteHandleIcon;
            case 5:
                return copterEngineIcon;
            case 6:
            case 7:
            case 8:

                return copterBladesIcon;
        }
        return itemIcon;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        switch (getDamage(stack))
        {
            case 1:
                return super.getUnlocalizedName("sleepingBag");
            case 2:
                return super.getUnlocalizedName("backpackTank");
            case 3:
                return super.getUnlocalizedName("hoseHead");
            case 4:
                return super.getUnlocalizedName("macheteHandle");
            case 5:
                return super.getUnlocalizedName("copterEngine");
            case 6:
                return super.getUnlocalizedName("copterBlades");
            case 7:
                return super.getUnlocalizedName("inflatableBoat");
            case 8:
                return super.getUnlocalizedName("hydroBlades");
        }
        return super.getUnlocalizedName("backpackComponent");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list)
    {
        for (int i = 1; i <= 8; i++)
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

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     *
     * @param stack
     * @param world
     * @param player
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(stack.getItemDamage() != 7) return stack;
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)f + 1.62D - (double)player.yOffset;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        MovingObjectPosition movingobjectposition = world.rayTraceBlocks(vec3, vec31, true);

        if (movingobjectposition == null)
        {
            return stack;
        }
        else
        {
            Vec3 vec32 = player.getLook(f);
            boolean flag = false;
            float f9 = 1.0F;
            List list = world.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(vec32.xCoord * d3, vec32.yCoord * d3, vec32.zCoord * d3).expand((double)f9, (double)f9, (double)f9));
            int i;

            for (i = 0; i < list.size(); ++i)
            {
                Entity entity = (Entity)list.get(i);

                if (entity.canBeCollidedWith())
                {
                    float f10 = entity.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f10, (double)f10, (double)f10);

                    if (axisalignedbb.isVecInside(vec3))
                    {
                        flag = true;
                    }
                }
            }

            if (flag)
            {
                return stack;
            }
            else
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

                    EntityInflatableBoat inflatableBoat = new EntityInflatableBoat(world, i + 0.5, j + 1.0, k + 0.5, false);
                    inflatableBoat.rotationYaw = (float)(((MathHelper.floor_double((double)(player.rotationYaw * 4.0 / 360.0) + 0.5D) & 3) - 1) * 90);

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
}
