package com.darkona.adventurebackpack.common.ability;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.AbilityEffects;
import com.darkona.adventurebackpack.common.NightVisionRefresh;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.util.TickThrottle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

final class AbilityHelper
{
    private AbilityHelper() {}

    static boolean isUnderRain(EntityPlayer player)
    {
        return player.worldObj.canLightningStrikeAt(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY),
                MathHelper.floor_double(player.posZ))
                || player.worldObj.canLightningStrikeAt(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY + player.height),
                MathHelper.floor_double(player.posZ));
    }

    static void giveNightVision(EntityPlayer player)
    {
        PotionEffect nightVision = player.getActivePotionEffect(Potion.nightVision);
        if (NightVisionRefresh.needsRefresh(nightVision != null,
                nightVision != null ? nightVision.getDuration() : 0,
                nightVision != null ? nightVision.getAmplifier() : 0, -4))
        {
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id, ConfigHandler.BAT_NIGHT_VISION_TICKS, -4));
        }
    }

    static void giveWaterBreathing(EntityPlayer player, World world)
    {
        if (world.isRemote) return;
        if (AbilityEffects.squidActive(player.isInWater(), ConfigHandler.SQUID_WORKS_ON_LAND))
        {
            PotionEffect breathing = player.getActivePotionEffect(Potion.waterBreathing);
            if (AbilityEffects.needsShortRefresh(breathing != null,
                    breathing != null ? breathing.getDuration() : 0,
                    breathing != null ? breathing.getAmplifier() : 0, AbilityEffects.SQUID_WATER_BREATHING_MARKER))
            {
                player.addPotionEffect(new PotionEffect(Potion.waterBreathing.getId(),
                        AbilityEffects.SHORT_EFFECT_TICKS, AbilityEffects.SQUID_WATER_BREATHING_MARKER));
            }
            giveNightVision(player);
        }
        else
        {
            removeSquidEffects(player);
        }
    }

    static void giveFireResistance(EntityPlayer player, int ticks)
    {
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.fireResistance.id))
        {
            potion = player.getActivePotionEffect(Potion.fireResistance);
        }
        if (potion == null || AbilityEffects.needsRefresh(potion.getDuration()))
        {
            player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, ticks, -5));
        }
    }

    static void fillWhenWet(EntityPlayer player, World world, ItemStack backpack, Fluid fluid, int interval, int inWater, int inRain)
    {
        if (world.isRemote || fluid == null) return;
        if (!TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), interval)) return;
        int drops = 0;
        if (player.isInWater()) drops += inWater;
        if (isUnderRain(player)) drops += inRain;
        if (drops <= 0) return;
        InventoryBackpack inv = new InventoryBackpack(backpack);
        FluidStack raindrop = new FluidStack(fluid, drops);
        int filled = inv.getLeftTank().fill(raindrop, true) + inv.getRightTank().fill(raindrop, true);
        if (filled > 0) inv.dirtyTanks();
    }

    static void fillWithRain(World world, TileAdventureBackpack backpack, Fluid fluid, int amount, int time)
    {
        if (world.isRaining() && world.canBlockSeeTheSky(backpack.xCoord, backpack.yCoord, backpack.zCoord))
        {
            int dropTime = backpack.getLastTime() - 1;
            if (dropTime <= 0)
            {
                if (fluid != null)
                {
                    FluidStack drop = new FluidStack(fluid, amount);
                    backpack.getRightTank().fill(drop, true);
                    backpack.getLeftTank().fill(drop, true);
                }
                dropTime = time;
                backpack.markDirty();
            }
            backpack.setLastTime(dropTime);
        }
    }

    static void makeFromWheat(EntityPlayer player, World world, ItemStack backpack, FluidStack drop, int eatInterval, int wheatPerBatch, int perBatch)
    {
        if (world.isRemote || drop.getFluid() == null) return;
        if (!TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), eatInterval)) return;

        InventoryBackpack inv = new InventoryBackpack(backpack);
        if (inv.getLeftTank().fill(drop, false) <= 0 && inv.getRightTank().fill(drop, false) <= 0)
        {
            return;
        }
        int wheatConsumed = 0;
        int remaining = 0;
        if (inv.getExtendedProperties() != null && inv.extendedProperties.hasKey("wheatConsumed"))
        {
            wheatConsumed = inv.extendedProperties.getInteger("wheatConsumed");
            remaining = MilkBatch.remaining(inv.extendedProperties.getInteger("milkTime"));
        }
        int wheatBefore = wheatConsumed;
        int remainingBefore = remaining;

        if (MilkBatch.eats(remaining, inv.hasItem(Items.wheat)))
        {
            inv.consumeInventoryItem(Items.wheat);
            wheatConsumed++;
            inv.dirtyInventory();
        }

        if (MilkBatch.starts(wheatConsumed, wheatPerBatch))
        {
            wheatConsumed = 0;
            remaining = perBatch;
            world.playSoundAtEntity(player, "mob.cow.say", 1f, 1f);
        }

        int chunk = MilkBatch.chunk(remaining, eatInterval);
        if (chunk > 0)
        {
            int made = inv.getLeftTank().fill(new FluidStack(drop, chunk), true);
            if (made < chunk) made += inv.getRightTank().fill(new FluidStack(drop, chunk - made), true);
            remaining -= made;
            if (made > 0) inv.dirtyTanks();
        }
        if (wheatConsumed != wheatBefore || remaining != remainingBefore)
        {
            inv.extendedProperties.setInteger("wheatConsumed", wheatConsumed);
            inv.extendedProperties.setInteger("milkTime", remaining);
            inv.dirtyExtended();
        }
    }

    static void removeNightVision(EntityPlayer player)
    {
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.nightVision.id))
        {
            potion = player.getActivePotionEffect(Potion.nightVision);

            if (potion != null && potion.getAmplifier() == -4)
            {
                removeEffect(player, Potion.nightVision.id);
            }
        }
    }

    static void removeSquidEffects(EntityPlayer player)
    {
        removeNightVision(player);
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.waterBreathing.id))
        {
            potion = player.getActivePotionEffect(Potion.waterBreathing);

            if (potion != null && AbilityEffects.isSquidWaterBreathing(potion.getAmplifier()))
            {
                removeEffect(player, Potion.waterBreathing.id);
            }
        }
    }

    static void removeMarkedFireResistance(EntityPlayer player)
    {
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.fireResistance.id))
        {
            potion = player.getActivePotionEffect(Potion.fireResistance);

            if (potion != null && potion.getAmplifier() == -5)
            {
                removeEffect(player, Potion.fireResistance.id);
            }
        }
    }

    static void removeEffect(EntityPlayer player, int potionId)
    {
        if (player.worldObj.isRemote)
        {
            player.removePotionEffectClient(potionId);
        }
        else
        {
            player.removePotionEffect(potionId);
        }
    }
}
