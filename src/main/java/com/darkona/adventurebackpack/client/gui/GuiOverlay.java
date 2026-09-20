package com.darkona.adventurebackpack.client.gui;

import codechicken.lib.render.TextureUtils;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import com.darkona.adventurebackpack.inventory.IInventoryTanks;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created on 09/01/2015
 *
 * @author Darkona
 */
public class GuiOverlay extends Gui
{
    private Minecraft mc;
    private int screenWidth;
    private int screenHeight;
    protected static RenderItem itemRender = new RenderItem();
    protected FontRenderer fontRendererObj;
    ScaledResolution resolution;
    private static final ResourceLocation INVENTORY_TEXTURE = new ResourceLocation("textures/gui/container/inventory.png");
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "textures/gui/overlay.png");
    private int lastInvReloadTick = Integer.MIN_VALUE;
    private IInventoryTanks overlayInv;
    private ItemStack overlayStack;
    public GuiOverlay(Minecraft mc)
    {
        super();

        // We need this to invoke the render engine.
        this.mc = mc;
        this.itemRender.renderWithColor = false;
        this.fontRendererObj = mc.fontRenderer;
    }

    private static final int BUFF_ICON_SIZE = 18;
    private static final int BUFF_ICON_SPACING = BUFF_ICON_SIZE + 2; // 2 pixels between buff icons
    private static final int BUFF_ICON_BASE_U_OFFSET = 0;
    private static final int BUFF_ICON_BASE_V_OFFSET = 198;
    private static final int BUFF_ICONS_PER_ROW = 8;


    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderExperienceBar(RenderGameOverlayEvent.Post event)
    {
        if(event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE)
        {
            return;
        }
        resolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        screenWidth = resolution.getScaledWidth();
        screenHeight = resolution.getScaledHeight();
        if(ConfigHandler.STATUS_OVERLAY)
        {
            int xPos = 2;
            int yPos = 2;
            Collection collection = this.mc.thePlayer.getActivePotionEffects();
            if (!collection.isEmpty())
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.mc.renderEngine.bindTexture(INVENTORY_TEXTURE);

                for (Iterator iterator = this.mc.thePlayer.getActivePotionEffects()
                        .iterator(); iterator.hasNext(); xPos += BUFF_ICON_SPACING)
                {
                    PotionEffect potioneffect = (PotionEffect) iterator.next();
                    int potionId = potioneffect.getPotionID();
                    // Effects from other mods can carry an id with no Potion registered on this client (#108)
                    Potion potion = potionId >= 0 && potionId < Potion.potionTypes.length ? Potion.potionTypes[potionId] : null;

                    if (potion != null && potion.hasStatusIcon())
                    {
                        int iconIndex = potion.getStatusIconIndex();
                        this.drawTexturedModalRect(
                                xPos, yPos,
                                BUFF_ICON_BASE_U_OFFSET + iconIndex % BUFF_ICONS_PER_ROW * BUFF_ICON_SIZE, BUFF_ICON_BASE_V_OFFSET + iconIndex / BUFF_ICONS_PER_ROW * BUFF_ICON_SIZE,
                                BUFF_ICON_SIZE, BUFF_ICON_SIZE);
                    }
                }
            }
        }

        if(ConfigHandler.TANKS_OVERLAY)
        {
            EntityPlayer player= mc.thePlayer;
            if(Wearing.isWearingWearable(player))
            {
                // One inventory per worn stack, reloaded from its NBT every few ticks: building it reads every
                // slot and tank, which must not happen on every frame (a sync replaces the stack, so it is rebuilt)
                ItemStack wearable = Wearing.getWearingWearable(player);
                int now = player.ticksExisted;
                if (overlayInv == null || wearable != overlayStack)
                {
                    overlayInv = Wearing.getWearableInv(player);
                    overlayStack = wearable;
                    lastInvReloadTick = now;
                } else if (com.darkona.adventurebackpack.util.TickThrottle.due(lastInvReloadTick, now, 5))
                {
                    lastInvReloadTick = now;
                    overlayInv.openInventory();
                }
                IInventoryTanks inv = overlayInv;
                if (inv == null) return;

                int textureHeight = 23;
                int textureWidth = 10;

                int xPos = screenWidth - (textureWidth*3) - 4 ;
                //int yPos = ((screenHeight / 3) * 2) - textureHeight - 2;
                int yPos = (screenHeight) - textureHeight - 2;

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);

                int tankX = xPos;
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                for (FluidTank tank : inv.getTanksArray())
                {

                    mc.renderEngine.bindTexture(OVERLAY_TEXTURE);
                    drawTexturedModalRect(tankX, yPos, 10, 0, textureWidth, textureHeight);
                    drawTank(tank, tankX + 1, yPos + 1, textureHeight - 2, textureWidth - 2);
                    ++tankX;
                    tankX += textureWidth;
                }
                if (Wearing.isWearingBackpack(player))
                {
                    int u[] = {10, 10};
                    int v[] = {0, 0};
                    int[] xStart = {xPos, xPos + textureWidth + 1};
                    int[] yStart = {yPos, yPos};
                    short tank = -1;
                    if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemHose)
                    {
                        tank = (short) (ItemHose.getHoseTank(player.getHeldItem()));
                    }
                    if (tank > -1)
                    {
                        u[0] = (tank == 0) ? 0 : 10;
                        u[1] = (tank == 1) ? 0 : 10;
                    }
                    mc.renderEngine.bindTexture(OVERLAY_TEXTURE);

                    //Left Tank
                    drawTexturedModalRect(xStart[0], yStart[0], u[0], v[0], textureWidth, textureHeight);
                    //Right Tank
                    drawTexturedModalRect(xStart[1], yStart[0], u[1], v[1], textureWidth, textureHeight);
                    RenderHelper.enableStandardItemLighting();
                    RenderHelper.enableGUIStandardItemLighting();
                    GL11.glPushMatrix();
                    try
                    {
                        GL11.glTranslatef(xStart[1] + textureWidth + 2, yStart[0], 0);
                        GL11.glScalef(0.5f, 0.5f, 0.5f);
                        drawItemStack(inv.getStackInSlot(Constants.upperTool), 0, 0);
                        drawItemStack(inv.getStackInSlot(Constants.lowerTool), 0, 16);
                    } finally
                    {
                        GL11.glPopMatrix();
                        RenderHelper.disableStandardItemLighting();
                    }

                }
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                GL11.glDisable(GL11.GL_BLEND);
            }
            if (player.ridingEntity instanceof EntityInflatableBoat && ((EntityInflatableBoat) player.ridingEntity).isMotorized())
            {
                drawBoatFuel((EntityInflatableBoat) player.ridingEntity, Wearing.isWearingWearable(player));
            }
        }
    }

    /** Fuel tank of the motorized boat the player rides, left of the worn pack's tanks (read from the DataWatcher). */
    private void drawBoatFuel(EntityInflatableBoat boat, boolean wearing)
    {
        int textureHeight = 23;
        int textureWidth = 10;
        int xPos = screenWidth - textureWidth - 4 - (wearing ? textureWidth * 3 + 4 : 0);
        int yPos = screenHeight - textureHeight - 2;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        try
        {
            mc.renderEngine.bindTexture(OVERLAY_TEXTURE);
            drawTexturedModalRect(xPos, yPos, 10, 0, textureWidth, textureHeight);
            drawTank(boat.getDisplayFuelTank(), xPos + 1, yPos + 1, textureHeight - 2, textureWidth - 2);
        } finally
        {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    private void drawTank(FluidTank tank, int startX, int startY, int height, int width)
    {
        int liquidPerPixel = Math.max(1, tank.getCapacity() / Math.max(1, height));
        FluidStack fluid = tank.getFluid();
        if (fluid != null && fluid.getFluid() != null)
        {
            try
            {
                IIcon icon = fluid.getFluid().getStillIcon();
                if (icon == null) return;
                TextureUtils.bindAtlas(fluid.getFluid().getSpriteNumber());
                GuiTank.drawFluidColumn(startX, startY, width, height, icon, fluid.amount, liquidPerPixel, 1);
            } catch (Exception oops)
            {
                LogHelper.error("Exception while trying to render the fluid in the overlay");
            }
        }
    }

    private void drawItemStack(ItemStack stack, int x, int y)
    {
        if(stack == null)return;
        // Items from other mods (e.g. GregTech tools) may throw or leave GL state dirty: contain both (#110).
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try
        {
            GL11.glTranslatef(0.0F, 0.0F, 32.0F);
            this.zLevel = 200.0F;
            itemRender.zLevel = 200.0F;
            FontRenderer font = stack.getItem().getFontRenderer(stack);
            if (font == null) font = fontRendererObj;
            itemRender.renderItemIntoGUI(font,mc.getTextureManager(),stack,x,y);
        } catch (Throwable t)
        {
            LogHelper.error("Could not render " + stack.getItem().getUnlocalizedName() + " in the overlay: " + t);
        } finally
        {
            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
