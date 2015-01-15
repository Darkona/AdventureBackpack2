package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.client.models.ModelCopterPack;
import com.darkona.adventurebackpack.common.BackpackProperty;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.network.messages.PlayerParticlePacket;
import com.darkona.adventurebackpack.util.FluidUtils;
import com.darkona.adventurebackpack.util.Resources;
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
import net.minecraft.util.ChatComponentText;
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
        if (world.isRemote)
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
        double posX = player.posX;
        double posY = player.posY;
        double posZ = player.posZ;

        List<EntityItem> groundItems = world.getEntitiesWithinAABB(
                EntityItem.class,
                AxisAlignedBB.getBoundingBox(posX, posY, posZ,
                        posX + 1.0D, posY + 1.0D,
                        posZ + 1.0D).expand(4.0D, 4.0D, 4.0D));

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
        return ModelCopterPack.instance.setCopter(stack);
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
        /*double currentAccel = player.motionY < 0.4D ? player.motionY : 0.15;

        if(copter.hasTagCompound() && copter.stackTagCompound.hasKey("status"))
        {
            byte status = copter.stackTagCompound.getByte("status");
            if((status != OFF_MODE))
            {*/
        if (player.posY < 100) player.motionY = Math.max(player.motionY, 0.15);
        if (player.posY > 100) player.motionY = 0.15 - ((player.posY % 100) / 100);
     /*       }
        }*/

    }

    long soundLoopTime = 0L;
    @Override
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack)
    {
        if (player == null || stack == null) return;
        if(!stack.hasTagCompound())stack.setTagCompound(new NBTTagCompound());
        if(!stack.stackTagCompound.hasKey("status"))stack.stackTagCompound.setByte("status",OFF_MODE);
        InventoryCopterPack inv = new InventoryCopterPack(stack);
        boolean canElevate = true;
        int fuelConsumption = 0;
        byte status = stack.stackTagCompound.getByte("status");
        if (status != OFF_MODE)
        {
            if (player.isInWater())
            {
                stack.stackTagCompound.setByte("status", OFF_MODE);
                status = OFF_MODE;
                player.addChatComponentMessage(new ChatComponentText("CopterPack: can't function in water."));
                return;
            }
            if (inv.fuelTank.getFluidAmount() == 0)
            {
                canElevate = false;
                if (player.onGround)
                {
                    stack.stackTagCompound.setByte("status", OFF_MODE);
                    status = OFF_MODE;
                    if (!world.isRemote)
                    {
                        player.addChatComponentMessage(new ChatComponentText("CopterPack: out of fuel, shutting off"));
                    }
                    return;
                    //TODO play "backpackOff" sound
                }
                if (status == HOVER_MODE)
                {

                    stack.stackTagCompound.setByte("status", NORMAL_MODE);
                    status = NORMAL_MODE;
                    if (!world.isRemote)
                    {
                        player.addChatComponentMessage(new ChatComponentText("CopterPack: out of fuel."));
                    }
                    return;
                    //TODO play "outofFuel" sound
                }
            }
        }

        if (status != OFF_MODE)
        {
            fuelConsumption++;
            if (status == NORMAL_MODE)
            {
                if (!player.onGround && !player.isSneaking() && player.motionY < 0.0D)
                {
                    player.motionY = player.motionY * 0.6;
                }
                if (player.isSneaking())
                {
                    player.motionY = -0.3;
                }
            }

            if (status == HOVER_MODE)
            {
                fuelConsumption+=2;
                player.motionY *= 0.05;

                if (player.isSneaking())
                {
                    player.motionY = -0.3;
                    fuelConsumption--;
                }
            }
            player.fallDistance = 0;
            //Smoke
            if (!world.isRemote)
            {
                ModNetwork.sendToNearby(new PlayerParticlePacket.Message(PlayerParticlePacket.COPTER_PARTICLE, player.getUniqueID().toString()), player);
            }
            //Sound

            //Airwave
            float factor = 0.24f;
            if (!player.onGround)
            {
                pushEntities(world, player, 0.1f);

                if (player.moveForward > 0)
                {
                    player.moveFlying(0.0F, factor, factor);
                }
                if (player.moveForward < 0)
                {
                    player.moveFlying(0.0F, -factor, factor * 0.8F);
                }
                if (player.moveStrafing > 0)
                {
                    player.moveFlying(factor, 0.0F, factor);
                }
                if (player.moveStrafing < 0)
                {
                    player.moveFlying(-factor, 0.0F, factor);
                }
            } else
            {
                pushEntities(world, player, factor + 0.4f);
            }

            //Elevation
            if (world.isRemote)
            {
                if (Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed())
                {
                    if (inv.canConsumeFuel(fuelConsumption + 2) && canElevate)
                    {
                        elevate(player, stack);
                    }
                }
            }

            //Elevation
            if (!player.onGround && player.motionY > 0)
            {
                fuelConsumption += 2;
            }
            inv.openInventory();
            int ticks = inv.tickCounter - 1;
            if (inv.fuelTank.getFluid() != null)
            {
                if(FluidUtils.isValidFuel(inv.getFuelTank().getFluid().getFluid()))
                {
                    fuelConsumption = (int)Math.floor(fuelConsumption * FluidUtils.fuelValues.get(inv.getFuelTank().getFluid().getFluid().getName()));
                }
            }
            if (ticks <= 0)
            {
                inv.tickCounter = 3;
                inv.consumeFuel(fuelConsumption);
            } else
            {
                inv.tickCounter = ticks;
            }
            inv.status = status;
            inv.closeInventory();
        }
    }

    @Override
    public void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
    {
        onDroppedByPlayer(stack.copy(),player);
        BackpackProperty.get(player).setWearable(null);
    }

    @Override
    public void onEquipped(World world, EntityPlayer player, ItemStack stack)
    {
        if(!stack.hasTagCompound())stack.setTagCompound(new NBTTagCompound());
        stack.stackTagCompound.setByte("status",OFF_MODE);
        /*if(!world.isRemote)
        ModNetwork.sendToNearby(new PlayerSoundPacket.Message(PlayerSoundPacket.COPTER_SOUND,player.getUniqueID().toString(),true), player);*/
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
        return new ModelCopterPack(wearable);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearableTexture(ItemStack wearable)
    {
        return Resources.modelTextures("copterPack");
    }


}
