package com.darkona.adventurebackpack.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Created on 16/10/2014
 *
 * @author Darkona
 */
public class CopterSoundEvent extends PlayerEvent
{
    public final ItemStack backpack;

    public CopterSoundEvent(EntityPlayer player, ItemStack theBackpack)
    {
        super(player);
        backpack = theBackpack;
    }

}
