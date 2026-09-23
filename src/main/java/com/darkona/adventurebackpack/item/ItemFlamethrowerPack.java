package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.block.BlockStation;
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

/**
 * The Flamethrower Pack: two fuel tanks on the back and a pyro mask, one wearable in the back slot (it cannot be worn
 * with a backpack, copter pack, jetpack or scuba gear). It does nothing on its own: Sneak + the toggle key lights its
 * pilot, and the Flamethrower nozzle held in hand fires from its tanks. The Fuel Station fills it ({@link IFuelPack}).
 * <p>
 * Tick cost (performance contract, Tier 3): with the default free pilot one config read and a return on the server;
 * on the client a held-item check, and the stack's tag only on the pilot flame's beat.
 */
public class ItemFlamethrowerPack extends ItemAB implements IBackWearableItem, IFuelPack
{
    /** Ticks between two charges of the pilot flame (pilotFuelPerMinute). */
    private static final int PILOT_BEAT_TICKS = 1200;
    /** Set in postInit: some fuel of fuels.list is a registered fluid, so the tanks can ever be filled. */
    private static boolean fuelPresent = false;

    public ItemFlamethrowerPack()
    {
        super();
        setUnlocalizedName("flamethrowerPack");
        setFull3D();
        setMaxStackSize(1);
    }

    /** Called once in postInit, after every mod registered its fluids (Tier 1). */
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

    /** True when the Flamethrower works at all: it is enabled and some fuel exists. */
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

    /** Sneak + right click on a Fuel Station with the pack in hand reaches the station, which fills the pack. */
    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
    {
        return world.getBlock(x, y, z) instanceof BlockStation;
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
            // the pilot flame at the nozzle, for every player this client sees with the nozzle in hand
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

    /** Puts the pilot out (does not save) and tells the wearer why. Server side. */
    static void putOut(EntityPlayer player, InventoryFlamethrowerPack inv, String message)
    {
        inv.setOn(false);
        player.addChatComponentMessage(new ChatComponentTranslation(message));
    }

    /** A small still flame at the nozzle (client). */
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

    /** Taking the pack off (or dying with it) puts the pilot out. */
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
