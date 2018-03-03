package com.darkona.adventurebackpack.item;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.CreativeTabAB;
import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.fluids.FluidEffectRegistry;
import com.darkona.adventurebackpack.init.ModFluids;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.TipUtils;
import com.darkona.adventurebackpack.util.Wearing;

import static com.darkona.adventurebackpack.common.Constants.BUCKET;
import static com.darkona.adventurebackpack.util.TipUtils.l10n;

/**
 * Created by Darkona on 12/10/2014.
 */
public class ItemHose extends ItemAB
{
    private IIcon drinkIcon;
    private IIcon spillIcon;
    private IIcon suckIcon;
    private static final byte HOSE_SUCK_MODE = 0;
    private static final byte HOSE_SPILL_MODE = 1;
    private static final byte HOSE_DRINK_MODE = 2;

    public ItemHose()
    {
        super();
        setMaxStackSize(1);
        setFull3D();
        setNoRepair();
        setUnlocalizedName("backpackHose");
        setCreativeTab(CreativeTabAB.TAB_AB);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltips, boolean advanced)
    {
        if (GuiScreen.isCtrlKeyDown())
        {
            tooltips.add(l10n("hose.key.header"));
            tooltips.add("- " + TipUtils.pressKeyFormat(TipUtils.actionKeyFormat()) + l10n("hose.key.tank"));
            tooltips.add("- " + TipUtils.pressShiftKeyFormat(TipUtils.whiteFormat(l10n("mouse.wheel"))) + l10n("hose.key.mode"));
            tooltips.add("");
            tooltips.add(l10n("hose.dump1"));
            tooltips.add(l10n("hose.dump2"));
            tooltips.add(EnumChatFormatting.RED.toString() + l10n("hose.dump.warn"));
        }
        else
        {
            tooltips.add(TipUtils.holdCtrl());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass)
    {
        switch (getHoseMode(stack))
        {
            case HOSE_SUCK_MODE:
                return suckIcon;
            case HOSE_SPILL_MODE:
                return spillIcon;
            case HOSE_DRINK_MODE:
                return drinkIcon;
            default:
                return itemIcon;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1)
    {
        return itemIcon;
    }

    public static int getHoseMode(ItemStack hose)
    {
        return hose.stackTagCompound != null ? hose.stackTagCompound.getInteger("mode") : -1;
    }

    public static int getHoseTank(ItemStack hose)
    {
        return hose.hasTagCompound() ? hose.getTagCompound().getInteger("tank") : -1;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("mode"))
        {
            return (stack.stackTagCompound.getInteger("mode") == 2) ? EnumAction.drink : EnumAction.none;
        }
        return EnumAction.none;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        String name = "hose_" + (getHoseTank(stack) == 0 ? "leftTank" : getHoseTank(stack) == 1 ? "rightTank" : "");
        switch (getHoseMode(stack))
        {
            case 0:
                return super.getUnlocalizedName(name + "_suck");
            case 1:
                return super.getUnlocalizedName(name + "_spill");
            case 2:
                return super.getUnlocalizedName(name + "_drink");
            default:
                return super.getUnlocalizedName("hoseUseless");
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 24;
    }

    @Override
    public int getMaxDamage()
    {
        return 0;
    }

    @Override
    public int getMaxDamage(ItemStack stack)
    {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        drinkIcon = iconRegister.registerIcon(Resources.getIconString("hoseDrink"));
        spillIcon = iconRegister.registerIcon(Resources.getIconString("hoseSpill"));
        suckIcon = iconRegister.registerIcon(Resources.getIconString("hoseSuck"));
        itemIcon = iconRegister.registerIcon(Resources.getIconString("hoseLeft"));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int inv_slot, boolean isCurrent)
    {
        if (entity == null || !(entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entity;
        if (world.isRemote && player.getItemInUse() != null && player.getItemInUse().getItem().equals(this)) return;

        NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        ItemStack backpack = Wearing.getWearingBackpack(player);
        if (backpack != null)
        {
            if (nbt.getInteger("tank") == -1) nbt.setInteger("tank", 0);
            if (nbt.getInteger("mode") == -1) nbt.setInteger("mode", 0);
            InventoryBackpack inv = new InventoryBackpack(backpack);
            FluidTank tank = nbt.getInteger("tank") == 0 ? inv.getLeftTank() : inv.getRightTank();
            if (tank != null && tank.getFluid() != null)
            {
                nbt.setString("fluid", WordUtils.capitalize(tank.getFluid().getFluid().getName()));
                nbt.setInteger("amount", tank.getFluidAmount());
            }
            else
            {
                nbt.setInteger("amount", 0);
                nbt.setString("fluid", "Empty");
            }
        }
        else
        {
            nbt.setInteger("amount", 0);
            nbt.setString("fluid", "None");
            nbt.setInteger("mode", -1);
            nbt.setInteger("tank", -1);
        }
        stack.setTagCompound(nbt);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (!Wearing.isWearingBackpack(player)) return true;

        InventoryBackpack inv = new InventoryBackpack(Wearing.getWearingBackpack(player));
        inv.openInventory();
        FluidTank tank = getHoseTank(stack) == 0 ? inv.getLeftTank() : inv.getRightTank();
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof IFluidHandler)
        {
            IFluidHandler exTank = (IFluidHandler) te;
            int accepted;
            switch (getHoseMode(stack))
            {
                case HOSE_SUCK_MODE:

                    accepted = tank.fill(exTank.drain(ForgeDirection.UNKNOWN, BUCKET, false), false);
                    if (accepted > 0)
                    {
                        tank.fill(exTank.drain(ForgeDirection.UNKNOWN, accepted, true), true);
                        te.markDirty();
                        inv.dirtyTanks();
                        return true;
                    }
                    break;

                case HOSE_SPILL_MODE:

                    accepted = exTank.fill(ForgeDirection.UNKNOWN, tank.drain(BUCKET, false), false);
                    if (accepted > 0)
                    {
                        exTank.fill(ForgeDirection.UNKNOWN, tank.drain(accepted, true), true);
                        te.markDirty();
                        inv.dirtyTanks();
                        return true;
                    }
                    break;
            }
        }
        return false;

    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!Wearing.isWearingBackpack(player)) return stack;
        InventoryBackpack inv = new InventoryBackpack(Wearing.getWearingBackpack(player));
        inv.openInventory();
        MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
        FluidTank tank = getHoseTank(stack) == 0 ? inv.getLeftTank() : inv.getRightTank();
        if (tank != null)
        {
            switch (getHoseMode(stack))
            {
                case HOSE_SUCK_MODE: // If it's in Suck Mode

                    if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                    {
                        if (!player.canPlayerEdit(mop.blockX, mop.blockY, mop.blockZ, mop.sideHit, null))
                        {
                            return stack;
                        }
                        //TODO adjust for Adventure Mode
                        Fluid fluidBlock = FluidRegistry.lookupFluidForBlock(world.getBlock(mop.blockX, mop.blockY, mop.blockZ));
                        if (fluidBlock != null)
                        {
                            FluidStack fluid = new FluidStack(fluidBlock, BUCKET);
                            if (tank.getFluid() == null || tank.getFluid().containsFluid(fluid))
                            {
                                int accepted = tank.fill(fluid, false);
                                if (accepted > 0)
                                {
                                    world.setBlockToAir(mop.blockX, mop.blockY, mop.blockZ);
                                    tank.fill(new FluidStack(fluidBlock, accepted), true);
                                }
                            }
                        }
                        inv.dirtyTanks();
                    }
                    break;

                case HOSE_SPILL_MODE: // If it's in Spill Mode
                    if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                    {
                        int x = mop.blockX;
                        int y = mop.blockY;
                        int z = mop.blockZ;
                        if (world.getBlock(x, y, z).isBlockSolid(world, x, y, z, mop.sideHit))
                        {

                            switch (mop.sideHit)
                            {
                                case 0:
                                    --y;
                                    break;
                                case 1:
                                    ++y;
                                    break;
                                case 2:
                                    --z;
                                    break;
                                case 3:
                                    ++z;
                                    break;
                                case 4:
                                    --x;
                                    break;
                                case 5:
                                    ++x;
                                    break;
                            }
                        }
                        if (tank.getFluidAmount() > 0)
                        {
                            FluidStack fluid = tank.getFluid();
                            if (fluid != null)
                            {
                                if (fluid.getFluid().canBePlacedInWorld())
                                {
                                    Material material = world.getBlock(x, y, z).getMaterial();
                                    boolean flag = !material.isSolid();
                                    if (!world.isAirBlock(x, y, z) && !flag)
                                    {
                                        return stack;
                                    }
                                    /* IN HELL DIMENSION No, I won't let you put water in the nether. You freak*/
                                    if (world.provider.isHellWorld && fluid.getFluid() == FluidRegistry.WATER)
                                    {
                                        tank.drain(BUCKET, true);
                                        world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
                                        for (int l = 0; l < 12; ++l)
                                        {
                                            world.spawnParticle("largesmoke", x + Math.random(), y + Math.random(), z + Math.random(), 0.0D, 0.0D, 0.0D);
                                        }
                                    }
                                    else
                                    {
                                        /* NOT IN HELL DIMENSION. */
                                        FluidStack drainedFluid = tank.drain(BUCKET, false);
                                        if (drainedFluid != null && drainedFluid.amount >= BUCKET)
                                        {
                                            if (!world.isRemote && flag && !material.isLiquid())
                                            {
                                                world.func_147480_a(x, y, z, true);
                                            }

                                            if (fluid.getFluid().getBlock() == Blocks.water)
                                            {
                                                if (world.setBlock(x, y, z, Blocks.flowing_water, 0, 3))
                                                {
                                                    tank.drain(BUCKET, true);
                                                }
                                            }
                                            else if (fluid.getFluid().getBlock() == Blocks.lava)
                                            {
                                                if (world.setBlock(x, y, z, Blocks.flowing_lava, 0, 3))
                                                {
                                                    tank.drain(BUCKET, true);
                                                }
                                            }
                                            else if (world.setBlock(x, y, z, fluid.getFluid().getBlock(), 0, 3))
                                            {
                                                tank.drain(BUCKET, true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        inv.dirtyTanks();
                    }
                    break;
                case HOSE_DRINK_MODE:
                    if (tank.getFluid() != null && tank.getFluidAmount() >= BUCKET)
                    {
                        if (FluidEffectRegistry.hasFluidEffect(tank.getFluid().getFluid()))
                        {
                            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
                        }
                    }
                    break;
                default:
                    return stack;
            }
        }
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack hose, World world, EntityPlayer player)
    {
        if (!Wearing.isWearingBackpack(player)) return hose;
        int mode = -1;
        int tank = -1;
        if (hose.stackTagCompound != null)
        {
            tank = getHoseTank(hose);
            mode = getHoseMode(hose);
        }
        if (mode == HOSE_DRINK_MODE && tank > -1)
        {
            InventoryBackpack inv = new InventoryBackpack(Wearing.getWearingBackpack(player));
            inv.openInventory();
            FluidTank backpackTank = (tank == 0) ? inv.getLeftTank() : (tank == 1) ? inv.getRightTank() : null;
            if (backpackTank != null)
            {
                if (ServerActions.setFluidEffect(world, player, backpackTank))
                {
                    backpackTank.drain(BUCKET, true);
                    inv.dirtyTanks();
                }
            }
        }
        return hose;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity)
    {
        if (!Wearing.isWearingBackpack(player)) return false;
        InventoryBackpack inventory = new InventoryBackpack(Wearing.getWearingBackpack(player));
        inventory.openInventory();
        if (entity instanceof EntityCow && !(entity instanceof EntityMooshroom))
        {

            FluidTank tank = getHoseTank(stack) == 0 ? inventory.getLeftTank() : inventory.getRightTank();
            tank.fill(new FluidStack(ModFluids.milk, BUCKET), true);
            inventory.dirtyTanks();

            ((EntityCow) entity).faceEntity(player, 0.1f, 0.1f);
            return true;
        }
        if (entity instanceof EntityMooshroom)
        {
            FluidTank tank = getHoseTank(stack) == 0 ? inventory.getLeftTank() : inventory.getRightTank();
            tank.fill(new FluidStack(ModFluids.mushroomStew, BUCKET), true);
            inventory.dirtyTanks();

            ((EntityMooshroom) entity).faceEntity(player, 0.1f, 0.1f);
            return true;
        }

        return false;
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack stack)
    {
        return FluidRegistry.lookupFluidForBlock(block) != null;
    }

}
