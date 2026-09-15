package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.CreativeTabAB;
import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * A station block (Fuel Station, later the Air Compressor): one plain block with a front face, a {@link TileStation}
 * and a GUI. Breaking it drops what is in its slots and the block itself with the fluid still in it (kept in the
 * item's NBT and put back when it is placed again); a creative player breaking it only gets the slots back.
 */
public abstract class BlockStation extends BlockContainer
{
    private static final int[] FACING = {2, 5, 3, 4};

    private final String textureName;
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconFront;

    protected BlockStation(String name, String textureName)
    {
        super(Material.iron);
        this.textureName = textureName;
        setBlockName(ModInfo.MOD_ID + ":" + name);
        setHardness(3.5F);
        setResistance(10.0F);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(CreativeTabAB.ADVENTURE_BACKPACK_CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        iconSide = register.registerIcon(ModInfo.MOD_ID + ":" + textureName + "_side");
        iconTop = register.registerIcon(ModInfo.MOD_ID + ":" + textureName + "_top");
        iconFront = register.registerIcon(ModInfo.MOD_ID + ":" + textureName + "_front");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (side == 0 || side == 1) return iconTop;
        // meta 0 is the item in the inventory: show the front facing the viewer
        int front = meta < 2 || meta > 5 ? 3 : meta;
        return side == front ? iconFront : iconSide;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack)
    {
        int quarter = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, FACING[quarter], 2);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileStation && stack != null && stack.hasTagCompound())
        {
            ((TileStation) te).readTank(stack.getTagCompound());
            te.markDirty();
        }
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileStation && player != null && player.capabilities.isCreativeMode)
        {
            ((TileStation) te).dropOnBreak = false;
        }
        super.onBlockHarvested(world, x, y, z, meta, player);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileStation && !world.isRemote)
        {
            TileStation station = (TileStation) te;
            for (int i = 0; i < station.getSizeInventory(); i++)
            {
                drop(world, x, y, z, station.getStackInSlot(i));
            }
            if (station.dropOnBreak)
            {
                ItemStack self = new ItemStack(Item.getItemFromBlock(this));
                if (station.getTank().getFluidAmount() > 0)
                {
                    NBTTagCompound tag = new NBTTagCompound();
                    station.writeTank(tag);
                    self.setTagCompound(tag);
                }
                drop(world, x, y, z, self);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    /** The drops are spawned by {@link #breakBlock}, which still sees the tile entity and its fluid. */
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        return new ArrayList<ItemStack>();
    }

    private static void drop(World world, int x, int y, int z, ItemStack stack)
    {
        if (stack == null || stack.stackSize <= 0) return;
        EntityItem item = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack.copy());
        item.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(item);
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    /** Redstone comparators read how full the tank is (0 to 15). */
    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileStation)) return 0;
        int amount = ((TileStation) te).getTank().getFluidAmount();
        int capacity = ((TileStation) te).getTank().getCapacity();
        if (amount <= 0 || capacity <= 0) return 0;
        return Math.min(15, 1 + (int) (14L * amount / capacity));
    }
}
