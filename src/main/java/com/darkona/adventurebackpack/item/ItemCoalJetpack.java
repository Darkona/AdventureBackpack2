package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerJetpack;
import com.darkona.adventurebackpack.inventory.InventoryCoalJetpack;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.network.PlayerActionPacket;
import com.darkona.adventurebackpack.network.messages.EntityParticlePacket;
import com.darkona.adventurebackpack.network.messages.EntitySoundPacket;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.Utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class ItemCoalJetpack extends ItemAB implements IBackWearableItem
{

    public static byte OFF_MODE = 0;
    public static byte NORMAL_MODE = 1;

    public ItemCoalJetpack()
    {
        super();
        setUnlocalizedName("CoalJetpack");
        setFull3D();
        setMaxStackSize(1);
    }

    @Override
    public int getItemEnchantability()
    {
        return 0;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return (Utils.isSoulBook(book));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.JETPACK_GUI, GUIPacket.FROM_HOLDING));
        }
        return stack;
    }

    private void runFirebox(InventoryCoalJetpack inv)
    {
        if (inv.getBurnTicks() <= 0)
        {
            inv.setBurnTicks(inv.consumeFuel());
            inv.currentItemBurnTime = inv.getBurnTicks();
        }
        inv.dirtyInventory();
    }

    private void runHeater(InventoryCoalJetpack inv, World world, EntityPlayer player)
    {
        int temperature = inv.getTemperature();
        int burnTicks = inv.getBurnTicks() - 1;
        int coolTicks = inv.getCoolTicks() - 1;

        if (burnTicks > 0)
        {
            if (temperature < InventoryCoalJetpack.MAX_TEMPERATURE)
            {
                if (burnTicks % inv.getIncreasingFactor() == 0)
                {
                    ++temperature;
                    coolTicks = coolTicks < 5000 ? coolTicks + 100 : coolTicks;
                }
            }
        } else if (burnTicks <= 0)
        {
            inv.currentItemBurnTime = 0;
            if (coolTicks % inv.getDecreasingFactor() == 0)
            {
                temperature = (temperature - 1 >= getBiomeMinTemp(player, world)) ? temperature - 1 : 0;
            }
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

    @Override
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack)
    {
        InventoryCoalJetpack inv = new InventoryCoalJetpack(stack);
        inv.openInventory();
        boolean mustFizzz = !inv.isInUse();
        int CoalConsumed = 13;
        boolean canUse = inv.getCoalTank().drain(CoalConsumed, false) != null;

        if (inv.getStatus())
        {
            runFirebox(inv);
        }
        runHeater(inv, world, player);
        runWater(inv, world, player);
        inv.dirtyBoiler();

        //Suction
        if (player.isInWater())
        {
            inv.getWaterTank().fill(new FluidStack(FluidRegistry.WATER, 2), true);
        }

        //Elevation
        if (world.isRemote)
        {
            if (inv.getStatus() && canUse && Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed())
            {
                inv.setInUse(true);
                ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(PlayerActionPacket.JETPACK_IN_USE));
                if (mustFizzz)
                {
                    ModNetwork.net.sendToServer(new EntitySoundPacket.Message(EntitySoundPacket.JETPACK_FIZZ, player));
                }
            } else
            {
                inv.setInUse(false);
                ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(PlayerActionPacket.JETPACK_NOT_IN_USE));
            }
        }

        if (inv.isInUse() && canUse)
        {

            elevate(player);
            inv.getCoalTank().drain(CoalConsumed, true);
            if (inv.getCoalTank().getFluidAmount() == 0)
            {
                inv.setInUse(false);
            }
            player.moveFlying(player.moveStrafing, player.moveForward, 0.02f);
            if (player.fallDistance > 1)
            {
                player.fallDistance -= 1;
            }
            if (player.motionY >= 0)
            {
                player.fallDistance = 0;
            }
            if (!world.isRemote) ModNetwork.sendToNearby(new EntityParticlePacket.Message(EntityParticlePacket.JETPACK_PARTICLE, player), player);

        }
        inv.closeInventory();
    }

    private void runWater(InventoryCoalJetpack inv, World world, EntityPlayer player)
    {
        int temperature = inv.getTemperature();
        boolean mustSSSSS = !inv.isLeaking();
        boolean mustBlublub = !inv.isWater();
        boolean Water = inv.isWater();
        boolean leaking = inv.isLeaking();

        if (temperature >= 100 && inv.getWaterTank().getFluidAmount() > 0)
        {
            if (!Water) Water = true;

            if (!world.isRemote && mustBlublub)
            {
                ModNetwork.net.sendTo(new EntitySoundPacket.Message(EntitySoundPacket.BOILING_BUBBLES, player), (EntityPlayerMP) player);
            }
        } else
        {
            if (Water)
            {
                Water = false;
            }
        }

        if (Water)
        {
            if (inv.getCoalTank().getFluidAmount() < inv.getCoalTank().getCapacity())
            {
                if (inv.getWaterTank().getFluid() != null)
                {
                    int water = inv.getWaterTank().drain((temperature / 100), true).amount;
                    inv.getCoalTank().fill(new FluidStack(FluidRegistry.getFluid("water"), water * 4), true);
                    inv.dirtyTanks();
                }
            }
        }

        if (inv.getCoalTank().getFluidAmount() < inv.getCoalTank().getCapacity() - 100)
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
        inv.setWater(Water);
        inv.setLeaking(leaking);
        inv.setTemperature(temperature);
    }

    public static void elevate(EntityPlayer player)
    {
        if (player.motionY <= 0.32 && player.posY < 100)
        {
            player.motionY += 0.1;
        } else
        {
            if (player.posY < 256) player.motionY = Math.max(player.motionY, 0.32);
            if (player.posY > 256) player.motionY = 0.32 - ((player.posY % 256) / 256);
        }
    }

    @Override
    public void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
    {
        onUnequipped(world, player, stack);
    }

    @Override
    public void onEquipped(World world, EntityPlayer player, ItemStack stack)
    {
        InventoryCoalJetpack inv = new InventoryCoalJetpack(stack);
        inv.calculateLostTime();
        if (inv.getTemperature() == 0) inv.setTemperature(getBiomeMinTemp(player, world));
    }

    @Override
    public void onUnequipped(World world, EntityPlayer player, ItemStack stack)
    {
        InventoryCoalJetpack inv = new InventoryCoalJetpack(stack);
        inv.setWater(false);
        inv.setInUse(false);
        inv.setLeaking(false);
        inv.setStatus(false);
        inv.setSystemTime(System.currentTimeMillis());
        inv.markDirty();
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player)
    {
        if (stack != null && player instanceof EntityPlayerMP && player.openContainer instanceof ContainerJetpack)
        {
            player.closeScreen();
        }
        return super.onDroppedByPlayer(stack, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getWearableModel(ItemStack wearable)
    {
        return ClientProxy.modelCoalJetpack.setWearable(wearable);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        String modelTexture;
        modelTexture = Resources.modelTextures("CoalJetpack").toString();

        return modelTexture;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearableTexture(ItemStack wearable)
    {
        return Resources.modelTextures("CoalJetpack");
    }
}