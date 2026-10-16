package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.client.GrappleClient;
import com.darkona.adventurebackpack.client.models.ModelClockworkCrossbow;
import com.darkona.adventurebackpack.common.CrossbowAnimation;
import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.item.ItemCrossbow;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RendererItemClockworkCrossbow implements IItemRenderer
{
    private static final float MODEL_SCALE = 0.0625F;
    private static final float GRIP_CENTER = 3.5F;
    private static final float GRIP_Z = 6F;
    private static final float THIRD_PERSON_SCALE = 0.5F;

    public static float partialTicks;

    private final ModelClockworkCrossbow model = new ModelClockworkCrossbow();
    private final ResourceLocation texture = Resources.modelTextures("clockworkCrossbow");
    private final CrossbowRenderState state = new CrossbowRenderState();
    private float bipodFrom;
    private boolean bipodTarget;
    private long bipodSince = -1L;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return type != ItemRenderType.FIRST_PERSON_MAP;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return !(type == ItemRenderType.EQUIPPED && helper == ItemRendererHelper.BLOCK_3D);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.theWorld;
        long now = world != null ? world.getTotalWorldTime() : 0L;
        state.update(item);
        float sinceShot = CrossbowAnimation.sinceShot(now, partialTicks, state.lastShot);
        float reload = CrossbowAnimation.reloadProgress(now, partialTicks, state.lastReload, ConfigHandler.CROSSBOW_RELOAD_TICKS);
        float recoil = CrossbowAnimation.recoil(sinceShot);
        int capacity = ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE;
        int ammo = CrossbowLogic.clampAmmo(state.ammo, capacity);
        if (state.fed)
        {
            ammo = CrossbowAnimation.feedAmmo();
            capacity = CrossbowAnimation.feedCapacity(state.quiverShots);
        }
        int mode = ItemCrossbow.effectiveMode(state.mode);
        EntityPlayer holder = (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON)
                && data.length > 1 && data[1] instanceof EntityPlayer ? (EntityPlayer) data[1] : null;
        float bipod = 0F;
        boolean hookLoaded = true;
        if (holder != null)
        {
            boolean deployed = mode == CrossbowLogic.MODE_SNIPER && ItemCrossbow.isBipodDeployed(holder);
            if (holder == mc.thePlayer)
            {
                bipod = localBipodPose(deployed, now, partialTicks);
                hookLoaded = !GrappleClient.hasLocalHook();
            } else
            {
                bipod = deployed ? 1F : 0F;
            }
        }

        mc.renderEngine.bindTexture(texture);
        GL11.glPushMatrix();
        try
        {
            GL11.glColor4f(1F, 1F, 1F, 1F);
            switch (type)
            {
                case EQUIPPED_FIRST_PERSON:
                    firstPerson(recoil);
                    break;
                case EQUIPPED:
                    thirdPerson(CrossbowHoldPose.isPosed(holder));
                    break;
                case INVENTORY:
                    inventory();
                    break;
                case ENTITY:
                    entity();
                    break;
                default:
                    return;
            }
            model.render(MODEL_SCALE, ammo, capacity, sinceShot, reload, state.reloadSwap, mode, bipod, hookLoaded);
        } finally
        {
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPopMatrix();
        }
    }

    private float localBipodPose(boolean deployed, long now, float pt)
    {
        if (bipodSince < 0L)
        {
            bipodFrom = deployed ? 1F : 0F;
            bipodTarget = deployed;
            bipodSince = now;
        } else if (deployed != bipodTarget)
        {
            bipodFrom = CrossbowAnimation.bipodPose(bipodFrom, bipodTarget, now - bipodSince + pt);
            bipodTarget = deployed;
            bipodSince = now;
        }
        return CrossbowAnimation.bipodPose(bipodFrom, bipodTarget, now - bipodSince + pt);
    }

    private static void firstPerson(float recoil)
    {
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glRotatef(-45F, 0F, 1F, 0F);
        GL11.glTranslatef(-0.35F, -0.15F, 0.05F + recoil * 0.12F);
        GL11.glRotatef(8F, 0F, 1F, 0F);
        GL11.glRotatef(recoil * 6F, 1F, 0F, 0F);
    }

    private static void thirdPerson(boolean posed)
    {
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glRotatef(-45F, 0F, 1F, 0F);
        GL11.glRotatef(100F, 1F, 0F, 0F);
        GL11.glScalef(1F / 0.625F, -1F / 0.625F, 1F / 0.625F);
        GL11.glTranslatef(0F, -MODEL_SCALE, -MODEL_SCALE);
        if (posed)
        {
            CrossbowHoldPose.armToWeapon(MODEL_SCALE);
        } else
        {
            GL11.glRotatef(90F, 1F, 0F, 0F);
        }
        GL11.glRotatef(180F, 0F, 0F, 1F);
        GL11.glScalef(THIRD_PERSON_SCALE, THIRD_PERSON_SCALE, THIRD_PERSON_SCALE);
        GL11.glTranslatef(0F, GRIP_CENTER * MODEL_SCALE, -GRIP_Z * MODEL_SCALE);
    }

    private static void inventory()
    {
        GL11.glRotatef(45F, 0F, 1F, 0F);
        GL11.glRotatef(-210F, 1F, 0F, 0F);
        GL11.glScalef(1F, 1F, -1F);
        GL11.glRotatef(180F, 0F, 0F, 1F);
        GL11.glRotatef(30F, 0F, 0F, 1F);
        GL11.glRotatef(-70F, 0F, 1F, 0F);
        GL11.glRotatef(20F, 1F, 0F, 0F);
        GL11.glScalef(0.6F, 0.6F, 0.6F);
        GL11.glTranslatef(0F, -2F * MODEL_SCALE, -2F * MODEL_SCALE);
    }

    private static void entity()
    {
        GL11.glTranslatef(0F, 0.25F, 0F);
        GL11.glScalef(0.8F, 0.8F, 0.8F);
    }
}
