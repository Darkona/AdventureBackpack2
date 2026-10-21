package com.darkona.adventurebackpack.client.gui;

import codechicken.lib.render.TextureUtils;
import com.darkona.adventurebackpack.common.RenderInfo;
import com.darkona.adventurebackpack.common.RenderInfoCache;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
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
    public GuiOverlay(Minecraft mc)
    {
        super();

        this.mc = mc;
        this.itemRender.renderWithColor = false;
        this.fontRendererObj = mc.fontRenderer;
    }

    private static final int BUFF_ICON_SIZE = 18;
    private static final int BUFF_ICON_SPACING = BUFF_ICON_SIZE + 2;
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
            ItemStack wearable = Wearing.getWearingWearable(player);
            if (wearable != null)
            {
                RenderInfo info = RenderInfoCache.get(wearable);
                int tanks = info.tankCount();
                if (tanks == 0) return;

                int textureHeight = 23;
                int textureWidth = 10;

                int xPos = screenWidth - (textureWidth*3) - 4 ;
                int yPos = (screenHeight) - textureHeight - 2;

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);

                int tankX = xPos;
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                for (int t = 0; t < tanks; t++)
                {
                    mc.renderEngine.bindTexture(OVERLAY_TEXTURE);
                    drawTexturedModalRect(tankX, yPos, 10, 0, textureWidth, textureHeight);
                    drawTank(info.tankFluid(t), info.tankCapacity(t), tankX + 1, yPos + 1, textureHeight - 2, textureWidth - 2);
                    ++tankX;
                    tankX += textureWidth;
                }
                if (info.kind == RenderInfo.KIND_BACKPACK)
                {
                    int u0 = 10, u1 = 10;
                    int xRight = xPos + textureWidth + 1;
                    ItemStack held = player.getHeldItem();
                    int tank = held != null && held.getItem() instanceof ItemHose ? ItemHose.getHoseTank(held) : -1;
                    if (tank > -1)
                    {
                        u0 = (tank == 0) ? 0 : 10;
                        u1 = (tank == 1) ? 0 : 10;
                    }
                    mc.renderEngine.bindTexture(OVERLAY_TEXTURE);

                    drawTexturedModalRect(xPos, yPos, u0, 0, textureWidth, textureHeight);
                    drawTexturedModalRect(xRight, yPos, u1, 0, textureWidth, textureHeight);
                    RenderHelper.enableStandardItemLighting();
                    RenderHelper.enableGUIStandardItemLighting();
                    GL11.glPushMatrix();
                    try
                    {
                        GL11.glTranslatef(xRight + textureWidth + 2, yPos, 0);
                        GL11.glScalef(0.5f, 0.5f, 0.5f);
                        drawItemStack(info.upperTool, 0, 0);
                        drawItemStack(info.lowerTool, 0, 16);
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
                drawBoatFuel((EntityInflatableBoat) player.ridingEntity, wearable != null);
            }
        }
    }

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
            drawTexturedModalRect(xPos, yPos, 20, 0, textureWidth, textureHeight);
            FluidTank fuel = boat.getDisplayFuelTank();
            drawTank(fuel.getFluid(), fuel.getCapacity(), xPos + 1, yPos + 1, textureHeight - 2, textureWidth - 2);
        } finally
        {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    private void drawTank(FluidStack fluid, int capacity, int startX, int startY, int height, int width)
    {
        int liquidPerPixel = Math.max(1, capacity / Math.max(1, height));
        if (fluid != null && fluid.getFluid() != null)
        {
            try
            {
                IIcon icon = fluid.getFluid().getStillIcon();
                if (icon == null) return;
                TextureUtils.bindAtlas(fluid.getFluid().getSpriteNumber());
                GuiTank.drawFluidColumn(startX, startY, width, height, icon, fluid.amount, liquidPerPixel, 1, fluid.getFluid().getColor(fluid));
            } catch (Exception oops)
            {
                LogHelper.error("Exception while trying to render the fluid in the overlay");
            }
        }
    }

    private void drawItemStack(ItemStack stack, int x, int y)
    {
        if(stack == null)return;
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
