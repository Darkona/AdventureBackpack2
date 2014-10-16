package com.darkona.adventurebackpack.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Created on 16/10/2014
 *
 * @author Darkona
 */
public class EquipBackpackEvent extends PlayerEvent {

    public EquipBackpackEvent(EntityPlayer player) {
        super(player);
    }


}
