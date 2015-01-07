package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.CreativeTabAB;
import com.darkona.adventurebackpack.client.Icons;
import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import static net.minecraftforge.common.util.ForgeDirection.*;
import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;

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

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        icon = iconRegister.registerIcon(ModInfo.MOD_ID + ":campFire");
    }
    @Override
    public String getUnlocalizedName()
    {
        return "campFire";
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_)
    {
        return new TileCampFire();
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
        world.spawnParticle("largesmoke",  rndX, rndY, rndZ, 0.0D, 0.0D, 0.0D);
        for(int i = 0; i < 4; i++)
        {
            rndX = posX + 0.5f - (float)rnd.nextGaussian()*0.08f;
            rndY = (float) (posY + 1f - Math.cos((float)rnd.nextGaussian()*0.1f));
            rndZ = posZ + 0.5f - (float)rnd.nextGaussian()*0.08f;
            //world.spawnParticle("flame", posX+Math.sin(i/4), posY, posZ+Math.cos(i/4), 0.0D, 0.0D, 0.0D);
            world.spawnParticle("flame", rndX, rndY+0.16, rndZ, 0.0D, 0.0D, 0.0D);
        }

    }



    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileCampFire();
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
       setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.15F, 0.8F);
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
}
