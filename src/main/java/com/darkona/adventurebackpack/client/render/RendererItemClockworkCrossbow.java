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

/**
 * Draws the Clockwork Crossbow model in the hand, on other players, in the inventory and on the ground.
 * <p>
 * Every render type arrives in a different GL frame (see {@code ItemRenderer}, {@code RenderBiped},
 * {@code ForgeHooksClient}); each case first undoes the vanilla transform of that frame and then places the model,
 * whose frame is +Y up and -Z forward, where the weapon should be. The first person hold is a two-handed aim: grip
 * low right, muzzle toed in towards the centre, the whole weapon kicking up and back for a few ticks after a shot.
 * Frame by frame this reads cached fields and does arithmetic (performance contract, tier 4); GL state is restored
 * in {@code finally}.
 */
@SideOnly(Side.CLIENT)
public class RendererItemClockworkCrossbow implements IItemRenderer
{
    /** One model unit in blocks. */
    private static final float MODEL_SCALE = 0.0625F;
    /** The hand holds the grip 3.5 units under the model origin and 6 units behind it. */
    private static final float GRIP_CENTER = 3.5F;
    private static final float GRIP_Z = 6F;
    /** Size of the weapon in another entity's hand. */
    private static final float THIRD_PERSON_SCALE = 0.5F;

    /** Partial tick of the frame being drawn, written by {@code CrossbowRenderHandler} at the start of each frame. */
    public static float partialTicks;

    private final ModelClockworkCrossbow model = new ModelClockworkCrossbow();
    private final ResourceLocation texture = Resources.modelTextures("clockworkCrossbow");
    private final CrossbowRenderState state = new CrossbowRenderState();
    /** Bipod of the local player's crossbow: where it started moving from, towards what, and when (world ticks). */
    private float bipodFrom;
    private boolean bipodTarget;
    private long bipodSince = -1L;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return type != ItemRenderType.FIRST_PERSON_MAP;
    }

    /**
     * The block helpers give a centred, lit frame for every type, except {@code BLOCK_3D} in the hand: with it
     * {@code RenderPlayer} would take its block branch while {@code RenderBiped} (zombies) takes the full-3D one, so
     * players and mobs would get different frames. Without it both use the full-3D branch ({@link #thirdPerson}).
     */
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
            model.render(MODEL_SCALE, ammo, capacity, sinceShot, reload, mode, bipod, hookLoaded);
        } finally
        {
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPopMatrix();
        }
    }

    /**
     * The local player's bipod folds and unfolds over {@link CrossbowAnimation#BIPOD_TICKS}: when the target changes
     * the current pose becomes the start of the next move. Other players' bipods snap (one field set per renderer).
     */
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

    /**
     * Frame on entry: the view frame moved to the hand, turned 45 degrees about Y, scaled 0.4, shifted by the block
     * helper. Undo the shift and the turn, then hold the weapon lower and nearer the centre, muzzle toed in 8
     * degrees; the recoil pushes it back and tips the muzzle up.
     */
    private static void firstPerson(float recoil)
    {
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glRotatef(-45F, 0F, 1F, 0F);
        GL11.glTranslatef(-0.35F, -0.15F, 0.05F + recoil * 0.12F);
        GL11.glRotatef(8F, 0F, 1F, 0F);
        GL11.glRotatef(recoil * 6F, 1F, 0F, 0F);
    }

    /**
     * Frame on entry: the right arm's frame at the hand, scaled (0.625, -0.625, 0.625), turned -100 about X and 45
     * about Y (the full-3D item branch shared by {@code RenderPlayer} and {@code RenderBiped}), shifted by the block
     * helper. Undo all of it back to the arm's own frame (pixels: X left, Y down the arm, Z back), move to the middle
     * of the hand and turn to the weapon's look frame: for a player posed by {@link CrossbowHoldPose} that is the
     * look itself (the weapon points where the head looks whatever the arm's angle); for anything else, along the
     * arm with the top towards the front of the arm. Then turn to the model's frame (+Y up, -Z muzzle, a half turn
     * about Z, no mirror) and put the grip in the hand.
     */
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

    /**
     * Frame on entry: the GUI block frame (10 px per unit, mirrored in Z, turned 210 about X and -45 about Y).
     * Undo it, flip to Y up, and show the right side from above with the muzzle up and to the left.
     */
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

    /** Frame on entry: the item's position, Y up, spinning and bobbing, scaled 0.5. Lie flat and a little raised. */
    private static void entity()
    {
        GL11.glTranslatef(0F, 0.25F, 0F);
        GL11.glScalef(0.8F, 0.8F, 0.8F);
    }
}
