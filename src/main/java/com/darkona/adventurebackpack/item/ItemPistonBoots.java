package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.config.ConfigHandler;

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

    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
    {
        if (ConfigHandler.PISTONBOOTS_ENABLED && player.isSprinting())
        {
            player.stepHeight = ConfigHandler.PISTONBOOTS_STEP_HEIGHT;
            if (ConfigHandler.PISTONBOOTS_SPRINT_SPEED) player.addPotionEffect(new PotionEffect(Potion.moveSpeed.getId(), 1));
        } else
        {
            player.stepHeight = 0.5001F;
        }
    }

}
