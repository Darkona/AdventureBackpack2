package com.darkona.adventurebackpack.client.gui.widget.classic;

import static com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets.*;

import com.darkona.adventurebackpack.client.gui.widget.BackgroundComposer;
import com.darkona.adventurebackpack.client.gui.widget.FrameSkin;
import com.darkona.adventurebackpack.client.gui.widget.WidgetSkin;
import com.darkona.adventurebackpack.client.gui.widget.WidgetSkins;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.layout.gear.GearWidgets;
import com.darkona.adventurebackpack.util.Resources;

public final class ClassicSkins
{
    public static final int TEXTURE_SIZE = 256;
    private static final int CRAFT_U = 151, CRAFT_V = 60;
    private static final int RESULT_U = 225, RESULT_V = 96;
    private static final int TOOL_U = 43, TOOL_V = 78;
    private static final int PLAYER_U = 43, PLAYER_V = 124;
    private static final int BUCKETS_LEFT_U = 5, BUCKETS_RIGHT_U = 225, BUCKETS_V = 6, BUCKETS_H = 48;
    private static final int TANK_LEFT_U = 24, TANK_RIGHT_U = 206, TANK_TOP_V = 6, TANK_BODY_V = 7, TANK_BODY_H = 25;
    private static final int TANK_MARK_V = 32, TANK_BOTTOM_V = 107, TANK_FRAME_W = 18;
    private static final int PIPE_U = 205, PIPE_V = 108, PIPE_W = 20, PIPE_H = 6, PIPE_DX = -21, PIPE_DY = 11;
    private static final int SLOT = FrameSkin.SIZE;
    private static final int QUIVER_TINT = 0xFFFFE6B3;
    private static final int ROPE_TINT = 0xFFFCD596;

    private static boolean installed;

    private ClassicSkins()
    {
    }

    public static synchronized void install()
    {
        if (installed) return;
        installed = true;
        int classic = WidgetSkins.texture(Resources.guiTextures("guiBackpackNew"), TEXTURE_SIZE);
        int widgets = WidgetSkins.texture(Resources.guiTextures("guiBackpackWidgets"), TEXTURE_SIZE);
        WidgetSkins.window(new ClassicWindowSkin(classic));
        WidgetSkins.fallback(new FrameSkin(widgets, WidgetSkins.NEUTRAL_FRAME_U, WidgetSkins.NEUTRAL_FRAME_V, WidgetSkins.NEUTRAL_TINT, WidgetSkin.DEFAULT_LAYER, null));
        WidgetSkins.register(STORAGE, new FrameSkin(classic, PLAYER_U, PLAYER_V, BackgroundComposer.WHITE, 10, null));
        WidgetSkins.register(CRAFT, new BlockSkin(classic, CRAFT_U, CRAFT_V, 3 * SLOT, 3 * SLOT, 20));
        WidgetSkins.register(RESULT, new ResultSkin(classic));
        WidgetSkins.register(TOOLS, new BlockSkin(classic, TOOL_U, TOOL_V, SLOT, 2 * SLOT, 40));
        WidgetSkins.register(TANK, new TankSkin(classic));
        WidgetSkins.register(GearWidgets.FUEL_TANK, new TankSkin(classic));
        WidgetSkins.register(GearWidgets.AIR_TANK, new TankSkin(classic));
        WidgetSkins.register(GearWidgets.BAR, new WidgetSkin());
        WidgetSkins.register(GearWidgets.FUEL_SLOT, new FrameSkin(classic, PLAYER_U, PLAYER_V, BackgroundComposer.WHITE, 30, null));
        WidgetSkins.register(ROPE, new FrameSkin(widgets, WidgetSkins.NEUTRAL_FRAME_U, WidgetSkins.NEUTRAL_FRAME_V, ROPE_TINT, 60, null));
        WidgetSkins.register(QUIVER, new FrameSkin(widgets, WidgetSkins.NEUTRAL_FRAME_U, WidgetSkins.NEUTRAL_FRAME_V, QUIVER_TINT, 70, "adventurebackpack:gui.quiver.slot"));
        WidgetSkins.register(PLAYER, new FrameSkin(classic, PLAYER_U, PLAYER_V, BackgroundComposer.WHITE, 80, null));
        WidgetSkins.register(HOTBAR, new FrameSkin(classic, PLAYER_U, PLAYER_V, BackgroundComposer.WHITE, 90, null));
        WidgetSkins.register(BUTTON, new WidgetSkin());
        WidgetSkins.register(TOGGLE, new WidgetSkin());
        WidgetSkins.register(SORT, new WidgetSkin());
        WidgetSkins.register(LOCK, new WidgetSkin());
    }

