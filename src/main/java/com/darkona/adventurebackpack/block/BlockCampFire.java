package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.CreativeTabAB;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created on 05/01/2015
 *
 * @author Darkona
 */
public class BlockCampFire extends BlockContainer
{
    private IIcon icon;

    public BlockCampFire()
    {
        super(Material.rock);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabAB.ADVENTURE_BACKPACK_CREATIVE_TAB);

    }

    private void func_149978_e()
    {
        this.setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.15F, 0.8F);
    }

    @SuppressWarnings("unused")
	private void blockBoundsForRender()
    {
        this.func_149978_e();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        icon = iconRegister.registerIcon(ModInfo.MOD_ID + ":campFire");
    }

    @Override
    public String getUnlocalizedName()
    {
        return "blockCampFire";
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_)
    {
        return new TileCampfire();
    }

    @Override
    public int tickRate(World p_149738_1_)
    {
        return 30;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public boolean isNormalCube()
    {
        return false;
    }

    /**
     * Indicate if a material is a normal solid opaque cube
     */
    @Override
    public boolean isBlockNormalCube()
    {
        return false;
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int posX, int posY, int posZ, Random rnd)
    {
        float rndX = posX + rnd.nextFloat();
        float rndY = (posY + 1) - rnd.nextFloat() * 0.1F;
        float rndZ = posZ + rnd.nextFloat();
        world.spawnParticle("largesmoke", rndX, rndY, rndZ, 0.0D, 0.0D, 0.0D);
        for (int i = 0; i < 4; i++)
        {
            rndX = posX + 0.5f - (float) rnd.nextGaussian() * 0.08f;
            rndY = (float) (posY + 1f - Math.cos((float) rnd.nextGaussian() * 0.1f));
            rndZ = posZ + 0.5f - (float) rnd.nextGaussian() * 0.08f;
            //world.spawnParticle("flame", posX+Math.sin(i/4), posY, posZ+Math.cos(i/4), 0.0D, 0.0D, 0.0D);
            world.spawnParticle("flame", rndX, rndY + 0.16, rndZ, 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileCampfire();
    }

    @Override
    public boolean hasTileEntity(int meta)
    {
        return true;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        return 11;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
    {
        this.setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.15F, 0.8F);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     *
     * @param p_149668_1_
     * @param p_149668_2_
     * @param p_149668_3_
     * @param p_149668_4_
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return super.getCollisionBoundingBoxFromPool(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     *
     * @param p_149633_1_
     * @param p_149633_2_
     * @param p_149633_3_
     * @param p_149633_4_
     */
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int p_149633_2_, int p_149633_3_, int p_149633_4_)
    {
        return super.getSelectedBoundingBoxFromPool(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
    }

    @Override
    public IIcon getIcon(IBlockAccess p_149673_1_, int p_149673_2_, int p_149673_3_, int p_149673_4_, int p_149673_5_)
    {
        return icon;
    }

    /**
     * Gets the block's texture. Args: side, meta
     *
     * @param p_149691_1_
     * @param p_149691_2_
     */
    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return icon;
    }

    /**
     * Determines if this block is classified as a Bed, Allowing
     * players to sleep in it, though the block has to specifically
     * perform the sleeping functionality in it's activated event.
     *
     * @param world  The current world
     * @param x      X Position
     * @param y      Y Position
     * @param z      Z Position
     * @param player The player or camera entity, null in some cases.
     * @return True to treat this as a bed
     */
    @Override
    public boolean isBed(IBlockAccess world, int x, int y, int z, EntityLivingBase player)
    {
        return true;
    }

    @Override
    public ChunkCoordinates getBedSpawnPosition(IBlockAccess world, int x, int y, int z, EntityPlayer player)
    {
        for (int i = y - 5; i <= y + 5; i++)
        {
            ChunkCoordinates spawn = Utils.getNearestEmptyChunkCoordinatesSpiral(world, x, z, x, i, z, 8, true, 1, (byte) 0, true);

            if (spawn != null)
            {
              return spawn;
            }
        }
        return null;
    }
}
