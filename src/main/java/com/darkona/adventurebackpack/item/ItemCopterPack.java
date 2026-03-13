package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.client.models.ModelCopterPack;
import com.darkona.adventurebackpack.common.Flight;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.network.messages.EntityParticlePacket;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
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

import java.util.List;

/**
 * Created on 31/12/2014
 *
 * @author Darkona
 */
public class ItemCopterPack extends ItemAB implements IBackWearableItem
{

    public ItemCopterPack()
    {
        super();
        setUnlocalizedName("copterPack");
        setFull3D();
        setMaxStackSize(1);
    }

    public static byte OFF_MODE = 0;
    public static byte NORMAL_MODE = 1;
    public static byte HOVER_MODE = 2;

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote && ConfigHandler.COPTERPACK_ENABLED)
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
        if (!ConfigHandler.COPTERPACK_ENABLED) return;
        InventoryCopterPack inv = new InventoryCopterPack(Wearing.getWearingCopter(player));
        inv.openInventory();
        boolean canElevate = true;
        int fuelConsumption = 0;
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
            fuelConsumption += ConfigHandler.COPTER_FUEL_PER_TICK;
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
                    fuelConsumption += ConfigHandler.COPTER_HOVER_EXTRA_FUEL;
                    player.motionY = 0.0f;
                }
            }
            player.fallDistance = 0;

            //Smoke
            if (!world.isRemote)
            {
                ModNetwork.sendToNearby(new EntityParticlePacket.Message(EntityParticlePacket.COPTER_PARTICLE, player), player);
            }
            //Sound


            float factor = ConfigHandler.COPTER_AIR_CONTROL;
            if (!player.onGround)
            {
                //Airwave
                pushEntities(world, player, 0.2f);
                //movement boost
                player.moveFlying(player.moveStrafing,player.moveForward,factor);
            } else
            {
                pushEntities(world, player, factor + 0.4f);
            }

            //Elevation clientside
            if (world.isRemote)
            {
                if (Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed())
                {
                    if (inv.canConsumeFuel(fuelConsumption + ConfigHandler.COPTER_CLIMB_EXTRA_FUEL) && canElevate)
                    {
                        elevate(player, stack);
                    }
                }
            }

            //Elevation serverside
            if (!player.onGround && player.motionY > 0)
            {
                fuelConsumption += ConfigHandler.COPTER_CLIMB_EXTRA_FUEL;
            }
            int ticks = inv.tickCounter - 1;
            if (inv.fuelTank.getFluid() != null)
            {
                if(GeneralReference.isValidFuel(inv.getFuelTank().getFluid().getFluid()))
                {
                    fuelConsumption = (int)Math.floor(fuelConsumption * GeneralReference.liquidFuels.get(inv.getFuelTank().getFluid().getFluid().getName()));
                }
            }
            if (ticks <= 0)
            {
                inv.tickCounter = ConfigHandler.COPTER_FUEL_INTERVAL_TICKS;
                inv.consumeFuel(fuelConsumption);
                inv.dirtyTanks();
            } else
            {
                inv.tickCounter = ticks;
            }
        }
       // if(!world.isRemote)inv.closeInventory();
        inv.closeInventory();
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
        stack.stackTagCompound.setByte("status",OFF_MODE);
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
