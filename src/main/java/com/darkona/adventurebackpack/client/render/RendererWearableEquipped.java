package com.darkona.adventurebackpack.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.darkona.adventurebackpack.item.IBackWearableItem;
import com.darkona.adventurebackpack.util.Wearing;

/**
 * Created on 25/12/2014
 *
 * @author Darkona
 */
public class RendererWearableEquipped extends RendererLivingEntity
{
    public ResourceLocation texture;
    public ModelBiped modelBipedMain;

    public RendererWearableEquipped()
    {
        super(new ModelBiped(0.0F), 0.0F);
        renderManager = RenderManager.instance;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return texture;
    }

    public void render(Entity entity, double x, double y, double z, float rotX, float rotY, float rotZ, float yaw, float pitch)
    {
        try
        {
            if (!Wearing.isWearingWearable((EntityPlayer) entity)) return;
            ItemStack wearable = Wearing.getWearingWearable((EntityPlayer) entity).copy();
            IBackWearableItem wearableItem = (IBackWearableItem) wearable.getItem();
            modelBipedMain = wearableItem.getWearableModel(wearable);
            texture = wearableItem.getWearableTexture(wearable);
            modelBipedMain.bipedBody.rotateAngleX = rotX;
            modelBipedMain.bipedBody.rotateAngleY = rotY;
            modelBipedMain.bipedBody.rotateAngleZ = rotZ;
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            GL11.glPushMatrix();
            try
            {
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                renderMainModel((EntityPlayer) entity, 0, 0, 0, 0, 0, 0.0625f);
            }
            catch (Exception oops)
            {

            }
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glPopMatrix();
        }
        catch (Exception ex)
        {
            //discard silently because NO ONE CARES
        }
    }

    protected void renderMainModel(EntityLivingBase entity, float limbSwing1, float limbswing2, float z, float yaw, float whatever, float scale)
    {
        bindTexture(this.texture);
        if (!entity.isInvisible())
        {
            modelBipedMain.render(entity, limbSwing1, limbswing2, z, yaw, whatever, scale);
        }
        else if (!entity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer))
        {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
            modelBipedMain.render(entity, limbSwing1, limbswing2, z, yaw, whatever, scale);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glPopMatrix();
            GL11.glDepthMask(true);
        }
        else
        {
            modelBipedMain.setRotationAngles(limbSwing1, limbswing2, z, yaw, whatever, scale, entity);
        }
    }
}
