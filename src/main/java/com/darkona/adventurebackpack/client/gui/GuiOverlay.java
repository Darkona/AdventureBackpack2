package com.darkona.adventurebackpack.client.gui;

import java.util.Collection;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import codechicken.lib.render.TextureUtils;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.IInventoryTanks;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Wearing;

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
    private static RenderItem itemRender = new RenderItem();
    private FontRenderer fontRendererObj;
    private ScaledResolution resolution;

    public GuiOverlay(Minecraft mc)
    {
        super();

        // We need this to invoke the render engine.
        this.mc = mc;
        this.itemRender.renderWithColor = false;
        this.fontRendererObj = mc.fontRenderer;
    }

    private static final int BUFF_ICON_SIZE = 18;
    private static final int BUFF_ICON_SPACING = 2; // 2 pixels between buff icons
    private static final int BUFF_ICON_BASE_U_OFFSET = 0;
    private static final int BUFF_ICON_BASE_V_OFFSET = 198;
    private static final int BUFF_ICONS_PER_ROW = 8;

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderExperienceBar(RenderGameOverlayEvent.Post event)
    {
        if (event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE)
        {
            return;
        }
        resolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        screenWidth = resolution.getScaledWidth();
        screenHeight = resolution.getScaledHeight();
        if (ConfigHandler.statusOverlay)
        {
            int xStep = ConfigHandler.statusOverlayLeft
                    ? BUFF_ICON_SIZE + BUFF_ICON_SPACING
                    : - BUFF_ICON_SIZE - BUFF_ICON_SPACING;
            int xPos = ConfigHandler.statusOverlayLeft
                    ? ConfigHandler.statusOverlayIndentH
                    : screenWidth - BUFF_ICON_SIZE - ConfigHandler.statusOverlayIndentH;
            int yPos = ConfigHandler.statusOverlayTop
                    ? ConfigHandler.statusOverlayIndentV
                    : screenHeight - BUFF_ICON_SIZE - ConfigHandler.statusOverlayIndentV;

            Collection activePotionEffects = this.mc.thePlayer.getActivePotionEffects();
            if (!activePotionEffects.isEmpty())
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));

                for (Iterator activeEffect = activePotionEffects.iterator(); activeEffect.hasNext(); xPos += xStep)
                {
                    PotionEffect potionEffect = (PotionEffect) activeEffect.next();
                    Potion potion = Potion.potionTypes[potionEffect.getPotionID()];

                    if (potion.hasStatusIcon())
                    {
                        int iconIndex = potion.getStatusIconIndex();
                        this.drawTexturedModalRect(
                                xPos, yPos,
                                BUFF_ICON_BASE_U_OFFSET + iconIndex % BUFF_ICONS_PER_ROW * BUFF_ICON_SIZE,
                                BUFF_ICON_BASE_V_OFFSET + iconIndex / BUFF_ICONS_PER_ROW * BUFF_ICON_SIZE,
                                BUFF_ICON_SIZE, BUFF_ICON_SIZE);
                    }
                }
            }
        }

        if (ConfigHandler.tanksOverlay)
        {
            EntityPlayer player = mc.thePlayer;
            if (Wearing.isWearingWearable(player))
            {
                IInventoryTanks inv = Wearing.getWearingWearableInv(player);
                assert inv != null;
                inv.openInventory();

                int textureHeight = 23;
                int textureWidth = 10;

                int xPos = ConfigHandler.tanksOverlayRight
                        ? screenWidth - (textureWidth * 3) - ConfigHandler.tanksOverlayIndentH
                        : ConfigHandler.tanksOverlayIndentH;
                int yPos = ConfigHandler.tanksOverlayBottom
                        ? screenHeight - textureHeight - ConfigHandler.tanksOverlayIndentV
                        : ConfigHandler.tanksOverlayIndentV;

                int tankX = xPos;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                for (FluidTank tank : inv.getTanksArray())
                {
                    mc.renderEngine.bindTexture(new ResourceLocation(ModInfo.MOD_ID, "textures/gui/overlay.png"));
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
                    if (Wearing.isHoldingHose(player))
                    {
                        tank = (short) (ItemHose.getHoseTank(player.getHeldItem()));
                    }
                    if (tank > -1)
                    {
                        u[0] = (tank == 0) ? 0 : 10;
                        u[1] = (tank == 1) ? 0 : 10;
                    }
                    mc.renderEngine.bindTexture(new ResourceLocation(ModInfo.MOD_ID, "textures/gui/overlay.png"));
                    drawTexturedModalRect(xStart[0], yStart[0], u[0], v[0], textureWidth, textureHeight); //Left Tank
                    drawTexturedModalRect(xStart[1], yStart[0], u[1], v[1], textureWidth, textureHeight); //Right Tank
                    RenderHelper.enableStandardItemLighting();
                    RenderHelper.enableGUIStandardItemLighting();
                    GL11.glPushMatrix();
                    GL11.glTranslatef(xStart[1] + textureWidth + 2, yStart[0], 0);
                    GL11.glScalef(0.5f, 0.5f, 0.5f);
                    if (ConfigHandler.enableToolsRender)
                    {
                        drawItemStack(inv.getStackInSlot(Constants.UPPER_TOOL), 0, 0);
                        drawItemStack(inv.getStackInSlot(Constants.LOWER_TOOL), 0, 16);
                    }
                    GL11.glPopMatrix();
                    RenderHelper.disableStandardItemLighting();
                }
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }
    }

    private void drawTank(FluidTank tank, int startX, int startY, int height, int width)
    {
        int liquidPerPixel = tank.getCapacity() / height;
        FluidStack fluid = tank.getFluid();
        if (fluid != null)
        {
            try
            {
                IIcon icon = fluid.getFluid().getStillIcon();
                TextureUtils.bindAtlas(fluid.getFluid().getSpriteNumber());
                int top = startY + height - (fluid.amount / liquidPerPixel);
                for (int j = startY + height - 1; j >= top; j--)
                {
                    for (int i = startX; i <= startX + width - 1; i++)
                    {
                        GL11.glPushMatrix();
                        if (j >= top + 5)
                        {
                            GL11.glColor4f(0.9f, 0.9f, 0.9f, 1);
                        } else
                        {
                            GL11.glColor4f(1, 1, 1, 1);
                        }
                        GuiTank.drawFluidPixelFromIcon(i, j, icon, 1, 1, 0, 0, 0, 0, 1);
                        GL11.glPopMatrix();
                    }
                }
            } catch (Exception oops)
            {
                LogHelper.error("Exception while trying to render the fluid in the GUI");
            }
        }
    }

    private void drawItemStack(ItemStack stack, int x, int y)
    {
        if (stack == null) return;
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        FontRenderer font = null;
        font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = fontRendererObj;
        itemRender.renderItemIntoGUI(font, mc.getTextureManager(), stack, x, y);
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }
}
