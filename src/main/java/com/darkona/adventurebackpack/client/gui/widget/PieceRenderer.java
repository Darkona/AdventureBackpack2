package com.darkona.adventurebackpack.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public final class PieceRenderer
{
    private PieceRenderer()
    {
    }

    public static void drawPieces(Minecraft mc, int[] pieces, int left, int top, float z)
    {
        Tessellator t = Tessellator.instance;
        GL11.glColor4f(1, 1, 1, 1);
        for (int tex = 0; tex < WidgetSkins.textureCount(); tex++)
        {
            boolean bound = false;
            float px = 1f / WidgetSkins.textureSize(tex);
            for (int i = 0; i < pieces.length; i += BackgroundComposer.STRIDE)
            {
                if (pieces[i + BackgroundComposer.TEX] != tex) continue;
                if (!bound)
                {
                    mc.renderEngine.bindTexture(WidgetSkins.textureAt(tex));
                    t.startDrawingQuads();
                    bound = true;
                }
                int x = left + pieces[i], y = top + pieces[i + 1];
                int u = pieces[i + 2], v = pieces[i + 3], w = pieces[i + 4], h = pieces[i + 5];
                int argb = pieces[i + BackgroundComposer.ARGB];
                t.setColorRGBA_I(argb & 0xFFFFFF, argb >>> 24);
                t.addVertexWithUV(x, y + h, z, u * px, (v + h) * px);
                t.addVertexWithUV(x + w, y + h, z, (u + w) * px, (v + h) * px);
                t.addVertexWithUV(x + w, y, z, (u + w) * px, v * px);
                t.addVertexWithUV(x, y, z, u * px, v * px);
            }
            if (bound) t.draw();
        }
    }
}