    static final class BlockSkin extends WidgetSkin
    {
        private final int texture, u, v, w, h, layer;

        BlockSkin(int texture, int u, int v, int w, int h, int layer)
        {
            this.texture = texture;
            this.u = u;
            this.v = v;
            this.w = w;
            this.h = h;
            this.layer = layer;
        }

        @Override
        public int layer()
        {
            return layer;
        }

        @Override
        public void paint(BackgroundComposer c, WidgetLayout layout, int widget)
        {
            int s = layout.slotFirst(widget);
            c.use(texture, BackgroundComposer.WHITE).add(layout.slotX(s) - 1, layout.slotY(s) - 1, u, v, w, h);
        }
    }

    static final class ResultSkin extends WidgetSkin
    {
        private final int texture;

        ResultSkin(int texture)
        {
            this.texture = texture;
        }

        @Override
        public int layer()
        {
            return 30;
        }

        @Override
        public void paint(BackgroundComposer c, WidgetLayout layout, int widget)
        {
            int s = layout.slotFirst(widget);
            c.use(texture, BackgroundComposer.WHITE).add(layout.slotX(s) - 1, layout.slotY(s) - 1, RESULT_U, RESULT_V, SLOT, SLOT);
        }

        @Override
        public void paintOver(BackgroundComposer c, WidgetLayout layout, int widget)
        {
            int s = layout.slotFirst(widget);
            c.use(texture, BackgroundComposer.WHITE).add(layout.slotX(s) + PIPE_DX, layout.slotY(s) + PIPE_DY, PIPE_U, PIPE_V, PIPE_W, PIPE_H);
        }
    }

    static final class TankSkin extends WidgetSkin
    {
        private final int texture;

        TankSkin(int texture)
        {
            this.texture = texture;
        }

        @Override
        public int layer()
        {
            return 50;
        }

        @Override
        public void paint(BackgroundComposer c, WidgetLayout layout, int widget)
        {
            if (layout.slots(widget) == 0) return;
            int s = layout.slotFirst(widget);
            int u = layout.param(widget) == 0 ? BUCKETS_LEFT_U : BUCKETS_RIGHT_U;
            c.use(texture, BackgroundComposer.WHITE).add(layout.slotX(s) - 1, layout.slotY(s) - 1, u, BUCKETS_V, SLOT, BUCKETS_H);
        }

        @Override
        public void paintOver(BackgroundComposer c, WidgetLayout layout, int widget)
        {
            int u = layout.param(widget) == 0 ? TANK_LEFT_U : TANK_RIGHT_U;
            int h = layout.h(widget);
            int x = layout.x(widget) - 1;
            int y = layout.y(widget) - 1;
            c.use(texture, BackgroundComposer.WHITE);
            c.add(x, y, u, TANK_TOP_V, TANK_FRAME_W, 1);
            c.repeat(x, y + 1, TANK_FRAME_W, h, u, TANK_BODY_V, TANK_FRAME_W, TANK_BODY_H);
            for (int q = 1; q <= 3; q++)
            {
                c.add(x, layout.y(widget) + h * q / 4, u, TANK_MARK_V, TANK_FRAME_W, 1);
            }
            c.add(x, layout.y(widget) + h, u, TANK_BOTTOM_V, TANK_FRAME_W, 1);
        }
    }
}
