package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.client.models.ModelBackpackBlock;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.Resources;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.lwjgl.opengl.GL11;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
@SuppressWarnings("unused")
public class RendererItemAdventureBackpack implements IItemRenderer
{
    private final ModelBackpackBlock model;
	private Minecraft mc;

    public RendererItemAdventureBackpack()
    {
        model = new ModelBackpackBlock();
    }

    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type)
    {
        switch (type)
        {
            case INVENTORY:
                return true;
            case ENTITY:
                return true;
            case EQUIPPED:
                return true;
            case EQUIPPED_FIRST_PERSON:
                return true;
            case FIRST_PERSON_MAP:
                return false;
        }
        return false;
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper)
    {
        switch (type)
        {
            case INVENTORY:
                return true;
            case ENTITY:
                return true;
            case EQUIPPED:
                return false;
            case EQUIPPED_FIRST_PERSON:
                return false;
            case FIRST_PERSON_MAP:
                return false;
        }
        return false;
    }

    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack backpack, Object... data)
    {
        InventoryBackpack inv = new InventoryBackpack(backpack);
        ResourceLocation modelTexture;
        if (BackpackNames.getBackpackColorName(backpack).equals("Standard"))
        {
            modelTexture = Resources.backpackTextureFromString(AdventureBackpack.instance.Holiday);
        } else
        {
            modelTexture = Resources.backpackTextureFromColor(inv);
        }
        switch (type)
        {

            case INVENTORY:
                Minecraft.getMinecraft().renderEngine.bindTexture(modelTexture);
            {
                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 128);

                GL11.glPushMatrix();
                GL11.glTranslatef(-0.5f, 0f, -0.5f);

                GL11.glPushMatrix();
                GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

                GL11.glPushMatrix();
                GL11.glScalef(1.9f, 1.9f, 1.9f);

                model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.05F, inv);

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
                GL11.glTranslatef(0f, 1f, 0f);

                GL11.glPushMatrix();
                GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

                GL11.glPushMatrix();
                GL11.glScalef(1.2f, 1.2f, 1.2f);

                model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.05F, inv);

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
                GL11.glTranslatef(0.8f, 0.8f, 0.0f);

                GL11.glPushMatrix();
                GL11.glScalef(0.9f, 0.9f, 0.9f);

                GL11.glPushMatrix();
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glRotatef(90, 0, 1, 0);

                GL11.glPushMatrix();
                GL11.glRotatef(-45, 0, 1, 0);
                model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.05f, inv);

                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                break;

            case EQUIPPED_FIRST_PERSON:
                Minecraft.getMinecraft().renderEngine.bindTexture(modelTexture);

                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 128);

                GL11.glPushMatrix();
                GL11.glTranslatef(1f, 0.8f, 0.8f);

                GL11.glPushMatrix();
                GL11.glScalef(1.5f, 1.5f, 1.5f);

                GL11.glPushMatrix();
                GL11.glRotatef(195, 0, 0, 1);

                GL11.glPushMatrix();
                GL11.glRotatef(180, 0, 1, 0);
                model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.05f, inv);

                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                break;
            case FIRST_PERSON_MAP:
                break;
        }
    }
 }