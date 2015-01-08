package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.client.models.ModelCopterPack;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.network.messages.PlayerParticlePacket;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.List;

/**
 * Created on 31/12/2014
 *
 * @author Darkona
 */
public class ItemCopterPack extends ArmorAB
{

    public ItemCopterPack()
    {
        super(1, 1);
        setUnlocalizedName("copterPack");
        setFull3D();
        setMaxStackSize(1);
    }

    public static byte OFF_MODE = 0;
    public static byte NORMAL_MODE = 1;
    public static byte HOVER_MODE = 2;


    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.COPTER_GUI, GUIPacket.FROM_HOLDING));
        }
        return stack;
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     *
     * @param stack
     * @param world
     * @param entity
     * @param slot
     * @param isCurrent
     */
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isCurrent)
    {
        super.onUpdate(stack, world, entity, slot, isCurrent);

    }

    /**
     * Called to tick armor in the armor slot. Override to do something
     *
     * @param world
     * @param player
     * @param stack
     */


    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {
        //You dont need a helicopter backpack if you can simply fly around.
        if (player == null || stack == null) return;
        //|| player.capabilities.isCreativeMode || player.capabilities.allowFlying || player.capabilities.isFlying) return;
        InventoryCopterPack inv = new InventoryCopterPack(stack);


        boolean canElevate = true;
        int fuelConsumption = 0;
        byte status = stack.stackTagCompound.getByte("status");
        float pitch = 1.0f;
        if (status != OFF_MODE)
        {
            if (inv.fuelTank.getFluidAmount() == 0)
            {
                canElevate = false;
                if (player.onGround)
                {
                    stack.stackTagCompound.setByte("status", OFF_MODE);
                    if (world.isRemote)
                    {
                        player.addChatComponentMessage(new ChatComponentText("CopterPack: out of fuel, shutting off."));
                    }
                    //TODO play "backpackOff" sound
                }
                if (!player.onGround && status == HOVER_MODE)
                {
                    stack.stackTagCompound.setByte("status", NORMAL_MODE);
                    if (world.isRemote)
                    {
                        player.addChatComponentMessage(new ChatComponentText("CopterPack: out of fuel."));
                    }
                    //TODO play "outofFuel" sound
                }
            }
            fuelConsumption++;
            if (status == NORMAL_MODE)
            {
                player.fallDistance = 0;
                if (!player.onGround && !player.isSneaking() && player.motionY < 0.0D)
                {
                    fuelConsumption--;
                    player.motionY = (status == OFF_MODE) ? player.motionY * 0.7 : player.motionY * 0.6;
                }
                if (player.isSneaking())
                {
                    fuelConsumption--;
                    player.motionY = -0.3;
                    pitch = 0.8f;
                }
            }

            if (status == HOVER_MODE)
            {
                fuelConsumption++;
                player.motionY = 0;
                player.fallDistance = 0;
                if (player.isSneaking())
                {
                    player.motionY = -0.3;
                    fuelConsumption--;
                    pitch = 0.8f;
                }
            }

            //Smoke
            if (!world.isRemote)
            {
                ModNetwork.sendToNearby(new PlayerParticlePacket.Message(PlayerParticlePacket.COPTER_PARTICLE, player.getUniqueID().toString()), player);
            }
            //Sound

            //Airwave
            if (!player.onGround)
            {
                pushEntities(world, player, 0.1f);
                float factor = 0.18f;
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
                pushEntities(world, player, 0.2f);
            }

            //Elevation
            if (world.isRemote)
            {
                if (Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed())
                {
                    if (inv.canConsumeFuel(fuelConsumption + 2) && canElevate)
                    {
                        elevate(player, stack);
                        pitch = 1.2f;
                    }
                }
            }
            //Elevation
            if (!player.onGround && player.motionY > 0)
            {
                fuelConsumption += 2;
            }
        }

        //Consume the Fuel, update the ticks

        inv.openInventory();
        int ticks = inv.tickCounter - 1;

        if (inv.fuelTank.getFluid() != null)
        {
            String name = FluidRegistry.getFluidName(inv.fuelTank.getFluid());
            if (name.equals("oil")) fuelConsumption = (int) Math.floor(fuelConsumption * 1.5D);
        }

        if (ticks <= 0)
        {
            //if(status!=OFF_MODE)
            // world.playSound(player.posX,player.posY,player.posZ,"adventurebackpack:helicopter",0.5f,pitch,true);
            //world.playSoundAtEntity(player,"adventurebackpack:helicopter",1.0f,pitch);
            inv.tickCounter = 12;
        } else
        {
            inv.tickCounter = ticks;
        }
        if (ticks % 4 == 0)
        {
            inv.consumeFuel(fuelConsumption);
        }
        inv.closeInventoryNoStatus();
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
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot)
    {
        return ModelCopterPack.instance.setCopterPack(stack);
    }


    @SideOnly(Side.CLIENT)
    @Override
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
}
