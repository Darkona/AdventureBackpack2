package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.common.PistonStep;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.util.Wearing;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Created by Darkona on 11/10/2014.
 */
public class ItemPistonBoots extends ArmorAB
{

    public ItemPistonBoots()
    {
        super(2, 3);
        setUnlocalizedName("pistonBoots");
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
    {
        update(player, true);
    }

    /** Runs every player tick (PlayerEventHandler) so the step height also comes back when the boots come off. */
    public static void tickUnworn(EntityPlayer player)
    {
        BackpackProperty prop = BackpackProperty.get(player);
        if (prop != null && prop.isPistonStepApplied() && !Wearing.isWearingBoots(player))
        {
            update(player, false);
        }
    }

    private static void update(EntityPlayer player, boolean wearing)
    {
        BackpackProperty prop = BackpackProperty.get(player);
        if (prop == null) return;
        boolean sprinting = player.isSprinting();
        boolean boosting = PistonStep.stepping(ConfigHandler.PISTONBOOTS_ENABLED, sprinting, ConfigHandler.PISTONBOOTS_ONLY_WHILE_SPRINTING);
        switch (PistonStep.action(wearing, boosting, prop.isPistonStepApplied()))
        {
            case PistonStep.APPLY:
                prop.setPistonStepApplied(true);
                player.stepHeight = ConfigHandler.PISTONBOOTS_STEP_HEIGHT;
                if (sprinting && ConfigHandler.PISTONBOOTS_SPRINT_SPEED && !player.worldObj.isRemote)
                {
                    // Renewed shortly before it ends instead of a 1-tick effect (and a packet) every tick.
                    PotionEffect speed = player.getActivePotionEffect(Potion.moveSpeed);
                    if (PistonStep.needsSpeed(speed != null, speed != null ? speed.getAmplifier() : 0, speed != null ? speed.getDuration() : 0))
                    {
                        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.getId(), PistonStep.SPEED_TICKS, 0));
                    }
                }
                break;
            case PistonStep.RESTORE:
                prop.setPistonStepApplied(false);
                player.stepHeight = PistonStep.VANILLA_STEP_HEIGHT;
                if (!player.worldObj.isRemote)
                {
                    PotionEffect speed = player.getActivePotionEffect(Potion.moveSpeed);
                    if (speed != null && PistonStep.isBootsSpeed(speed.getAmplifier(), speed.getDuration()))
                    {
                        player.removePotionEffect(Potion.moveSpeed.id);
                    }
                }
                break;
            default:
                break;
        }
    }
}
