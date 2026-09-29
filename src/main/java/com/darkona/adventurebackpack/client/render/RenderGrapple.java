package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.client.models.ModelGrapple;
import com.darkona.adventurebackpack.entity.EntityGrapple;
import com.darkona.adventurebackpack.reference.ModInfo;
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
 * The grappling hook: the {@link ModelGrapple} (eye, shank, four barbed teeth) turned to the yaw and pitch it was
 * fired with, as {@code RenderArrow} does, and a textured rope from its eye to the owner's crossbow. The rope is a
 * ribbon of quads that faces the camera, one tile of the rope strip per half block, with a slight sag. The rope end
 * follows the vanilla fishing line ({@code RenderFish}): in first person from the right of the view, else from the
 * right hand of the body. No allocation per frame; GL state is restored in {@code finally} (performance contract,
 * tier 4 and rule 7).
 */
@SideOnly(Side.CLIENT)
public class RenderGrapple extends Render
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "textures/entity/grapple.png");
    private static final float MODEL_SCALE = 0.0625F;
    /** The rope leaves the model at the eye, 5 units behind the origin along the shank. */
    private static final double EYE_OFFSET = 5.0 * MODEL_SCALE;
    /** Rope strip on the texture: u 28..32 of 32, the full height, one tile per {@link #TILE} blocks of rope. */
    private static final double ROPE_U0 = 28.0 / 32.0;
    private static final double ROPE_U1 = 1.0;
    private static final double TILE = 0.5;
    private static final int MAX_TILES = 160;
    private static final double HALF_WIDTH = 0.03;
    /** Sag in blocks at the middle of a rope one block long (scaled by the length, only while hooked). */
    private static final double SAG = 0.04;

    private final ModelGrapple model = new ModelGrapple();

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks)
    {
        if (!(entity instanceof EntityGrapple)) return;
        EntityGrapple hook = (EntityGrapple) entity;
        float hookYaw = hook.prevRotationYaw + (hook.rotationYaw - hook.prevRotationYaw) * partialTicks;
        float hookPitch = hook.prevRotationPitch + (hook.rotationPitch - hook.prevRotationPitch) * partialTicks;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
        try
        {
            GL11.glTranslated(x, y, z);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            bindTexture(TEXTURE);
            GL11.glPushMatrix();
            try
            {
                // the hook keeps the owner's look angles: his look vector is (-sin yaw, -sin pitch, cos yaw), so
                // the +X shank needs -yaw - 90 around Y and -pitch around Z (an arrow's own yaw is the opposite)
                GL11.glRotatef(-hookYaw - 90F, 0F, 1F, 0F);
                GL11.glRotatef(-hookPitch, 0F, 0F, 1F);
                model.render(MODEL_SCALE);
            } finally
            {
                GL11.glPopMatrix();
            }
            EntityPlayer owner = hook.getOwner();
            if (owner != null) drawRope(hook, owner, hookYaw, hookPitch, partialTicks);
        } finally
        {
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    /** Rope from the hook's eye (the origin is the hook) to the owner's crossbow. */
    private void drawRope(EntityGrapple hook, EntityPlayer owner, float hookYaw, float hookPitch, float pt)
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

        // the eye of the hook, behind the origin along the fired direction (the shank points where it flew)
        double yawRad = hookYaw * Math.PI / 180.0;
        double pitchRad = hookPitch * Math.PI / 180.0;
        double cosPitch = Math.cos(pitchRad);
        double eyeX = Math.sin(yawRad) * cosPitch * EYE_OFFSET;
        double eyeY = Math.sin(pitchRad) * EYE_OFFSET;
        double eyeZ = -Math.cos(yawRad) * cosPitch * EYE_OFFSET;

        double dx = handX - hookX - eyeX;
        double dy = handY - hookY - eyeY;
        double dz = handZ - hookZ - eyeZ;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1.0E-3) return;

        // a ribbon facing the camera: side = normalize(rope x toCamera) * half width
        double camX = renderManager.viewerPosX - hookX - eyeX;
        double camY = renderManager.viewerPosY - hookY - eyeY;
        double camZ = renderManager.viewerPosZ - hookZ - eyeZ;
        double sx = dy * camZ - dz * camY;
        double sy = dz * camX - dx * camZ;
        double sz = dx * camY - dy * camX;
        double sideLength = Math.sqrt(sx * sx + sy * sy + sz * sz);
        if (sideLength < 1.0E-6)
        {
            sx = 0;
            sy = HALF_WIDTH;
            sz = 0;
        } else
        {
            sx *= HALF_WIDTH / sideLength;
            sy *= HALF_WIDTH / sideLength;
            sz *= HALF_WIDTH / sideLength;
        }
        double sag = hook.isHooked() ? SAG * length : 0.0;
        int tiles = (int) Math.ceil(length / TILE);
        if (tiles < 1) tiles = 1;
        if (tiles > MAX_TILES) tiles = MAX_TILES;

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.setColorOpaque_F(1F, 1F, 1F);
        double px0 = eyeX;
        double py0 = eyeY;
        double pz0 = eyeZ;
        for (int i = 1; i <= tiles; i++)
        {
            double f = (double) i / tiles;
            double px1 = eyeX + dx * f;
            double py1 = eyeY + dy * f - sag * 4.0 * f * (1.0 - f);
            double pz1 = eyeZ + dz * f;
            t.addVertexWithUV(px0 - sx, py0 - sy, pz0 - sz, ROPE_U0, 0.0);
            t.addVertexWithUV(px0 + sx, py0 + sy, pz0 + sz, ROPE_U1, 0.0);
            t.addVertexWithUV(px1 + sx, py1 + sy, pz1 + sz, ROPE_U1, 1.0);
            t.addVertexWithUV(px1 - sx, py1 - sy, pz1 - sz, ROPE_U0, 1.0);
            px0 = px1;
            py0 = py1;
            pz0 = pz1;
        }
        t.draw();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return TEXTURE;
    }
}
