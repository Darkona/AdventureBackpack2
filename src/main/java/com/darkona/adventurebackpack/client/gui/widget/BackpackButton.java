package com.darkona.adventurebackpack.client.gui.widget;

import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public final class BackpackButton
{
    public static final int SIZE = 18;
    public static final int BED = 0, EQUIP = 1, UNEQUIP = 2;

    private static final ResourceLocation WIDGETS = Resources.guiTextures("guiBackpackWidgets");
    private static final ResourceLocation CLASSIC = Resources.guiTextures("guiBackpackNew");
    private static final int BED_U = 1, BED_HOVER_U = 20, BED_V = 227;
    private static final int FACE_U = 0, FACE_HOVER_U = 18, FACE_PRESSED_U = 36, FACE_V = 0;
    private static final int ARROW_UP_U = 54, ARROW_DOWN_U = 60, ARROW_V = 0, ARROW_W = 5, ARROW_H = 7;
    private static final int ICON_X = 1, ICON_Y = 4, ARROW_X = 12, ARROW_Y = 6;
    private static final float ICON_SCALE = 0.625f;

    private final int x;
    private final int y;
    private final int mode;
    private final ItemStack icon;

    public BackpackButton(int x, int y, int mode)
    {
        this.x = x;
        this.y = y;
        this.mode = mode;
        icon = mode != BED && ModItems.adventureBackpack != null
                ? BackpackNames.setBackpackColorNameFromDamage(new ItemStack(ModItems.adventureBackpack), 0) : null;
    }

    public static void drawFace(Gui gui, Minecraft mc, int x, int y, boolean hover, boolean pressed)
    {
        drawFace(gui, mc, x, y, SIZE, hover, pressed);
    }

    public static void drawFace(Gui gui, Minecraft mc, int x, int y, int height, boolean hover, boolean pressed)
    {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(WIDGETS);
        int u = pressed ? FACE_PRESSED_U : hover ? FACE_HOVER_U : FACE_U;
        int upper = height / 2;
        int lower = height - upper;
        gui.drawTexturedModalRect(x, y, u, FACE_V, SIZE, upper);
        gui.drawTexturedModalRect(x, y + upper, u, FACE_V + SIZE - lower, SIZE, lower);
    }

    public String tipKey()
    {
        return mode == BED ? "adventurebackpack:gui.button.bed" : mode == EQUIP ? "adventurebackpack:gui.button.equip" : "adventurebackpack:gui.button.unequip";
    }

    public int mode()
    {
        return mode;
    }

    public boolean contains(int localX, int localY)
    {
        return localX >= x && localX <= x + SIZE && localY >= y && localY <= y + SIZE;
    }

    public void draw(Gui gui, Minecraft mc, RenderItem items, FontRenderer font, int left, int top, boolean hover, boolean pressed)
    {
        GL11.glColor4f(1, 1, 1, 1);
        if (mode == BED)
        {
            mc.renderEngine.bindTexture(CLASSIC);
            gui.drawTexturedModalRect(left + x, top + y, hover ? BED_HOVER_U : BED_U, BED_V, SIZE, SIZE);
            return;
        }
        int shift = pressed ? 1 : 0;
        drawFace(gui, mc, left + x, top + y, hover, pressed);
        gui.drawTexturedModalRect(left + x + ARROW_X + shift, top + y + ARROW_Y + shift, mode == EQUIP ? ARROW_UP_U : ARROW_DOWN_U, ARROW_V, ARROW_W, ARROW_H);
        if (icon == null) return;
        GL11.glPushMatrix();
        GL11.glTranslatef(left + x + ICON_X + shift, top + y + ICON_Y + shift, 0);
        GL11.glScalef(ICON_SCALE, ICON_SCALE, 1);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        items.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), icon, 0, 0);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glColor4f(1, 1, 1, 1);
    }
}
