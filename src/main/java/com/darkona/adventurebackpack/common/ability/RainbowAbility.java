package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.common.AbilityEffects;
import com.darkona.adventurebackpack.common.AbilityTimer;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class RainbowAbility implements IBackpackAbility
{
    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.RAINBOW_ENABLED) return;
        int length = Utils.secondsToTicks(ConfigHandler.RAINBOW_DURATION_SECONDS);
        long left = AbilityTimer.remaining(world.getTotalWorldTime(), BackpackUtils.getTimeMark(backpack, BackpackAbilities.RAINBOW_END), length);
        long noteTime = left - 1;
        if (left > 0 && noteTime < Utils.secondsToTicks(ConfigHandler.RAINBOW_DURATION_SECONDS - 3))
        {
            player.setSprinting(true);
            refresh(player, Potion.moveSpeed, ConfigHandler.RAINBOW_SPEED_AMPLIFIER);
            refresh(player, Potion.jump, ConfigHandler.RAINBOW_JUMP_AMPLIFIER);
            if (noteTime % 2 == 0 && world.isRemote) notes(player, world);
        }
        PotionEffect stale = player.getActivePotionEffect(Potion.moveSpeed);
        if (stale != null && AbilityEffects.isStaleRainbowMarker(stale.getAmplifier()))
        {
            if (world.isRemote) player.removePotionEffectClient(Potion.moveSpeed.id);
            else player.removePotionEffect(Potion.moveSpeed.id);
        }
    }

    private static void refresh(EntityPlayer player, Potion potion, int amplifier)
    {
        PotionEffect current = player.getActivePotionEffect(potion);
        if (AbilityEffects.needsRainbowRefresh(current != null, current == null ? 0 : current.getAmplifier(),
                current == null ? 0 : current.getDuration(), amplifier))
        {
            player.addPotionEffect(new PotionEffect(potion.getId(), AbilityEffects.RAINBOW_EFFECT_TICKS, amplifier));
        }
    }

    public static void notes(EntityPlayer player, World world)
    {
        int i = 2;
        for (int j = 0; j < i * 3; ++j)
        {
            float f = world.rand.nextFloat() * (float) Math.PI * 2.0F;
            float f1 = world.rand.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * i * 0.5F * f1;
            float f3 = MathHelper.cos(f) * i * 0.5F * f1;
            world.spawnParticle("note",
                    player.posX + f2,
                    player.boundingBox.minY + 0.8f,
                    player.posZ + f3,
                    (double) (float) Math.pow(2.0D, (world.rand.nextInt(169) - 12) / 12.0D) / 24.0D,
                    -1.0D,
                    0.0D);
        }
    }

    @Override
    public void onRemoval(EntityPlayer player, World world, ItemStack backpack)
    {
        PotionEffect marker = player.getActivePotionEffect(Potion.moveSpeed);
        if (marker != null && AbilityEffects.isStaleRainbowMarker(marker.getAmplifier()))
        {
            AbilityHelper.removeEffect(player, Potion.moveSpeed.id);
        }
        if (BackpackAbilities.isRainbowRunning(world, backpack)) return;
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.moveSpeed.id)) {
            potion = player.getActivePotionEffect(Potion.moveSpeed);
            if (potion != null && AbilityEffects.isRainbowEffect(potion.getAmplifier(), potion.getDuration(), ConfigHandler.RAINBOW_SPEED_AMPLIFIER)) {
                AbilityHelper.removeEffect(player, Potion.moveSpeed.id);
            }
        }
        potion = null;
        if (player.isPotionActive(Potion.jump.id)) {
            potion = player.getActivePotionEffect(Potion.jump);
            if (potion != null && AbilityEffects.isRainbowEffect(potion.getAmplifier(), potion.getDuration(), ConfigHandler.RAINBOW_JUMP_AMPLIFIER)) {
                AbilityHelper.removeEffect(player, Potion.jump.id);
            }
        }
    }
}
