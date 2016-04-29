package com.darkona.adventurebackpack.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Created on 16/10/2014
 *
 * @author Darkona
 */
public class WearableEvent extends PlayerEvent
{

    public final ItemStack wearable;

    public WearableEvent(EntityPlayer player, ItemStack wearable)
    {
        super(player);
        this.wearable = wearable;
    }

    public static class UnequipWearableEvent extends WearableEvent
    {
        public UnequipWearableEvent(EntityPlayer player, ItemStack wearable)
        {
            super(player, wearable);
        }
    }

    public static class EquipWearableEvent extends WearableEvent
    {
        public EquipWearableEvent(EntityPlayer player, ItemStack wearable)
        {
            super(player, wearable);
        }
    }

}
