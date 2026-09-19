package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.inventory.AirContainers;

import com.darkona.adventurebackpack.block.BlockSleepingBag;
import com.darkona.adventurebackpack.config.ConfigHandler;
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

import java.util.HashMap;
import java.util.List;

/**
 * Created on 11/10/2014
 * @author Darkona
 *
 */
public class ItemComponent extends ItemAB
{
    /** Damage values of the components (one more than their index in {@code names}). */
    public static final int SLEEPING_BAG = 1;
    public static final int HOSE_NOZZLE = 3;
    public static final int INFLATABLE_BOAT = 7;
    public static final int MOTORIZED_BOAT = 8;
    public static final int HYDRO_BLADES = 9;
    public static final int INFLATABLE_CHAMBER = 10;
    public static final int AIR_TANK = 11;
    public static final int DIVING_MASK = 12;

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
            "inflatableChamber",
            "airTank",
            "divingMask",
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

        for(String name : names){
            IIcon temporalIcon = iconRegister.registerIcon(super.getUnlocalizedName(name).substring(this.getUnlocalizedName().indexOf(".") + 1));
            componentIcons.put(name, temporalIcon);
        }


        itemIcon = iconRegister.registerIcon(super.getUnlocalizedName("sleepingBag").substring(this.getUnlocalizedName().indexOf(".") + 1));
    }


    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        return componentIcons.get(nameFor(damage));

    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(nameFor(getDamage(stack)));

    }

    /** Components start at damage 1; a stack with damage 0 or past the list (/give, NEI) must not crash. */
    private String nameFor(int damage)
    {
        return names[MathHelper.clamp_int(damage - 1, 0, names.length - 1)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list)
    {
        for (int i = 1; i <= names.length; i++)
        {
            if (i >= INFLATABLE_BOAT && i <= INFLATABLE_CHAMBER && !ConfigHandler.INFLATABLEBOAT_ENABLED) continue;
            if (i >= AIR_TANK && i <= DIVING_MASK && !AirContainers.usable()) continue;
            list.add(new ItemStack(this, 1, i));
        }
    }

    /**
     * The Sleeping Bag goes on the ground like a bed, on top of the clicked block (enabled.sleepingBag and
     * sleepingbag.placeable). The bag works without a backpack and gives the item back when broken or rolled up.
     */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (stack.getItemDamage() != SLEEPING_BAG || side != 1) return false;
        if (!ConfigHandler.SLEEPINGBAG_ENABLED || !ConfigHandler.SLEEPINGBAG_PLACEABLE) return false;
        if (world.isRemote) return true;
        if (BlockSleepingBag.placeOnItsOwn(world, player, stack, x, y + 1, z) && !player.capabilities.isCreativeMode)
        {
            --stack.stackSize;
        }
        return true;
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
        if (!ConfigHandler.INFLATABLEBOAT_ENABLED) return stack;
        if (stack.getItemDamage() == INFLATABLE_BOAT) return placeBoat(stack, world, player, false);
        if (stack.getItemDamage() == MOTORIZED_BOAT) return placeBoat(stack, world, player, true);
        return stack;
    }
}
