package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.block.TileGear;
import com.darkona.adventurebackpack.client.gui.widget.BackgroundComposer;
import com.darkona.adventurebackpack.client.gui.widget.BackpackButton;
import com.darkona.adventurebackpack.client.gui.widget.GaugeBar;
import com.darkona.adventurebackpack.client.gui.widget.PieceRenderer;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets;
import com.darkona.adventurebackpack.common.layout.gear.GearWidgets;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerGear;
import com.darkona.adventurebackpack.inventory.widget.WidgetTypes;
import com.darkona.adventurebackpack.network.EquipUnequipBackWearablePacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class GuiGear extends GuiWithTanks
{
    private static final int LABEL_Y = 10;
    private static final int LABEL_COLOR = 0x373737;
    private static final int TEMPERATURE_COLOR = 0xFFE0541E;
    private static final int BURN_COLOR = 0xFFFFA000;

    protected final WidgetLayout layout;
    protected final EntityPlayer player;
    protected final TileGear tile;
    protected final boolean wearing;
    private final int[] background;
    private final BackpackButton button;
    private final GuiTank[] tanks;
    private final int[] tankParam;
    private List<String> buttonTip;

    protected GuiGear(ContainerGear container, EntityPlayer player, TileGear tile, boolean wearing, String toggleKey)
    {
        super(container);
        this.layout = container.widgets;
        this.player = player;
        this.tile = tile;
        this.wearing = wearing;
        xSize = layout.width();
        ySize = layout.height();
        background = BackgroundComposer.compose(layout);
        int b = layout.first(ClassicWidgets.BUTTON);
        button = b == WidgetLayout.NONE || tile != null ? null
                : new BackpackButton(layout.x(b), layout.y(b), wearing ? BackpackButton.UNEQUIP : BackpackButton.EQUIP);
        int t = layout.first(ClassicWidgets.TOGGLE);
        if (t != WidgetLayout.NONE && wearing && toggleKey != null)
        {
            setToggle(new GuiToggleButton(layout.x(t), layout.y(t), toggleKey));
        }
        int n = 0;
        for (int w = 0; w < layout.count(); w++)
        {
            if (WidgetTypes.get(layout.kind(w)).tank(layout, w) >= 0) n++;
        }
        tanks = new GuiTank[n];
        tankParam = new int[n];
        n = 0;
        for (int w = 0; w < layout.count(); w++)
        {
            if (WidgetTypes.get(layout.kind(w)).tank(layout, w) < 0) continue;
            tanks[n] = new GuiTank(layout.x(w), layout.y(w), layout.h(w), layout.w(w), ConfigHandler.GUI_TANK_RENDER).withGauge();
            tankParam[n++] = layout.param(w);
        }
    }

    protected abstract FluidTank fluid(int tank);

    protected void update()
    {
    }

    protected float barFill(int bar)
    {
        return 0;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        buttonTip = button == null ? null : Collections.singletonList(StatCollector.translateToLocal(button.tipKey()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        PieceRenderer.drawPieces(mc, background, guiLeft, guiTop, zLevel);
        if (button != null)
        {
            boolean hover = button.contains(mouseX - guiLeft, mouseY - guiTop);
            button.draw(this, mc, itemRender, fontRendererObj, guiLeft, guiTop, hover, hover && Mouse.isButtonDown(0));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        update();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        for (int i = 0; i < tanks.length; i++) tanks[i].draw(this, fluid(tankParam[i]));
        drawBars();
        drawLabels(LABEL_Y, LABEL_COLOR);
        drawToggle(mouseX, mouseY);
    }

    private void drawBars()
    {
        for (int w = 0; w < layout.count(); w++)
        {
            if (layout.kind(w) != GearWidgets.BAR) continue;
            int id = layout.param(w);
            int color = id == GearWidgets.BAR_TEMPERATURE ? TEMPERATURE_COLOR : id == GearWidgets.BAR_BURN ? BURN_COLOR : TEMPERATURE_COLOR;
            GaugeBar.drawBar(layout.x(w), layout.y(w), layout.w(w), layout.h(w), barFill(id), color);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (button != null && buttonTip != null && button.contains(mouseX - guiLeft, mouseY - guiTop))
        {
            drawHoveringText(buttonTip, mouseX, mouseY, fontRendererObj);
            return;
        }
        if (!ConfigHandler.HOVERING_TEXT_TANKS) return;
        for (int i = 0; i < tanks.length; i++)
        {
            if (tanks[i].inTank(this, mouseX, mouseY)) drawHoveringText(tanks[i].getTankTooltip(), mouseX, mouseY, fontRendererObj);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (clickToggle(mouseX, mouseY)) return;
        if (button != null && button.contains(mouseX - guiLeft, mouseY - guiTop))
        {
            if (wearing)
            {
                ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.UNEQUIP_WEARABLE, false));
            } else
            {
                ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.EQUIP_WEARABLE, GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)));
            }
            player.closeScreen();
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char key, int keycode)
    {
        if (keycode == Keybindings.gear.getKeyCode()) player.closeScreen();
        super.keyTyped(key, keycode);
    }

    @Override
    public float getZLevel()
    {
        return zLevel;
    }
}
