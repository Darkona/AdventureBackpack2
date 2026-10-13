package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.darkona.adventurebackpack.client.models.ModelTamedSpider;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderRideableSpider extends RenderLiving
{
    private static final ResourceLocation spiderEyesTextures = new ResourceLocation("textures/entity/spider_eyes.png");
    private static final ResourceLocation spiderTextures = new ResourceLocation("textures/entity/spider/spider.png");

    public RenderRideableSpider()
    {
        super(new ModelTamedSpider(), 1.0F);
        this.setRenderPassModel(new ModelTamedSpider());
    }

    /** A sitting spider is drawn squashed flat (the model lowers it and spreads its legs). */
    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTicks)
    {
        if (entity instanceof EntityFriendlySpider && ((EntityFriendlySpider) entity).isSitting())
        {
            GL11.glScalef(1.1F, 0.6F, 1.1F);
        }
    }

    protected float getDeathMaxRotation(EntityFriendlySpider p_77037_1_)
    {
        return 180.0F;
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityFriendlySpider p_77032_1_, int p_77032_2_, float p_77032_3_)
    {
        if (p_77032_2_ != 0)
        {
            return -1;
        }
        else
        {
            this.bindTexture(spiderEyesTextures);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

            if (p_77032_1_.isInvisible())
            {
                GL11.glDepthMask(false);
            }
            else
            {
                GL11.glDepthMask(true);
            }

            char c0 = 61680;
            int j = c0 % 65536;
            int k = c0 / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            return 1;
        }
    }

    /** The typed methods above are not overrides; these are, as in RenderSpider (without them the eyes never glow). */
    @Override
    protected float getDeathMaxRotation(EntityLivingBase entity)
    {
        return this.getDeathMaxRotation((EntityFriendlySpider) entity);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTicks)
    {
        return this.shouldRenderPass((EntityFriendlySpider) entity, pass, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityFriendlySpider p_110775_1_)
    {
        return spiderTextures;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return this.getEntityTexture((EntityFriendlySpider)p_110775_1_);
    }
}