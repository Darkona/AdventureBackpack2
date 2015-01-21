package com.darkona.adventurebackpack.common;

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
        PotionEffect nightVision = null;
        if (player.isPotionActive(Potion.nightVision.id)) {
            nightVision = player.getActivePotionEffect(Potion.nightVision);
        }
        if (nightVision != null && nightVision.getAmplifier() == -5) {
            if (player.worldObj.isRemote) {
                player.removePotionEffectClient(Potion.nightVision.id);
            } else {
                player.removePotionEffect(Potion.nightVision.id);
            }
        }
    }

    public void itemSquid(EntityPlayer player, World world, ItemStack backpack)
    {
        itemBat(player, world, backpack);
    }

    public void itemDragon(EntityPlayer player, World world, ItemStack backpack)
    {
        itemBat(player, world, backpack);
    }

    public void itemRainbow(EntityPlayer player, World world, ItemStack backpack)
    {
        InventoryBackpack inv = new InventoryBackpack(Wearing.getWearingBackpack(player));
        if (inv.getLastTime() > 0) return;
        inv.setLastTime(0);
        inv.dirtyTime();
    }
}
