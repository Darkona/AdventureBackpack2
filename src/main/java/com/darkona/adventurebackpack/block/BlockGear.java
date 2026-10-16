package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.handlers.GuiHandler;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.inventory.GearType;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class BlockGear extends BlockContainer
{
    public BlockGear()
    {
        super(Material.iron);
        setHardness(ConfigHandler.BACKPACK_HARDNESS);
        setResistance(ConfigHandler.BACKPACK_BLAST_RESISTANCE);
        setStepSound(soundTypeMetal);
        setBlockName("adventurebackpack:blockGear");
    }

    public static int meta(int facing, GearType type)
    {
        return (facing & 3) | (type.ordinal() << 2);
    }

    public static int facing(int meta)
    {
        return meta & 3;
    }

    public static GearType type(int meta)
    {
        return GearType.byOrdinal((meta >> 2) & 3);
    }

    public boolean place(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side)
    {
        GearType type = GearType.of(stack);
        if (type == null || !type.placeable() || stack.stackSize <= 0) return false;
        y = PlacedWearables.placeY(world, x, y, z, side);
        if (y < 0 || !player.canPlayerEdit(x, y, z, side, stack)) return false;
        if (!world.canPlaceEntityOnSide(this, x, y, z, false, side, player, stack)) return false;
        if (!world.setBlock(x, y, z, this, meta(PlacedWearables.facing(player.rotationYaw), type), 3)) return false;
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileGear))
        {
            world.setBlockToAir(x, y, z);
            return false;
        }
        ItemStack placed = stack.copy();
        placed.stackSize = 1;
        ((TileGear) te).setGear(placed);
        world.markBlockForUpdate(x, y, z);
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, soundTypeMetal.func_150496_b(), 0.5F, 1.0F);
        --stack.stackSize;
        return true;
    }

    public static boolean useGearOn(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side)
    {
        GearType type = GearType.of(stack);
        if (type == null || !type.placeable() || !player.isSneaking()) return false;
        if (world.isRemote) return PlacedWearables.placeY(world, x, y, z, side) >= 0;
        return ModBlocks.blockGear.place(stack, player, world, x, y, z, side);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileGear)) return false;
        if (world.isRemote) return true;
        TileGear tile = (TileGear) te;
        if (player.isSneaking() && player.getCurrentEquippedItem() == null)
        {
            ItemStack gear = tile.takeGear();
            if (gear != null) PlacedWearables.equipOrGive(world, player, x, y, z, gear);
            world.setBlockToAir(x, y, z);
            return true;
        }
        if (!tile.enabled())
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.placedgear.disabled"));
            return true;
        }
        FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.GEAR_TILE, world, x, y, z);
        return true;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvest)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileGear && !world.isRemote && player != null)
        {
            ItemStack gear = ((TileGear) te).takeGear();
            if (gear != null)
            {
                if (player.isSneaking()) PlacedWearables.equipOrGive(world, player, x, y, z, gear);
                else if (!player.capabilities.isCreativeMode) PlacedWearables.spawnItem(world, x, y, z, gear);
            }
        }
        return world.func_147480_a(x, y, z, false);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileGear && !world.isRemote)
        {
            ItemStack gear = ((TileGear) te).takeGear();
            if (gear != null) PlacedWearables.spawnItem(world, x, y, z, gear);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileGear) ((TileGear) te).flushSync();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        return new ArrayList<ItemStack>();
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
        return 0;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        ItemStack gear = te instanceof TileGear ? ((TileGear) te).getGear() : null;
        return gear == null ? null : new ItemStack(gear.getItem());
    }

    @Override
    public int getMobilityFlag()
    {
        return 2;
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
    {
        return false;
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosion)
    {
        return false;
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
    {
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        if (facing(world.getBlockMetadata(x, y, z)) % 2 == 0) setBlockBounds(0.15F, 0.0F, 0.3F, 0.85F, 0.8F, 0.7F);
        else setBlockBounds(0.3F, 0.0F, 0.15F, 0.7F, 0.8F, 0.85F);
    }

    @Override
    public void setBlockBoundsForItemRender()
    {
        setBlockBounds(0.15F, 0.0F, 0.3F, 0.85F, 0.8F, 0.7F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.collisionRayTrace(world, x, y, z, start, end);
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return Blocks.iron_block.getIcon(side, 0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileGear();
    }
}
