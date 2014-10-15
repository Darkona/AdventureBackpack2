package com.darkona.adventurebackpack.blocks;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.CreativeTabAB;
import com.darkona.adventurebackpack.client.Icons;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

/**
 * Created by Darkona on 12/10/2014.
 */
public class BlockAdventureBackpack extends BlockContainer {

    public BlockAdventureBackpack() {
        super(Material.cloth);
        setHardness(0.5f);
    }

    @Override
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return false;
    }

    @Override
    public String getUnlocalizedName() {
        return "blockAdventureBackpack";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        Icons.milkStill = iconRegister.registerIcon(ModInfo.MOD_ID + ":fluid.milk");
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        int l1 = 0, l2 = ((world.getBlockMetadata(x, y, z) & 4) >= 4) ? 15 : 0;
        if (world.getTileEntity(x, y, z) instanceof TileAdventureBackpack) {
            l1 = ((TileAdventureBackpack) world.getTileEntity(x, y, z)).luminosity;
        }
        return (l1 > l2) ? l1 : l2;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        return ((world.getBlockMetadata(x, y, z) & 8) >= 8) ? 15 : 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        return ((world.getBlockMetadata(x, y, z) & 8) >= 8) ? 15 : 0;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {

        if ((world.getBlockMetadata(x, y, z) & 8) >= 8) {
            switch (side) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return true;
                default:
                    return false;
            }
        } else
            return false;
    }

    @Override
    public boolean canProvidePower() {

        return true;
    }

//    @Override
//    public boolean canDragonDestroy(World world, int x, int y, int z) {
//        return false;
//    }

//    @Override
//    public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
//        return false;
//    }
//
//    @Override
//    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
//        return true;
//    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        FMLNetworkHandler.openGui(player, AdventureBackpack.instance, 0, world, x, y, z);
        try {
            TileAdventureBackpack te = (TileAdventureBackpack) world.getTileEntity(x, z, z);

        } catch (Exception oops) {
        }

        return true;
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        meta = (meta & 8) >= 8 ? meta - 8 : meta;
        meta = (meta & 4) >= 4 ? meta - 4 : meta;
        switch (meta) {
            case 0:
            case 2:
                setBlockBounds(0.0F, 0.0F, 0.4F, 1.0F, 0.6F, 0.6F);
                break;
            case 1:
            case 3:
                setBlockBounds(0.4F, 0.0F, 0.0F, 0.6F, 0.6F, 1.0F);
                break;
        }
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        int dir = MathHelper.floor_double((player.rotationYaw * 4F) / 360F + 0.5D) & 3;
        // onBlockAdded(world, x, y, z);
        if (stack != null && stack.stackTagCompound != null && stack.stackTagCompound.hasKey("color")) {
            if (stack.stackTagCompound.getString("color").contains("BlockRedstone"))
                dir = dir | 8;
            if (stack.stackTagCompound.getString("color").contains("Lightgem"))
                dir = dir | 4;
        }
        world.setBlockMetadataWithNotify(x, y, z, dir, 3);
        createNewTileEntity(world, world.getBlockMetadata(x, y, z));

    }

    @Override
    public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int side) {
        return !(ForgeDirection.getOrientation(side) == ForgeDirection.DOWN);
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public IIcon getIcon(int par1, int par2) {
        return null;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.collisionRayTrace(world, x, y, z, start, end);
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvest) {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof TileAdventureBackpack && !world.isRemote && player != null) {
            if ((player.isSneaking()) ? ((TileAdventureBackpack) tile).equip(world, player, x, y, z) : ((TileAdventureBackpack) tile).drop(world, player, x, y, z)) {
                return world.func_147480_a(x, y, z, false);
            }
        } else {
            return world.func_147480_a(x, y, z, false);
        }
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof IInventory) {
            IInventory inventory = (IInventory) te;

            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlotOnClosing(i);

                if (stack != null) {
                    float spawnX = x + world.rand.nextFloat();
                    float spawnY = y + world.rand.nextFloat();
                    float spawnZ = z + world.rand.nextFloat();
                    float mult = 0.05F;

                    EntityItem droppedItem = new EntityItem(world, spawnX, spawnY, spawnZ, stack);

                    droppedItem.motionX = -0.5F + world.rand.nextFloat() * mult;
                    droppedItem.motionY = 4 + world.rand.nextFloat() * mult;
                    droppedItem.motionZ = -0.5 + world.rand.nextFloat() * mult;

                    world.spawnEntityInWorld(droppedItem);
                }
            }
        }

        super.breakBlock(world, x, y, z, world.getBlock(x, y, z), meta);
    }

    //This one is useless


    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileAdventureBackpack();
    }

   /*@Override
    public void onBlockAdded(World world, int x, int y, int z) {
        createNewTileEntity(world, world.getBlockMetadata(x,y,z));
       super.onBlockAdded(world, x, y, z);
    }*/

    @Override
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return ModItems.adventureBackpack;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return createTileEntity(world, metadata);
    }
}
