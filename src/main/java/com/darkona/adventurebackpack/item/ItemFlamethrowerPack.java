package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.block.BlockGear;
import adventurebackpack.api.IFuelPack;
import adventurebackpack.api.IBackWearableItem;
import com.darkona.adventurebackpack.block.BlockStation;
import com.darkona.adventurebackpack.client.ClientActions;
import com.darkona.adventurebackpack.common.FlamethrowerLogic;
import com.darkona.adventurebackpack.common.ParticleBeat;
import com.darkona.adventurebackpack.common.StationTransfer;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryFlamethrowerPack;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.TickThrottle;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ItemFlamethrowerPack extends ItemAB implements IBackWearableItem, IFuelPack
{
    private static final int PILOT_BEAT_TICKS = 1200;
    private static boolean fuelPresent = false;

    public ItemFlamethrowerPack()
    {
        super();
        setUnlocalizedName("flamethrowerPack");
        setFull3D();
        setMaxStackSize(1);
    }

    public static void detectFuels()
    {
        fuelPresent = false;
        for (String name : GeneralReference.liquidFuels.keySet())
        {
            if (FluidRegistry.isFluidRegistered(name))
            {
                fuelPresent = true;
                break;
            }
        }
        LogHelper.info("Flamethrower: " + (fuelPresent ? "a fuel of fuels.list is present." : "no fuel of fuels.list is registered; it stays hidden."));
    }

    public static boolean isUsable()
    {
        return ConfigHandler.FLAMETHROWER_ENABLED && fuelPresent;
    }

    @Override
    public int fillFuel(ItemStack pack, FluidStack offered, boolean doFill)
    {
        if (!isUsable() || pack == null || !GeneralReference.isValidFuel(offered)) return 0;
        InventoryFlamethrowerPack inv = new InventoryFlamethrowerPack(pack);
        Fluid stored = inv.getFuelFluid();
        int amount = StationTransfer.amount(offered.amount, inv.getFuel(), inv.getCapacity(), true,
                StationTransfer.sameOrEmpty(stored == null ? null : stored.getName(), offered.getFluid().getName()));
        if (amount > 0 && doFill)
        {
            FluidStack moved = offered.copy();
            moved.amount = amount;
            inv.fill(moved, true);
            inv.markDirty();
        }
        return amount;
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
    {
        return world.getBlock(x, y, z) instanceof BlockStation;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return BlockGear.useGearOn(stack, player, world, x, y, z, side);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote && isUsable())
        {
            ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.FLAMETHROWER_GUI, GUIPacket.FROM_HOLDING));
        }
        return stack;
    }

    @Override
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack)
    {
        if (world.isRemote)
        {
            if (isUsable()) ClientActions.flamethrowerSounds(player, stack);
            if (player.getHeldItem() == null || !(player.getHeldItem().getItem() instanceof ItemFlamethrower)) return;
            if (!ParticleBeat.pilot(world.getTotalWorldTime(), player.getEntityId())) return;
            if (isUsable() && InventoryFlamethrowerPack.isLit(stack)) pilotFlame(world, player);
            return;
        }
        if (ConfigHandler.FLAMETHROWER_PILOT_FUEL_PER_MINUTE <= 0) return;
        if (!TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), PILOT_BEAT_TICKS)) return;
        if (!isUsable() || !InventoryFlamethrowerPack.isLit(stack)) return;
        InventoryFlamethrowerPack inv = new InventoryFlamethrowerPack(stack);
        int cost = FlamethrowerLogic.pilotFuelPerBeat(ConfigHandler.FLAMETHROWER_PILOT_FUEL_PER_MINUTE, GeneralReference.fuelMultiplier(inv.getFuelFluid()));
        inv.takeFuel(FlamethrowerLogic.fuelTaken(inv.getFuel(), cost));
        if (inv.getFuel() <= 0) putOut(player, inv, "adventurebackpack:messages.flamethrower.nofuel");
        inv.markDirty();
    }

    static void putOut(EntityPlayer player, InventoryFlamethrowerPack inv, String message)
    {
        inv.setOn(false);
        player.addChatComponentMessage(new ChatComponentTranslation(message));
    }

    private static void pilotFlame(World world, EntityPlayer player)
    {
        Vec3 at = ItemFlamethrower.nozzle(player);
        world.spawnParticle("flame", at.xCoord, at.yCoord, at.zCoord, 0.0, 0.0, 0.0);
    }

    @Override
    public void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
    {
        onUnequipped(world, player, stack);
        player.dropPlayerItemWithRandomChoice(stack.copy(), false);
        BackpackProperty.get(player).setWearable(null);
    }

    @Override
    public void onEquipped(World world, EntityPlayer player, ItemStack stack)
    {
        putOutQuietly(stack);
    }

    @Override
    public void onUnequipped(World world, EntityPlayer player, ItemStack stack)
    {
        putOutQuietly(stack);
    }

    private static void putOutQuietly(ItemStack stack)
    {
        if (stack == null || !InventoryFlamethrowerPack.isLit(stack)) return;
        InventoryFlamethrowerPack inv = new InventoryFlamethrowerPack(stack);
        inv.setOn(false);
        inv.markDirty();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getWearableModel(ItemStack wearable)
    {
        return ClientProxy.modelFlamethrowerPack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearableTexture(ItemStack wearable)
    {
        return Resources.modelTextures("flamethrowerPack");
    }
}
