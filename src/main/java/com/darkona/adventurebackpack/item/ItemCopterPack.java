package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.block.BlockStation;
import com.darkona.adventurebackpack.client.models.ModelCopterPack;
import com.darkona.adventurebackpack.common.StationTransfer;
import com.darkona.adventurebackpack.common.Flight;
import com.darkona.adventurebackpack.common.FlightCost;
import com.darkona.adventurebackpack.client.ClientActions;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.TickThrottle;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.network.PlayerActionPacket;
import com.darkona.adventurebackpack.network.messages.EntityParticlePacket;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * Created on 31/12/2014
 *
 * @author Darkona
 */
public class ItemCopterPack extends ItemAB implements IBackWearableItem, IFuelPack
{

    public ItemCopterPack()
    {
        super();
        setUnlocalizedName("copterPack");
        setFull3D();
        setMaxStackSize(1);
    }

    /**
     * The copter pack is always registered (so worlds and servers keep their ids whatever mods are installed), but it
     * only works when it is enabled in the config and BuildCraft is loaded: its fuels come from BuildCraft. This class
     * does not touch any BuildCraft class, so it loads fine without the mod.
     */
    public static boolean isUsable()
    {
        return ConfigHandler.COPTERPACK_ENABLED && ConfigHandler.IS_BUILDCRAFT;
    }

    @Override
    public int fillFuel(ItemStack pack, FluidStack offered, boolean doFill)
    {
        if (!isUsable() || pack == null || !GeneralReference.isValidFuel(offered)) return 0;
        InventoryCopterPack inv = new InventoryCopterPack(pack);
        FluidStack stored = inv.getFuelTank().getFluid();
        int amount = StationTransfer.amount(offered.amount, inv.getFuelTank().getFluidAmount(), inv.getFuelTank().getCapacity(), true,
                StationTransfer.sameOrEmpty(stored == null ? null : stored.getFluid().getName(), offered.getFluid().getName()));
        if (amount > 0 && doFill)
        {
            FluidStack moved = offered.copy();
            moved.amount = amount;
            inv.getFuelTank().fill(moved, true);
            inv.dirtyTanks();
        }
        return amount;
    }

    /** Sneak + right click on a Fuel Station with the pack in hand reaches the station, which fills the pack. */
    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
    {
        return world.getBlock(x, y, z) instanceof BlockStation;
    }

