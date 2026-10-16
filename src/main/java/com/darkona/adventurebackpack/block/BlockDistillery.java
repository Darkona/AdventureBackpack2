package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.CreativeTabAB;
import com.darkona.adventurebackpack.client.Icons;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.handlers.GuiHandler;
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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Random;

public class BlockDistillery extends BlockContainer
{
    private IIcon front, frontLit, side, top, bottom;

    public BlockDistillery()
    {
        super(Material.iron);
        setHardness(3.5F);
        setResistance(10.0F);
        setStepSound(Block.soundTypeMetal);
        setBlockName("adventurebackpack:blockDistillery");
        setCreativeTab(CreativeTabAB.ADVENTURE_BACKPACK_CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        front = register.registerIcon(ModInfo.MOD_ID + ":distillery_front");
        frontLit = register.registerIcon(ModInfo.MOD_ID + ":distillery_front_lit");
        side = register.registerIcon(ModInfo.MOD_ID + ":distillery_side");
        top = register.registerIcon(ModInfo.MOD_ID + ":distillery_top");
        bottom = register.registerIcon(ModInfo.MOD_ID + ":distillery_bottom");
        Icons.ethanolStill = register.registerIcon(ModInfo.MOD_ID + ":fluid.ethanol");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int face, int meta)
    {
        if (face == 1) return top;
        if (face == 0) return bottom;
        int facing = meta & 7;
        if (face == (facing < 2 ? 3 : facing)) return (meta & TileDistillery.LIT) != 0 ? frontLit : front;
        return side;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        return world.getBlock(x, y, z) == this && (world.getBlockMetadata(x, y, z) & TileDistillery.LIT) != 0 ? 13 : 0;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack)
    {
        int facing = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int[] meta = {2, 5, 3, 4};
        world.setBlockMetadataWithNotify(x, y, z, meta[facing], 2);
    }

    @Override
    public int damageDropped(int meta)
    {
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int face, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileDistillery)) return false;
        if (world.isRemote) return true;
        if (!ConfigHandler.DISTILLERY_ENABLED)
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.distillery.disabled"));
            return true;
        }
        TileDistillery distillery = (TileDistillery) te;
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && !player.isSneaking())
        {
            FluidStack inside = FluidContainerRegistry.getFluidForFilledItem(held);
            if (inside != null && inside.getFluid() == FluidRegistry.WATER)
            {
                if (distillery.fillByHand(inside.amount)) giveBack(player, held, FluidContainerRegistry.drainFluidContainer(held));
                return true;
            }
        }
        player.openGui(AdventureBackpack.instance, GuiHandler.DISTILLERY, world, x, y, z);
        return true;
    }

    private static void giveBack(EntityPlayer player, ItemStack held, ItemStack empty)
    {
        if (player.capabilities.isCreativeMode) return;
        if (--held.stackSize <= 0)
        {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, empty);
        } else if (empty != null && !player.inventory.addItemStackToInventory(empty))
        {
            player.dropPlayerItemWithRandomChoice(empty, false);
        }
        player.inventoryContainer.detectAndSendChanges();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileDistillery) ((TileDistillery) te).neighbourChanged();
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileDistillery) ((TileDistillery) te).wake();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & TileDistillery.LIT) == 0) return;
        int facing = meta & 7;
        double fx = x + 0.5, fy = y + 0.1 + random.nextFloat() * 0.3, fz = z + 0.5;
        double out = 0.52, along = random.nextFloat() * 0.6 - 0.3;
        switch (facing)
        {
            case 2:
                fz -= out;
                fx += along;
                break;
            case 3:
                fz += out;
                fx += along;
                break;
            case 4:
                fx -= out;
                fz += along;
                break;
            default:
                fx += out;
                fz += along;
                break;
        }
        world.spawnParticle("smoke", fx, fy, fz, 0, 0, 0);
        world.spawnParticle("flame", fx, fy, fz, 0, 0, 0);
        if (random.nextInt(3) == 0) world.spawnParticle("cloud", x + 0.5, y + 1.05, z + 0.5, 0, 0.02, 0);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileDistillery && !world.isRemote)
        {
            TileDistillery distillery = (TileDistillery) te;
            for (int i = 0; i < distillery.getSizeInventory(); i++)
            {
                ItemStack stack = distillery.getStackInSlot(i);
                if (stack == null) continue;
                world.spawnEntityInWorld(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack.copy()));
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileDistillery();
    }
}
