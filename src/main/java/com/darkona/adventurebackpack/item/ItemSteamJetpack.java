package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.block.BlockGear;
import adventurebackpack.api.IBackWearableItem;
import adventurebackpack.api.IInventoryTanks;
import com.darkona.adventurebackpack.common.Flight;
import com.darkona.adventurebackpack.common.JetpackHeat;
import com.darkona.adventurebackpack.common.RenderInfo;
import com.darkona.adventurebackpack.common.RenderInfoCache;
import com.darkona.adventurebackpack.util.TickThrottle;
import com.darkona.adventurebackpack.client.ClientActions;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModFluids;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.network.PlayerActionPacket;
import com.darkona.adventurebackpack.network.messages.EntityParticlePacket;
import com.darkona.adventurebackpack.network.messages.EntitySoundPacket;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ItemSteamJetpack extends ItemAB implements IBackWearableItem
{
    public static byte OFF_MODE = 0;
    public static byte NORMAL_MODE = 1;

    public ItemSteamJetpack()
    {
        super();
        setUnlocalizedName("steamJetpack");
        setFull3D();
        setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return BlockGear.useGearOn(stack, player, world, x, y, z, side);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote && ConfigHandler.STEAMJETPACK_ENABLED)
        {
            ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.JETPACK_GUI, GUIPacket.FROM_HOLDING));
        }
        return stack;
    }

    private void runFirebox(InventorySteamJetpack inv)
    {
        if (inv.getBurnTicks() <= 0)
        {
            inv.setBurnTicks(inv.consumeFuel());
            inv.currentItemBurnTime = inv.getBurnTicks();
        }
    }

    private void runHeater(InventorySteamJetpack inv, World world, EntityPlayer player, boolean thrusting)
    {
        int temperature = inv.getTemperature();
        int burnTicks = inv.getBurnTicks() - JetpackHeat.burnPerTick(thrusting, ConfigHandler.JETPACK_THRUST_FUEL_BURN);
        int coolTicks = inv.getCoolTicks() - 1;
        long now = world.getTotalWorldTime();
        if (burnTicks > 0)
        {
            if (temperature < InventorySteamJetpack.MAX_TEMPERATURE
                    && TickThrottle.every(now, player.getEntityId(), JetpackHeat.scaled(JetpackHeat.heatingTicks(temperature), ConfigHandler.JETPACK_HEATING_SPEED)))
            {
                ++temperature;
                coolTicks = coolTicks < 5000 ? coolTicks + 100 : coolTicks;
            }
        } else
        {
            inv.currentItemBurnTime = 0;
            if (TickThrottle.every(now, player.getEntityId(), JetpackHeat.scaled(JetpackHeat.coolingTicks(temperature), ConfigHandler.JETPACK_COOLING_SPEED)))
            {
                temperature = JetpackHeat.coolDown(temperature, getBiomeMinTemp(player, world));
            }
        }
        if (thrusting && ConfigHandler.JETPACK_THRUST_HEAT_LOSS_TICKS > 0
                && TickThrottle.every(now, player.getEntityId(), ConfigHandler.JETPACK_THRUST_HEAT_LOSS_TICKS))
        {
            temperature = JetpackHeat.coolDown(temperature, getBiomeMinTemp(player, world));
        }
        inv.setTemperature(temperature);
        inv.setCoolTicks(coolTicks);
        inv.setBurnTicks(burnTicks <= 0 ? 0 : burnTicks);
    }

    private int getBiomeMinTemp(EntityPlayer player, World world)
    {
        BiomeDictionary.Type[] thisBiomeTypes = BiomeDictionary.getTypesForBiome(world.getBiomeGenForCoords((int) player.posX, (int) player.posZ));
        for (BiomeDictionary.Type type : thisBiomeTypes)
        {
            if (type == BiomeDictionary.Type.COLD || type == BiomeDictionary.Type.SNOWY)
            {
                return 0;
            }
            if (type == BiomeDictionary.Type.HOT || type == BiomeDictionary.Type.BEACH)
            {
                return 30;
            }
            if (type == BiomeDictionary.Type.NETHER)
            {
                return 40;
            }
        }
        return 25;
    }

    private static FluidStack waterIntake;

    @Override
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack)
    {
        if (!ConfigHandler.STEAMJETPACK_ENABLED) return;
        if (world.isRemote)
        {
            clientUpdate(world, player, stack);
            return;
        }
        InventorySteamJetpack inv = liveInventory(player, stack);
        int steamConsumed = ConfigHandler.JETPACK_STEAM_PER_TICK;
        boolean canUse = inv.getSteamTank().getFluid() != null;

        if (inv.getStatus())
        {
            runFirebox(inv);
        }
        runHeater(inv, world, player, inv.isInUse() && canUse);
        runBoiler(inv, world, player);

        if(player.isInWater())
        {
            if (waterIntake == null) waterIntake = new FluidStack(FluidRegistry.WATER, 0);
            waterIntake.amount = ConfigHandler.JETPACK_WATER_INTAKE;
            inv.getWaterTank().fill(waterIntake, true);
        }

        if (inv.isInUse() && canUse)
        {
            elevate(player);
            inv.getSteamTank().drain(steamConsumed, true);
            if (inv.getSteamTank().getFluidAmount() == 0)
            {
                inv.setInUse(false);
            }
            player.moveFlying(player.moveStrafing, player.moveForward, ConfigHandler.JETPACK_AIR_CONTROL);
            if(player.fallDistance > 1)
            {
                player.fallDistance -= 1;
            }
            if(player.motionY >= 0)
            {
                player.fallDistance = 0;
            }
        }
        inv.flushChanges();
    }

    public static InventorySteamJetpack liveInventory(EntityPlayer player, ItemStack stack)
    {
        BackpackProperty props = BackpackProperty.get(player);
        IInventoryTanks live = props == null ? null : props.getLiveGear();
        if (live instanceof InventorySteamJetpack && ((InventorySteamJetpack) live).isCurrentFor(stack))
        {
            return (InventorySteamJetpack) live;
        }
        InventorySteamJetpack inv = new InventorySteamJetpack(stack);
        if (props != null && props.getWearable() == stack) props.setLiveGear(inv);
        return inv;
    }

    private void clientUpdate(World world, EntityPlayer player, ItemStack stack)
    {
        RenderInfo info = RenderInfoCache.get(stack);
        boolean canUse = info.steamFluid != null;
        boolean inUse = info.jetpackInUse;

        if (ClientActions.isLocalPlayer(player))
        {
            if (info.jetpackOn && canUse && ClientActions.isJumpKeyDown())
            {
                if (!inUse)
                {
                    inUse = true;
                    writeInUse(stack, true);
                    ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(PlayerActionPacket.JETPACK_IN_USE));
                    ModNetwork.net.sendToServer(new EntitySoundPacket.Message(EntitySoundPacket.JETPACK_FIZZ, player));
                }
            } else if (inUse)
            {
                inUse = false;
                writeInUse(stack, false);
                ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(PlayerActionPacket.JETPACK_NOT_IN_USE));
            }
        }

        if (inUse && canUse)
        {
            elevate(player);
            player.moveFlying(player.moveStrafing, player.moveForward, ConfigHandler.JETPACK_AIR_CONTROL);
            if (player.fallDistance > 1)
            {
                player.fallDistance -= 1;
            }
            if (player.motionY >= 0)
            {
                player.fallDistance = 0;
            }
            ClientActions.showParticlesAtEntity(player, EntityParticlePacket.JETPACK_PARTICLE);
        }
    }

    private static void writeInUse(ItemStack stack, boolean inUse)
    {
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("jetpackData"))
        {
            stack.stackTagCompound.getCompoundTag("jetpackData").setBoolean("inUse", inUse);
        }
    }

    private void runBoiler(InventorySteamJetpack inv, World world, EntityPlayer player)
    {
        int temperature = inv.getTemperature();
        boolean mustSSSSS = !inv.isLeaking();
        boolean mustBlublub = !inv.isBoiling();
        boolean boiling = inv.isBoiling();
        boolean leaking = inv.isLeaking();

        if (temperature >= ConfigHandler.JETPACK_BOILING_TEMPERATURE && inv.getWaterTank().getFluidAmount() > 0)
        {
            if(!boiling)boiling = true;

            if (!world.isRemote && mustBlublub)
            {
                ModNetwork.net.sendTo(new EntitySoundPacket.Message(EntitySoundPacket.BOILING_BUBBLES, player), (EntityPlayerMP) player);
            }
        } else
        {
            if (boiling)
            {
                boiling = false;
            }
        }

        if (boiling)
        {
            if (inv.getSteamTank().getFluidAmount() < inv.getSteamTank().getCapacity())
            {
                Fluid steamFluid = ModFluids.steam;
                if (steamFluid != null && inv.getWaterTank().getFluid() != null)
                {
                    int steam = inv.getWaterTank().drain((temperature / ConfigHandler.JETPACK_BOILING_TEMPERATURE), true).amount;
                    inv.getSteamTank().fill(new FluidStack(steamFluid, steam * ConfigHandler.JETPACK_STEAM_PER_WATER), true);
                }
            }
        }

        if (inv.getSteamTank().getFluidAmount() < inv.getSteamTank().getCapacity() - 100)
        {
            if (leaking)
            {
                leaking = false;
            }
        } else
        {
            if (!leaking)
            {
                leaking = true;
                if (!world.isRemote && mustSSSSS)
                {
                    ModNetwork.net.sendTo(new EntitySoundPacket.Message(EntitySoundPacket.LEAKING_STEAM, player), (EntityPlayerMP) player);
                }
            }
        }
        inv.setBoiling(boiling);
        inv.setLeaking(leaking);
        inv.setTemperature(temperature);
    }

    public static void elevate(EntityPlayer player)
    {
        player.motionY = Flight.thrustSpeed(player.posY, player.motionY, ConfigHandler.JETPACK_THRUST, ConfigHandler.JETPACK_MAX_RISE_SPEED, ConfigHandler.JETPACK_FALL_BRAKE, ConfigHandler.JETPACK_MAX_ALTITUDE);
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
        InventorySteamJetpack inv = new InventorySteamJetpack(stack);
        inv.calculateLostTime();
        if(inv.getTemperature() == 0)inv.setTemperature(getBiomeMinTemp(player, world));
    }

    @Override
    public void onUnequipped(World world, EntityPlayer player, ItemStack stack)
    {
        InventorySteamJetpack inv = world.isRemote ? new InventorySteamJetpack(stack) : liveInventory(player, stack);
        inv.setBoiling(false);
        inv.setInUse(false);
        inv.setLeaking(false);
        inv.setStatus(false);
        inv.setSystemTime(System.currentTimeMillis());
        inv.markDirty();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getWearableModel(ItemStack wearable)
    {
        return ClientProxy.modelSteamJetpack.setWearable(wearable);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearableTexture(ItemStack wearable)
    {
        return Resources.modelTextures("steamJetpack");
    }
}
