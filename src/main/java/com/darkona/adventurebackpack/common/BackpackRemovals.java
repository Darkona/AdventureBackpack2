package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.util.Wearing;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Created on 09/01/2015
 *
 * @author Darkona
 */
public class BackpackRemovals
{
    public void itemBat(EntityPlayer player, World world, ItemStack backpack)
    {
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.nightVision.id))
        {
            potion = player.getActivePotionEffect(Potion.nightVision);

            if (potion != null && potion.getAmplifier() == -1)
            {
                if (player.worldObj.isRemote)
                {
                    player.removePotionEffectClient(Potion.nightVision.id);
                } else
                {
                    player.removePotionEffect(Potion.nightVision.id);
                }
            }
        }
    }

    public void itemSquid(EntityPlayer player, World world, ItemStack backpack)
    {
        itemBat(player, world, backpack);
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.waterBreathing.id))
        {
            potion = player.getActivePotionEffect(Potion.waterBreathing);

            if (potion != null && potion.getAmplifier() == -1)
            {
                if (player.worldObj.isRemote)
                {
                    player.removePotionEffectClient(Potion.waterBreathing.id);
                } else
                {
                    player.removePotionEffect(Potion.waterBreathing.id);
                }
            }
        }
    }

    public void itemPigman(EntityPlayer player, World world, ItemStack backpack)
    {
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.fireResistance.id))
        {
            potion = player.getActivePotionEffect(Potion.fireResistance);

            if (potion != null && potion.getAmplifier() == -1)
            {
                if (player.worldObj.isRemote)
                {
                    player.removePotionEffectClient(Potion.fireResistance.id);
                } else
                {
                    player.removePotionEffect(Potion.fireResistance.id);
                }
            }
        }
    }

    public void itemDragon(EntityPlayer player, World world, ItemStack backpack)
    {
        itemBat(player, world, backpack);
        itemPigman(player, world, backpack);
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.damageBoost.id))
        {
            potion = player.getActivePotionEffect(Potion.damageBoost);
            if (potion != null && potion.getAmplifier() == ConfigHandler.dragonBackpackDamage - 1)
            {
                if (player.worldObj.isRemote)
                {
                    player.removePotionEffectClient(Potion.damageBoost.id);
                } else
                {
                    player.removePotionEffect(Potion.damageBoost.id);
                }
            }
        }
        potion = null;
        if (player.isPotionActive(Potion.regeneration.id))
        {
            potion = player.getActivePotionEffect(Potion.regeneration);

            if (potion != null && potion.getAmplifier() == ConfigHandler.dragonBackpackRegen - 1)
            {
                if (player.worldObj.isRemote)
                {
                    player.removePotionEffectClient(Potion.regeneration.id);
                } else
                {
                    player.removePotionEffect(Potion.regeneration.id);
                }
            }
        }
    }

    public void itemRainbow(EntityPlayer player, World world, ItemStack backpack)
    {
        InventoryBackpack inv = new InventoryBackpack(Wearing.getWearingBackpack(player));
        if (inv.getLastTime() > 0) return;
        inv.setLastTime(0);
        inv.dirtyTime();
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.moveSpeed.id))
        {
            potion = player.getActivePotionEffect(Potion.moveSpeed);
            if (potion != null && potion.getAmplifier() == ConfigHandler.rainbowBackpackSpeed - 1)
            {
                if (player.worldObj.isRemote)
                {
                    player.removePotionEffectClient(Potion.moveSpeed.id);
                } else
                {
                    player.removePotionEffect(Potion.moveSpeed.id);
                }
            }
        }
        /*potion = null;
        if (player.isPotionActive(Potion.jump.id)) {
            potion = player.getActivePotionEffect(Potion.jump);
            if (potion != null && potion.getAmplifier() == 1)
            {
                if (player.worldObj.isRemote)
                {
                    player.removePotionEffectClient(Potion.jump.id);
                } else
                {
                    player.removePotionEffect(Potion.jump.id);
                }
            }
        }*/
    }
}
