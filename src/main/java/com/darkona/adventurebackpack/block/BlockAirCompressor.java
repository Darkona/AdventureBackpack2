package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.CreativeTabAB;
import com.darkona.adventurebackpack.client.Icons;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.handlers.GuiHandler;
import com.darkona.adventurebackpack.item.ItemScubaGear;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockAirCompressor extends BlockContainer
{
    private IIcon front, side, top, bottom;

    public BlockAirCompressor()
    {
        super(Material.iron);
        setHardness(3.5F);
        setResistance(10.0F);
        setStepSound(Block.soundTypeMetal);
        setBlockName("adventurebackpack:blockAirCompressor");
        setCreativeTab(CreativeTabAB.ADVENTURE_BACKPACK_CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        front = register.registerIcon(ModInfo.MOD_ID + ":airCompressor_front");
        side = register.registerIcon(ModInfo.MOD_ID + ":airCompressor_side");
        top = register.registerIcon(ModInfo.MOD_ID + ":airCompressor_top");
        bottom = top;
        Icons.airStill = register.registerIcon(ModInfo.MOD_ID + ":fluid.air");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int face, int meta)
    {
        if (face == 1) return top;
        if (face == 0) return bottom;
        return face == (meta < 2 ? 3 : meta) ? front : side;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack)
    {
        int facing = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int[] meta = {2, 5, 3, 4};
        world.setBlockMetadataWithNotify(x, y, z, meta[facing], 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int face, float hitX, float hitY, float hitZ)
    {
        if (!ConfigHandler.SCUBA_ENABLED) return false;
        if (world.isRemote) return true;
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileAirCompressor)) return false;
        TileAirCompressor compressor = (TileAirCompressor) te;
        if (player.getHeldItem() == null)
        {
            if (player.isSneaking())
            {
                ItemStack worn = Wearing.getWearingWearable(player);
                if (worn != null && worn.getItem() instanceof ItemScubaGear)
                {
                    compressor.fillGearByHand(player, worn);
                    return true;
                }
            } else if (compressor.pump(player))
            {
                return true;
            }
        }
        player.openGui(AdventureBackpack.instance, GuiHandler.AIR_COMPRESSOR, world, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileAirCompressor && !world.isRemote)
        {
            TileAirCompressor compressor = (TileAirCompressor) te;
            for (int i = 0; i < compressor.getSizeInventory(); i++)
            {
                ItemStack stack = compressor.getStackInSlot(i);
                if (stack == null) continue;
                EntityItem drop = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack.copy());
                world.spawnEntityInWorld(drop);
                compressor.setInventorySlotContents(i, null);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileAirCompressor();
    }
}
