package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.client.models.ModelBackpackArmor;
import com.darkona.adventurebackpack.client.models.ModelCopterPack;
import com.darkona.adventurebackpack.client.models.ModelWearable;
import com.darkona.adventurebackpack.item.IBackWearableItem;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Created on 25/12/2014
 *
 * @author Darkona
 */
public class RendererWearableEquipped extends RendererLivingEntity
{

    public ResourceLocation texture;
    public ModelBiped modelBipedMain;
    public ModelBackpackArmor backpack = new ModelBackpackArmor();
    public ModelCopterPack copter = new ModelCopterPack();

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

        ItemStack wearable = Wearing.getWearingWearable((EntityPlayer)entity);
        Item wearableItem = wearable.getItem();
        if(wearableItem instanceof ItemAdventureBackpack) modelBipedMain = backpack;
        // backpack.setBackpack(wearable);
        if(wearableItem instanceof ItemCopterPack)modelBipedMain = copter;
        //copter.setCopter(wearable);
        texture = ((IBackWearableItem)wearableItem).getWearableTexture(wearable);
        modelBipedMain.bipedBody.rotateAngleX = rotX;
        modelBipedMain.bipedBody.rotateAngleY = rotY;
        modelBipedMain.bipedBody.rotateAngleZ = rotZ;
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        try
        {
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            renderMainModel((EntityPlayer) entity, 0, 0, 0, 0, 0, 0.0625f,wearable);
        } catch (Exception oops)
        {

        }
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glPopMatrix();
    }

    protected void renderMainModel(EntityLivingBase entity, float limbSwing1, float limbswing2, float z, float yaw, float whatever, float scale, ItemStack wearable)
    {
        bindTexture(this.texture);
        if (!entity.isInvisible())
        {
            ((ModelWearable)this.modelBipedMain).render(entity, limbSwing1, limbswing2, z, yaw, whatever, scale, wearable);
        } else
        if (!entity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer))
        {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
            ((ModelWearable)this.modelBipedMain).render(entity, limbSwing1, limbswing2, z, yaw, whatever, scale, wearable);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glPopMatrix();
            GL11.glDepthMask(true);
        } else
        {
            this.modelBipedMain.setRotationAngles(limbSwing1, limbswing2, z, yaw, whatever, scale, entity);
        }
    }
}
