package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.client.Icons;
import com.darkona.adventurebackpack.handlers.GuiHandler;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Utils;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created on 12/10/2014.
 * @author Javier Darkona
 */
public class BlockAdventureBackpack extends BlockContainer
{

    public BlockAdventureBackpack()
    {
        super(new BackpackMaterial());
        setHardness(1.0f);
        setStepSound(soundTypeCloth);
        setResistance(2000f);
    }

    /**
     * Pretty effects for the bookshelf ;)
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param random
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
        if (getAssociatedTileColorName(world, x, y, z).equals("Bookshelf"))
        {
            ChunkCoordinates enchTable = Utils.findBlock3D(world, x, y, z, Blocks.enchanting_table, 2, 2);
            if(enchTable !=null)
            {
                if (!world.isAirBlock((enchTable.posX - x) / 2 + x, enchTable.posY, (enchTable.posZ - z) / 2 + z))
                {
                    return;
                }
                for (int o = 0; o < 4; o++)
                {
                    world.spawnParticle("enchantmenttable",enchTable.posX + 0.5D,enchTable.posY + 2.0D,enchTable.posZ + 0.5D,
                            ((x - enchTable.posX) + random.nextFloat()) - 0.5D,
                            ((y - enchTable.posY) - random.nextFloat() - 1.0F),
                            ((z - enchTable.posZ) + random.nextFloat()) - 0.5D);
                }
            }
        }
    }

    public int getMobilityFlag()
    {
        return 0;
    }

    @Override
    public String getHarvestTool(int metadata)
    {
        return null;
    }

    @Override
    public int getHarvestLevel(int metadata)
    {
        return 0;
    }

    @Override
    public boolean isToolEffective(String type, int metadata)
    {
        return true;
    }

    private String getAssociatedTileColorName(IBlockAccess world, int x, int y, int z)
    {
        return ((TileAdventureBackpack) world.getTileEntity(x, y, z)).getColorName();
    }
    @Override
    public boolean canRenderInPass(int pass)
    {
        return true;
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
    {
        return false;
    }

    @Override
    public float getEnchantPowerBonus(World world, int x, int y, int z)
    {
        return getAssociatedTileColorName(world, x, y, z).equals("Bookshelf") ? 10 : 0;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public boolean isWood(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public boolean isLeaves(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        return 0;
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta)
    {
        return true;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
        if (getAssociatedTileColorName(world, x, y, z).equals("Cactus"))
        {
            entity.attackEntityFrom(DamageSource.cactus, 1.0F);
        }
    }

    /**
     * Called when a player hits the block. Args: world, x, y, z, player
     *
     * @param p_149699_1_
     * @param p_149699_2_
     * @param p_149699_3_
     * @param p_149699_4_
     * @param p_149699_5_
     */
    @Override
    public void onBlockClicked(World p_149699_1_, int p_149699_2_, int p_149699_3_, int p_149699_4_, EntityPlayer p_149699_5_)
    {
        super.onBlockClicked(p_149699_1_, p_149699_2_, p_149699_3_, p_149699_4_, p_149699_5_);
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param side
     * @param hitX
     * @param hitY
     * @param hitZ
     * @param meta
     */
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
    {
        return super.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, meta);
    }


    @Override
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        return false;
    }

    @Override
    public String getUnlocalizedName()
    {
        return "blockAdventureBackpack";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        Icons.milkStill = iconRegister.registerIcon(ModInfo.MOD_ID + ":fluid.milk");
        Icons.melonJuiceStill = iconRegister.registerIcon(ModInfo.MOD_ID + ":fluid.melonJuiceStill");
        Icons.melonJuiceFlowing = iconRegister.registerIcon(ModInfo.MOD_ID + ":fluid.melonJuiceFlowing");
        Icons.mushRoomStewStill = iconRegister.registerIcon(ModInfo.MOD_ID + ":fluid.mushroomStewStill");
        Icons.mushRoomStewFlowing = iconRegister.registerIcon(ModInfo.MOD_ID + ":fluid.mushroomStewFlowing");
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        if (getAssociatedTileColorName(world, x, y, z).equals("Glowstone"))
        {
            return 15;
        } else if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileAdventureBackpack)
        {
            return ((TileAdventureBackpack) world.getTileEntity(x, y, z)).getLuminosity();
        } else
        {
            return 0;
        }
    }

    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int meta)
    {
        return getAssociatedTileColorName(world, x, y, z).equals("Redstone") ? 15 : 0;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        return getAssociatedTileColorName(world, x, y, z).equals("Redstone");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {

        FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.BACKPACK_TILE, world, x, y, z);
        return true;
    }


    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        ItemStack backpack = new ItemStack(ModItems.adventureBackpack, 1);
        BackpackNames.setBackpackColorNameFromDamage(backpack, BackpackNames.getBackpackDamageFromName(getAssociatedTileColorName(world, x, y, z)));
        return backpack;
    }

    @Override
    public boolean hasTileEntity(int meta)
    {
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
    {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        meta = (meta & 8) >= 8 ? meta - 8 : meta;
        meta = (meta & 4) >= 4 ? meta - 4 : meta;
        switch (meta)
        {
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
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack)
    {
        int dir = MathHelper.floor_double((player.rotationYaw * 4F) / 360F + 0.5D) & 3;
        if (stack != null && stack.stackTagCompound != null && stack.stackTagCompound.hasKey("color"))
        {
            if (stack.stackTagCompound.getString("color").contains("BlockRedstone"))
            {
                dir = dir | 8;
            }
            if (stack.stackTagCompound.getString("color").contains("Lightgem"))
            {
                dir = dir | 4;
            }
        }
        world.setBlockMetadataWithNotify(x, y, z, dir, 3);
        createNewTileEntity(world, world.getBlockMetadata(x, y, z));

    }

    @Override
    public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int side)
    {
        return (ForgeDirection.getOrientation(side) == ForgeDirection.UP);
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2)
    {
        return Blocks.wool.getIcon(par1,par2);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.collisionRayTrace(world, x, y, z, start, end);
    }

    @Override
    public boolean canReplace(World p_149705_1_, int p_149705_2_, int p_149705_3_, int p_149705_4_, int p_149705_5_, ItemStack p_149705_6_)
    {
        return false;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvest)
    {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof TileAdventureBackpack && !world.isRemote && player != null)
        {
            if ((player.isSneaking()) ?
                    ((TileAdventureBackpack) tile).equip(world, player, x, y, z) :
                    ((TileAdventureBackpack) tile).drop(world, player, x, y, z))
            {
                return world.func_147480_a(x, y, z, false);
            }
        } else
        {
            return world.func_147480_a(x, y, z, false);
        }
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof IInventory)
        {
            IInventory inventory = (IInventory) te;

            for (int i = 0; i < inventory.getSizeInventory(); i++)
            {
                ItemStack stack = inventory.getStackInSlotOnClosing(i);

                if (stack != null)
                {
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

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileAdventureBackpack();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return createTileEntity(world, metadata);
    }

    @Override
    public boolean canDropFromExplosion(Explosion p_149659_1_)
    {
        return false;
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        world.func_147480_a(x, y, z, false);
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
        //DO NOTHING
    }
}
