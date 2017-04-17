package com.darkona.adventurebackpack.client.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;

/**
 * Created on 19/10/2014
 *
 * @author Darkona
 */
public class ModelFullArmor extends ModelBiped
{
    public ModelFullArmor()
    {
        super(0.2F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {

        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase owner = (EntityLivingBase) entity;
            this.isSneak = owner.isSneaking();
            this.onGround = entity.onGround ? 1 : 0;
            this.heldItemRight = (owner.getHeldItem() != null) ? 1 : 0;
            this.isRiding = entity.isRiding();
            if (owner instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) owner;
                this.aimedBow = player.isUsingItem() && player.getItemInUse() != null && player.getItemInUse().getItemUseAction() == EnumAction.bow;
                this.heldItemRight = (player.getCurrentEquippedItem() != null) ? 1 : 0;
            }
        }
        super.render(entity, f, f1, f2, f3, f4, f5);
    }
}