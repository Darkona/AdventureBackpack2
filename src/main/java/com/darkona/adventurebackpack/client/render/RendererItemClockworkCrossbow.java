package com.darkona.adventurebackpack.client.render;


import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import com.darkona.adventurebackpack.client.models.ModelClockworkCrossbow;
import com.darkona.adventurebackpack.util.Resources;


/**
 * Created by Darkona on 11/10/2014.
 */
public class RendererItemClockworkCrossbow implements IItemRenderer
{
    private final ModelClockworkCrossbow model;
    private ResourceLocation modelTexture = Resources.modelTextures("clockworkCrossbow");

    public RendererItemClockworkCrossbow()
    {
        model = new ModelClockworkCrossbow();
    }

    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return (type != ItemRenderType.FIRST_PERSON_MAP);
    }

    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return (type != ItemRenderType.FIRST_PERSON_MAP);
    }

    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(modelTexture);
        EntityPlayer player;
        switch (type)
        {
            case INVENTORY:

            {
                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 128);

                GL11.glPushMatrix();
                GL11.glTranslatef(-0.5f, -.5f, -0.5f);

                GL11.glPushMatrix();
                GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
                GL11.glPushMatrix();
                GL11.glScalef(0.7f, 0.7f, 0.7f);

                model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.05F);

                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
            }
            break;
            case ENTITY:
                Minecraft.getMinecraft().renderEngine.bindTexture(modelTexture);
            {
                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 128);

                GL11.glPushMatrix();
                GL11.glTranslatef(0f, .50f, 0f);

                GL11.glPushMatrix();
                GL11.glRotatef(180, 0, 0, 1);

                GL11.glPushMatrix();
                GL11.glScalef(1.0f, 1.0f, 1.0f);

                model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.05F);

                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
            }
            break;
            case EQUIPPED:
                Minecraft.getMinecraft().renderEngine.bindTexture(modelTexture);

                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 128);
                GL11.glPushMatrix();
                GL11.glScalef(1.2f, 1.2f, 1.2f);

                GL11.glPushMatrix();
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glRotatef(-45, 0, 1, 0);
                GL11.glRotatef(75, 1, 0, 0);

                GL11.glTranslatef(0.0f, 0.4f, 0.2f);
                player = (EntityPlayer) data[1];
                model.render(player, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.05F, item);

                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPopMatrix();

                break;
            case EQUIPPED_FIRST_PERSON:
                Minecraft.getMinecraft().renderEngine.bindTexture(modelTexture);
                player = (EntityPlayer) data[1];

                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 128);
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glScalef(1.2f, 1.2f, 1.2f);

                GL11.glPushMatrix();
                GL11.glTranslatef(0.81f, -1f, 0f);

                GL11.glPushMatrix();
                GL11.glRotatef(45, 0, 1, 0);
                GL11.glRotatef(20, 1, 0, 0);

                GL11.glRotatef(-20, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.32F, .42F);
                model.render(player, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.05F, item);
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                break;
            case FIRST_PERSON_MAP:
                break;
        }
    }
}