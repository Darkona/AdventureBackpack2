package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.entity.EntityGrapple;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Placeholder look of the grappling hook: a small iron block for the hook and a straight rope from the owner's
 * crossbow to it. The visuals agent replaces the hook with the model's {@code hookHead}/{@code hookTooth} parts (see
 * {@code notes/ballesta-1.1-interfaz.md}). The rope end follows the vanilla fishing line ({@code RenderFish}): in first
 * person from the right of the view, else from the right hand of the body. No allocation per frame; GL state is
 * restored in {@code finally} (performance contract, tier 4 and rule 7).
 */
@SideOnly(Side.CLIENT)
public class RenderGrapple extends Render
{
    private static final float HOOK_HALF = 0.1F;
    private static final int HOOK_COLOR = 0x5A5A5E;
    private static final int ROPE_COLOR = 0x6B4A2B;

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks)
    {
        if (!(entity instanceof EntityGrapple)) return;
        EntityGrapple hook = (EntityGrapple) entity;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LINE_BIT | GL11.GL_CURRENT_BIT);
        try
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glTranslated(x, y, z);
            drawHook();
            EntityPlayer owner = hook.getOwner();
            if (owner != null) drawRope(hook, owner, partialTicks);
        } finally
        {
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    private static void drawHook()
    {
        Tessellator t = Tessellator.instance;
        float h = HOOK_HALF;
        t.startDrawingQuads();
        t.setColorOpaque_I(HOOK_COLOR);
        // bottom, top
        t.addVertex(-h, -h, -h); t.addVertex(h, -h, -h); t.addVertex(h, -h, h); t.addVertex(-h, -h, h);
        t.addVertex(-h, h, h); t.addVertex(h, h, h); t.addVertex(h, h, -h); t.addVertex(-h, h, -h);
        // north, south
        t.addVertex(-h, h, -h); t.addVertex(h, h, -h); t.addVertex(h, -h, -h); t.addVertex(-h, -h, -h);
        t.addVertex(-h, -h, h); t.addVertex(h, -h, h); t.addVertex(h, h, h); t.addVertex(-h, h, h);
        // west, east
        t.addVertex(-h, -h, -h); t.addVertex(-h, -h, h); t.addVertex(-h, h, h); t.addVertex(-h, h, -h);
        t.addVertex(h, h, -h); t.addVertex(h, h, h); t.addVertex(h, -h, h); t.addVertex(h, -h, -h);
        t.draw();
    }

    /** Straight rope from the hook (the origin here) to the owner's crossbow. */
    private void drawRope(EntityGrapple hook, EntityPlayer owner, float pt)
    {
        Minecraft mc = Minecraft.getMinecraft();
        double ownerX = owner.prevPosX + (owner.posX - owner.prevPosX) * pt;
        double ownerY = owner.prevPosY + (owner.posY - owner.prevPosY) * pt;
        double ownerZ = owner.prevPosZ + (owner.posZ - owner.prevPosZ) * pt;
        double handX;
        double handY;
        double handZ;
        if (owner == mc.thePlayer && renderManager.options.thirdPersonView == 0)
        {
            // Fishing line offset of RenderFish, a little lower for the crossbow: rotated by pitch, then by yaw.
            double pitch = -(owner.prevRotationPitch + (owner.rotationPitch - owner.prevRotationPitch) * pt) * Math.PI / 180.0;
            double yawRad = -(owner.prevRotationYaw + (owner.rotationYaw - owner.prevRotationYaw) * pt) * Math.PI / 180.0;
            double vx = -0.4;
            double vy = -0.05;
            double vz = 0.8;
            double cos = Math.cos(pitch);
            double sin = Math.sin(pitch);
            double ry = vy * cos + vz * sin;
            double rz = vz * cos - vy * sin;
            cos = Math.cos(yawRad);
            sin = Math.sin(yawRad);
            double rx = vx * cos + rz * sin;
            rz = rz * cos - vx * sin;
            handX = ownerX + rx;
            handY = ownerY + ry;
            handZ = ownerZ + rz;
        } else
        {
            double body = (owner.prevRenderYawOffset + (owner.renderYawOffset - owner.prevRenderYawOffset) * pt) * Math.PI / 180.0;
            double sin = MathHelper.sin((float) body);
            double cos = MathHelper.cos((float) body);
            double eyes = owner == mc.thePlayer ? 0.0 : owner.getEyeHeight();
            handX = ownerX - cos * 0.35 - sin * 0.85;
            handY = ownerY + eyes - 0.45;
            handZ = ownerZ - sin * 0.35 + cos * 0.85;
        }
        double hookX = hook.prevPosX + (hook.posX - hook.prevPosX) * pt;
        double hookY = hook.prevPosY + (hook.posY - hook.prevPosY) * pt;
        double hookZ = hook.prevPosZ + (hook.posZ - hook.prevPosZ) * pt;

        GL11.glLineWidth(2.0F);
        Tessellator t = Tessellator.instance;
        t.startDrawing(GL11.GL_LINES);
        t.setColorOpaque_I(ROPE_COLOR);
        t.addVertex(0, 0, 0);
        t.addVertex(handX - hookX, handY - hookY, handZ - hookZ);
        t.draw();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }
}