    public static byte OFF_MODE = 0;
    public static byte NORMAL_MODE = 1;
    public static byte HOVER_MODE = 2;

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote && isUsable())
        {
            ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.COPTER_GUI, GUIPacket.FROM_HOLDING));
        }
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isCurrent)
    {
    }


    @SuppressWarnings(value = "unchecked")
    public void pushEntities(World world, EntityPlayer player, float speed)
    {
        double range = ConfigHandler.COPTER_PUSH_RANGE;
        if (range <= 0) return;
        double posX = player.posX;
        double posY = player.posY;
        double posZ = player.posZ;
        List<EntityItem> groundItems = world.getEntitiesWithinAABB(
                EntityItem.class,
                AxisAlignedBB.getBoundingBox(posX, posY, posZ,
                        posX + 1.0D, posY + 1.0D,
                        posZ + 1.0D).expand(range, range, range));

        for (EntityItem groundItem : groundItems)
        {
            if (!groundItem.isInWater())
            {
                if (groundItem.posX > posX)
                {
                    groundItem.motionX = speed;
                }
                if (groundItem.posX < posX)
                {
                    groundItem.motionX = -speed;
                }

                if (groundItem.posZ > posZ)
                {
                    groundItem.motionZ = speed;
                }
                if (groundItem.posZ < posZ)
                {
                    groundItem.motionZ = -speed;
                }

                if (groundItem.posY < posY)
                {
                    groundItem.motionY -= speed;
                }
            }
        }
    }


    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot)
    {
        return ModelCopterPack.instance.setWearable(stack);
    }


    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        String modelTexture;
        modelTexture = Resources.modelTextures("copterPack").toString();

        return modelTexture;
    }

    public static void elevate(EntityPlayer player, ItemStack copter)
    {
        player.motionY = Flight.liftSpeed(player.posY, player.motionY, ConfigHandler.COPTER_LIFT_SPEED, ConfigHandler.COPTER_MAX_ALTITUDE);
    }

    @Override
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack)
    {
        if (!isUsable()) return;
        // Switched off: nothing to fly, so no inventory is rebuilt from the stack's NBT every tick
        if (stack == null || stack.stackTagCompound == null || stack.stackTagCompound.getByte("status") == OFF_MODE) return;
        InventoryCopterPack inv = new InventoryCopterPack(Wearing.getWearingCopter(player));
        inv.openInventory();
        boolean canElevate = true;
        if (inv.getStatus() != OFF_MODE)
        {
            if (player.isInWater())
            {
                inv.setStatus(OFF_MODE);
                inv.dirtyStatus();
                if (!world.isRemote){
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.copterpack.cantwater"));
                }
                return;
            }
            if (inv.fuelTank.getFluidAmount() == 0)
            {
                canElevate = false;
                if (player.onGround)
                {
                    inv.setStatus(OFF_MODE);
                    inv.dirtyStatus();
                    if (!world.isRemote)
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.copterpack.off"));
                    }
                    return;
                    //TODO play "backpackOff" sound
                }
                if (inv.getStatus() == HOVER_MODE)
                {

                    inv.setStatus(NORMAL_MODE);
                    inv.dirtyStatus();
                    if (!world.isRemote)
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.copterpack.outoffuel"));
                    }
                    return;
                    //TODO play "outofFuel" sound
                }
            }
        }

        if (inv.getStatus() != OFF_MODE)
        {
            boolean hovering = false;
            if (inv.getStatus() == NORMAL_MODE)
            {
                if (!player.onGround && !player.isSneaking() && player.motionY < 0.0D)
                {
                    player.motionY = player.motionY * ConfigHandler.COPTER_GLIDE_FACTOR;
                }
                if (player.isSneaking())
                {
                    player.motionY = -ConfigHandler.COPTER_DESCEND_SPEED;
                }
            }

            if (inv.getStatus() == HOVER_MODE)
            {
                if (player.isSneaking())
                {
                    player.motionY = -ConfigHandler.COPTER_DESCEND_SPEED;
                }else{
                    hovering = true;
                    player.motionY = 0.0f;
                }
            }
            player.fallDistance = 0;

            //Smoke: every client draws it for the players it sees, from the synced status (no packet per tick)
            if (world.isRemote)
            {
                ClientActions.showParticlesAtEntity(player, EntityParticlePacket.COPTER_PARTICLE);
            }

            float factor = ConfigHandler.COPTER_AIR_CONTROL;
            // Airwave: item motion belongs to the server, and the item search runs once every few ticks
            boolean push = !world.isRemote && TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), ConfigHandler.COPTER_PUSH_INTERVAL_TICKS);
            if (!player.onGround)
            {
                if (push) pushEntities(world, player, 0.2f);
                //movement boost
                player.moveFlying(player.moveStrafing,player.moveForward,factor);
            } else
            {
                if (push) pushEntities(world, player, factor + 0.4f);
            }

            // Climbing: this client's own player reads the jump key and lifts itself; the server only learns when
            // climbing starts or stops (one packet per change), and charges the climb from that.
            boolean climbing = false;
            BackpackProperty props = BackpackProperty.get(player);
            if (world.isRemote)
            {
                if (ClientActions.isLocalPlayer(player))
                {
                    climbing = canElevate && ClientActions.isJumpKeyDown()
                            && inv.canConsumeFuel(FlightCost.copterPerTick(ConfigHandler.COPTER_FUEL_PER_TICK, hovering, ConfigHandler.COPTER_HOVER_EXTRA_FUEL, true, ConfigHandler.COPTER_CLIMB_EXTRA_FUEL));
                    if (climbing) elevate(player, stack);
                    if (props != null && props.isCopterClimbing() != climbing)
                    {
                        props.setCopterClimbing(climbing);
                        ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(climbing ? PlayerActionPacket.COPTER_CLIMBING : PlayerActionPacket.COPTER_NOT_CLIMBING));
                    }
                }
            } else
            {
                climbing = canElevate && props != null && props.isCopterClimbing();
            }

            int perTick = FlightCost.copterPerTick(ConfigHandler.COPTER_FUEL_PER_TICK, hovering, ConfigHandler.COPTER_HOVER_EXTRA_FUEL, climbing, ConfigHandler.COPTER_CLIMB_EXTRA_FUEL);
            double multiplier = 1.0;
            if (inv.fuelTank.getFluid() != null && GeneralReference.isValidFuel(inv.getFuelTank().getFluid().getFluid()))
            {
                multiplier = GeneralReference.liquidFuels.get(inv.getFuelTank().getFluid().getFluid().getName());
            }
            // Fuel is taken on a world-time beat instead of a counter saved in the stack every tick, so the stack's
            // NBT (and the sync to nearby players) only changes when fuel or status really change.
            if (TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), ConfigHandler.COPTER_FUEL_INTERVAL_TICKS))
            {
                inv.consumeFuel(FlightCost.copterFuelPerInterval(perTick, ConfigHandler.COPTER_FUEL_INTERVAL_TICKS, multiplier));
            }
        }
    }

    @Override
    public void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
    {
        onUnequipped(world,player,stack);
        player.dropPlayerItemWithRandomChoice(stack.copy(),false);
        BackpackProperty.get(player).setWearable(null);
    }

    @Override
    public void onEquipped(World world, EntityPlayer player, ItemStack stack)
    {
        if(!stack.hasTagCompound())stack.setTagCompound(new NBTTagCompound());
        stack.stackTagCompound.setByte("status",OFF_MODE);
    }

    @Override
    public void onUnequipped(World world, EntityPlayer player, ItemStack stack)
    {
        if (stack.stackTagCompound != null) stack.stackTagCompound.setByte("status",OFF_MODE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getWearableModel(ItemStack wearable)
    {
        return ClientProxy.modelCopterPack.setWearable(wearable);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearableTexture(ItemStack wearable)
    {
        return Resources.modelTextures("copterPack");
    }


}
