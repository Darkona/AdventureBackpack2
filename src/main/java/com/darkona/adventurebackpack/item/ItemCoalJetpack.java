package com.darkona.adventurebackpack.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryCoalJetpack;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.network.PlayerActionPacket;
import com.darkona.adventurebackpack.network.messages.EntityParticlePacket;
import com.darkona.adventurebackpack.network.messages.EntitySoundPacket;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.TipUtils;

import static com.darkona.adventurebackpack.util.TipUtils.l10n;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class ItemCoalJetpack extends ItemAdventure
{
    public ItemCoalJetpack()
    {
        super();
        setUnlocalizedName("coalJetpack");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(BackpackUtils.createJetpackStack());
    }

    @Override
    @SuppressWarnings({"unchecked"})
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltips, boolean advanced)
    {
        FluidTank waterTank = new FluidTank(Constants.Jetpack.WATER_CAPACITY);
        FluidTank steamTank = new FluidTank(Constants.Jetpack.STEAM_CAPACITY);
        NBTTagCompound jetpackTag = BackpackUtils.getWearableCompound(stack);

        if (GuiScreen.isShiftKeyDown())
        {
            NBTTagList itemList = jetpackTag.getTagList(Constants.TAG_INVENTORY, NBT.TAG_COMPOUND);
            tooltips.add(l10n("jetpack.fuel") + ": " + TipUtils.slotStackTooltip(itemList, Constants.Jetpack.FUEL_SLOT));

            waterTank.readFromNBT(jetpackTag.getCompoundTag(Constants.Jetpack.TAG_WATER_TANK));
            tooltips.add(l10n("jetpack.tank.water") + ": " + TipUtils.tankTooltip(waterTank));

            steamTank.readFromNBT(jetpackTag.getCompoundTag(Constants.Jetpack.TAG_STEAM_TANK));
            // special case for steam, have to set displayed fluid name manually, cuz technically it's water
            String theSteam = steamTank.getFluidAmount() > 0 ? EnumChatFormatting.AQUA + l10n("steam") : "";
            tooltips.add(l10n("jetpack.tank.steam") + ": " + TipUtils.tankTooltip(steamTank, false) + theSteam);

            TipUtils.shiftFooter(tooltips);
        }
        else if (!GuiScreen.isCtrlKeyDown())
        {
            tooltips.add(TipUtils.holdShift());
        }

        if (GuiScreen.isCtrlKeyDown())
        {
            tooltips.add(l10n("max.altitude") + ": " + TipUtils.whiteFormat("185 ") + l10n("meters"));
            tooltips.add(TipUtils.pressShiftKeyFormat(TipUtils.actionKeyFormat()) + l10n("jetpack.key.onoff1"));
            tooltips.add(l10n("jetpack.key.onoff2") + " " + l10n("on"));
        }
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
        if (inv.getTemperature() == 0) inv.setTemperature(getBiomeMinTemp(player, world));
    }

    @Override
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack) //TODO extract behavior to separate class
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
            }
            else
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

            if (!world.isRemote && mustBlublub)
            {
                ModNetwork.net.sendTo(new EntitySoundPacket.Message(EntitySoundPacket.BOILING_BUBBLES, player), (EntityPlayerMP) player);
            }
        }
        else
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
        }
        else
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
            inv.setCurrentItemBurnTime(inv.getBurnTicks());
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
            if (temperature < Constants.Jetpack.MAX_TEMPERATURE)
            {
                if (burnTicks % inv.getIncreasingFactor() == 0)
                {
                    ++temperature;
                    coolTicks = coolTicks < 5000 ? coolTicks + 100 : coolTicks;
                }
            }
        }
        else
        {
            inv.setCurrentItemBurnTime(0);
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
        inv.markDirty();
    }

    @Override
    public void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
    {
        onUnequipped(world, player, stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return (float) getTemperature(stack) / Constants.Jetpack.MAX_TEMPERATURE + 50;
    }

    private int getTemperature(ItemStack jetpack)
    {
        return BackpackUtils.getWearableCompound(jetpack).getInteger("temperature");
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

}