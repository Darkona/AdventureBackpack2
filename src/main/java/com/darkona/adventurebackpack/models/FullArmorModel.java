package com.darkona.adventurebackpack.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;

/**
 * Created on 19/10/2014
 *
 * @author Darkona
 */
public class FullArmorModel extends ModelBiped
{

    public static FullArmorModel instance = new FullArmorModel();

    public FullArmorModel()
    {
        super(0.1F);

    }

    public FullArmorModel setPlayer(EntityPlayer player)
    {
        this.isSneak = player.isSneaking();
        this.aimedBow = player.isUsingItem() && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemBow;
        this.onGround = (player.onGround) ? 1 : 1;
        this.heldItemRight = (player.getCurrentEquippedItem() != null) ? 1 : 0;
        this.isRiding = player.isRiding();
        return this;
    }


    public FullArmorModel(float par1)
    {
        super(par1);
        // TODO Auto-generated constructor stub
    }

    public FullArmorModel(float par1, float par2, int par3, int par4)
    {
        super(par1, par2, par3, par4);
        // TODO Auto-generated constructor stub
    }

}