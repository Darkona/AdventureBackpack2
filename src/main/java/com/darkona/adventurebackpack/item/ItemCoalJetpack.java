package com.darkona.adventurebackpack.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerJetpack;
import com.darkona.adventurebackpack.inventory.InventoryCoalJetpack;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.network.PlayerActionPacket;
import com.darkona.adventurebackpack.network.messages.EntityParticlePacket;
import com.darkona.adventurebackpack.network.messages.EntitySoundPacket;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.util.EnchUtils;
import com.darkona.adventurebackpack.util.Resources;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class ItemCoalJetpack extends ItemAB implements IBackWearableItem
{
    public ItemCoalJetpack()
    {
        super();
        setUnlocalizedName("coalJetpack");
        setFull3D();
        setMaxStackSize(1);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        ItemStack iStack = new ItemStack(item, 1, 0);
        NBTTagCompound compound = new NBTTagCompound();
        iStack.setTagCompound(compound);

        NBTTagCompound jetpackTag = new NBTTagCompound();
        //jetpackTag.setTag(Constants.JETPACK_WATER_TANK, new FluidTank(Constants.JETPACK_WATER_CAPACITY).writeToNBT(new NBTTagCompound()));
        //jetpackTag.setTag(Constants.JETPACK_STEAM_TANK, new FluidTank(Constants.JETPACK_STEAM_CAPACITY).writeToNBT(new NBTTagCompound()));
        //jetpackTag.setTag(Constants.JETPACK_INVENTORY, new NBTTagList());
        compound.setTag(Constants.JETPACK_COMPOUND_TAG, jetpackTag);

        list.add(iStack);
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

    @Override
    public void onEquipped(World world, EntityPlayer player, ItemStack stack)
    {
        InventoryCoalJetpack inv = new InventoryCoalJetpack(stack);
        inv.calculateLostTime(); //TODO debug artifact?
        if (inv.getTemperature() == 0) inv.setTemperature(getBiomeMinTemp(player, world));
    }

    @Override
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack)
    {
        InventoryCoalJetpack inv = new InventoryCoalJetpack(stack);
        inv.openInventory();
        boolean mustFizzz = !inv.isInUse();
        int CoalConsumed = 13;
        boolean canUse = inv.getSteamTank().drain(CoalConsumed, false) != null;

        if (inv.getStatus())
        {
            runFirebox(inv);
        }
        runHeater(inv, world, player);
        runBoiler(inv, world, player);
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
            inv.getSteamTank().drain(CoalConsumed, true);
            if (inv.getSteamTank().getFluidAmount() == 0)
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
            if (!world.isRemote)
                ModNetwork.sendToNearby(new EntityParticlePacket.Message(EntityParticlePacket.JETPACK_PARTICLE, player), player);
        }
        inv.closeInventory();
    }

    private static void elevate(EntityPlayer player)
    {
        if (player.posY < 135)
            if (player.motionY <= 0.32)
                player.motionY += 0.1;
            else
                player.motionY = Math.max(player.motionY, 0.32);
        else if (player.posY < 185)
            player.motionY = 0.32 - (player.posY - 135) / 160;
        else if (player.posY >= 185)
            player.motionY += 0;
    }

    private void runBoiler(InventoryCoalJetpack inv, World world, EntityPlayer player)
    {
        int temperature = inv.getTemperature();
        boolean mustSSSSS = !inv.isLeaking();
        boolean mustBlublub = !inv.isBoiling();
        boolean boiling = inv.isBoiling();
        boolean leaking = inv.isLeaking();

        if (temperature >= 100 && inv.getWaterTank().getFluidAmount() > 0)
        {
            if (!boiling) boiling = true;

            if (!world.isRemote && mustBlublub) //TODO BoilingBoilerSound stop playing after steam tank is full (so boiling is false), and never starts again
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
                if (inv.getWaterTank().getFluid() != null)
                {
                    int water = inv.getWaterTank().drain((temperature / 100), true).amount;
                    inv.getSteamTank().fill(new FluidStack(FluidRegistry.getFluid("water"), water * 4), true);
                    inv.dirtyTanks();
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
            if (temperature < Constants.JETPACK_MAX_TEMPERATURE)
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

    @Override
    public void onUnequipped(World world, EntityPlayer player, ItemStack stack)
    {
        InventoryCoalJetpack inv = new InventoryCoalJetpack(stack);
        inv.setBoiling(false);
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
    public void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
    {
        onUnequipped(world, player, stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return (float) getTemperature(stack) / Constants.JETPACK_MAX_TEMPERATURE + 50;
    }

    private int getTemperature(ItemStack jetpack)
    {
        return jetpack.stackTagCompound.getCompoundTag(Constants.JETPACK_COMPOUND_TAG).getInteger("temperature");
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return ConfigHandler.enableTemperatureBar && getTemperature(stack) > 50;
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
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        String modelTexture;
        modelTexture = Resources.modelTextures("coalJetpack").toString();

        return modelTexture;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getWearableModel(ItemStack wearable)
    {
        return ClientProxy.modelCoalJetpack.setWearable(wearable);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearableTexture(ItemStack wearable)
    {
        return Resources.modelTextures("coalJetpack");
    }

    @Override
    public int getItemEnchantability()
    {
        return 0;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return EnchUtils.isSoulBook(book);
    }
}