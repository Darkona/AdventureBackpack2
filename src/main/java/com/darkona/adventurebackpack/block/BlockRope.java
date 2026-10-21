package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.common.RopeActions;
import com.darkona.adventurebackpack.common.RopeRules;
import com.darkona.adventurebackpack.init.ModItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockRope extends Block
{
    private static final float HALF = 2F / 16F;
    private static final float DEPTH = 1F / 16F;

    public BlockRope()
    {
        super(Material.circuits);
        setHardness(0.2F);
        setStepSound(soundTypeCloth);
        setBlockName("adventurebackpack:blockRope");
        setBlockTextureName("adventurebackpack:rope");
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
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
    {
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        float minX = 0.5F - HALF;
        float maxX = 0.5F + HALF;
        float minZ = 0.5F - HALF;
        float maxZ = 0.5F + HALF;
        if (RopeRules.hasWall(meta))
        {
            int dx = RopeRules.wallDx(meta);
            int dz = RopeRules.wallDz(meta);
            if (dx > 0) { maxX = 1F - DEPTH; minX = maxX - 2 * HALF; }
            if (dx < 0) { minX = DEPTH; maxX = minX + 2 * HALF; }
            if (dz > 0) { maxZ = 1F - DEPTH; minZ = maxZ - 2 * HALF; }
            if (dz < 0) { minZ = DEPTH; maxZ = minZ + 2 * HALF; }
        }
        setBlockBounds(minX, 0F, minZ, maxX, 1F, maxZ);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
        if (side == 0 || side == 1) return world.getBlock(x, y, z) != this;
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return false;
    }

    public boolean supported(World world, int x, int y, int z, int meta)
    {
        boolean ropeAbove = world.getBlock(x, y + 1, z) == this;
        boolean solidAbove = !ropeAbove && world.isSideSolid(x, y + 1, z, ForgeDirection.DOWN, false);
        boolean solidWall = false;
        if (RopeRules.hasWall(meta))
        {
            solidWall = world.isSideSolid(x + RopeRules.wallDx(meta), y, z + RopeRules.wallDz(meta), wallFace(meta), false);
        }
        return RopeRules.supported(meta, ropeAbove, solidAbove, solidWall);
    }

    private static ForgeDirection wallFace(int meta)
    {
        switch (meta)
        {
            case 0: return ForgeDirection.NORTH;
            case 1: return ForgeDirection.SOUTH;
            case 2: return ForgeDirection.WEST;
            default: return ForgeDirection.EAST;
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
    {
        if (world.isRemote) return;
        if (!supported(world, x, y, z, world.getBlockMetadata(x, y, z)))
        {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hx, float hy, float hz)
    {
        if (player.getCurrentEquippedItem() != null) return false;
        if (!world.isRemote) RopeActions.collect(player, world, x, y, z);
        return true;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
    {
        if (!world.isRemote && !player.capabilities.isCreativeMode) RopeActions.give(player, 1);
        return world.setBlockToAir(x, y, z);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta)
    {
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return ModItems.rope;
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z)
    {
        return ModItems.rope;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        blockIcon = register.registerIcon("adventurebackpack:rope");
    }
}
