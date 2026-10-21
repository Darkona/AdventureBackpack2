package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.Wearing;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.network.EquipUnequipBackWearablePacket;
import com.darkona.adventurebackpack.network.SleepingBagPacket;
import com.darkona.adventurebackpack.network.StoragePacket;
import com.darkona.adventurebackpack.common.RenderInfoCache;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.darkona.adventurebackpack.client.gui.widget.ActionButton;
import com.darkona.adventurebackpack.client.gui.widget.BackgroundComposer;
import com.darkona.adventurebackpack.client.gui.widget.BackpackButton;
import com.darkona.adventurebackpack.client.gui.widget.PieceRenderer;
import com.darkona.adventurebackpack.client.gui.widget.WidgetSkins;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.widget.WidgetInventory;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import com.darkona.adventurebackpack.inventory.widget.WidgetTypes;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL12;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiAdvBackpack extends GuiWithTanks
{
    private static final int GHOST_VEIL = 0xA08B8B8B;
    private static final int SLOT_SIZE = 16;
    private static final int LOCK_TINT = 0x50E8B820;
    private static final float CLICK_PITCH = 1.0F;
    private static final int SLOT_REACH = 1;

    protected IInventoryAdventureBackpack inventory;
    protected boolean isTile;
    protected boolean wearing;
    protected int X;
    protected int Y;
    protected int Z;
    private EntityPlayer player;
    private final WidgetLayout layout;
    private final int[] background;
    private final BackpackButton backpackButton;
    private final GuiToggleButton abilitiesButton;
    private final ActionButton sortButton;
    private final ActionButton lockButton;
    private boolean lockMode;
    private List<String>[] lockTips;
    private List<String> sortTip;
    private List<String> backpackTip;
    private BackpackProperty props;
    private final GuiTank[] tanks;
    private final int[] tankIndex;
    private List<String>[] tips;
    private ItemStack shownStack;
    private int shownVersion;
    public int lefties;
    public int topsies;

    public GuiAdvBackpack(EntityPlayer player, TileAdventureBackpack tileBackpack)
    {
        super(new ContainerBackpack(player, tileBackpack, ContainerBackpack.SOURCE_TILE));
        this.inventory = tileBackpack;
        this.isTile = true;
        layout = ((ContainerBackpack) inventorySlots).widgets;
        background = BackgroundComposer.compose(layout);
        xSize = layout.width();
        ySize = layout.height();
        backpackButton = button(BackpackButton.BED);
        abilitiesButton = toggle();
        sortButton = action(WidgetType.Control.SORT_BUTTON, ActionButton.SORT);
        lockButton = action(WidgetType.Control.LOCK_BUTTON, ActionButton.LOCK);
        tankIndex = tankIndexes(layout);
        tanks = tankViews(layout, tankIndex);
        this.X = tileBackpack.xCoord;
        this.Y = tileBackpack.yCoord;
        this.Z = tileBackpack.zCoord;
        this.player = player;
        this.lefties = guiLeft;
        this.topsies = guiTop;
    }

    public GuiAdvBackpack(EntityPlayer player, InventoryBackpack inventoryBackpack, boolean wearing)
    {
        super(new ContainerBackpack(player, inventoryBackpack, wearing ? ContainerBackpack.SOURCE_WEARING : ContainerBackpack.SOURCE_HOLDING));
        this.inventory = inventoryBackpack;
        this.wearing = wearing;
        this.isTile = false;
        layout = ((ContainerBackpack) inventorySlots).widgets;
        background = BackgroundComposer.compose(layout);
        xSize = layout.width();
        ySize = layout.height();
        backpackButton = button(wearing ? BackpackButton.UNEQUIP : BackpackButton.EQUIP);
        abilitiesButton = toggle();
        sortButton = action(WidgetType.Control.SORT_BUTTON, ActionButton.SORT);
        lockButton = action(WidgetType.Control.LOCK_BUTTON, ActionButton.LOCK);
        tankIndex = tankIndexes(layout);
        tanks = tankViews(layout, tankIndex);
        this.player = player;
        ItemStack worn = wearing ? Wearing.getWearingBackpack(player) : null;
        if (worn != null && ConfigHandler.BACKPACK_ABILITIES && BackpackAbilities.hasAbility(BackpackNames.getBackpackColorName(worn)))
        {
            props = BackpackProperty.get(player);
            if (props != null && abilitiesButton != null) setToggle(abilitiesButton);
        }
        this.lefties = guiLeft;
        this.topsies = guiTop;
    }

    private BackpackButton button(int mode)
    {
        int w = WidgetInventory.control(layout, WidgetType.Control.BACKPACK_BUTTON);
        return w == WidgetLayout.NONE ? null : new BackpackButton(layout.x(w), layout.y(w), mode);
    }

    private ActionButton action(WidgetType.Control control, int mode)
    {
        int w = WidgetInventory.control(layout, control);
        return w == WidgetLayout.NONE ? null : new ActionButton(layout.x(w), layout.y(w), mode);
    }

    private GuiToggleButton toggle()
    {
        int w = WidgetInventory.control(layout, WidgetType.Control.ABILITY_TOGGLE);
        return w == WidgetLayout.NONE ? null : new GuiToggleButton(layout.x(w), layout.y(w), "adventurebackpack:gui.toggle.abilities");
    }

    private static int[] tankIndexes(WidgetLayout layout)
    {
        int n = 0;
        for (int w = 0; w < layout.count(); w++)
        {
            if (WidgetTypes.get(layout.kind(w)).tank(layout, w) >= 0) n++;
        }
        int[] widgets = new int[n];
        n = 0;
        for (int w = 0; w < layout.count(); w++)
        {
            if (WidgetTypes.get(layout.kind(w)).tank(layout, w) >= 0) widgets[n++] = w;
        }
        return widgets;
    }

    private static GuiTank[] tankViews(WidgetLayout layout, int[] widgets)
    {
        GuiTank[] views = new GuiTank[widgets.length];
        for (int i = 0; i < widgets.length; i++)
        {
            int w = widgets[i];
            views[i] = new GuiTank(layout.x(w), layout.y(w), layout.h(w), layout.w(w), ConfigHandler.GUI_TANK_RENDER).withGauge();
        }
        return views;
    }

    private FluidTank fluidOf(int view)
    {
        int w = tankIndex[view];
        return WidgetTypes.get(layout.kind(w)).tank(layout, w) == 0 ? inventory.getLeftTank() : inventory.getRightTank();
    }

    @Override
    public void onGuiClosed()
    {
        if (inventory != null)
        {
            inventory.closeInventory();
        }
        super.onGuiClosed();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        drawPieces(background);
        if (backpackButton != null)
        {
            boolean hover = backpackButton.contains(mouseX - guiLeft, mouseY - guiTop);
            backpackButton.draw(this, mc, itemRender, fontRendererObj, guiLeft, guiTop, hover, hover && Mouse.isButtonDown(0));
        }
        if (sortButton != null)
        {
            boolean hover = sortButton.contains(mouseX - guiLeft, mouseY - guiTop);
            sortButton.draw(this, mc, guiLeft, guiTop, hover, hover && Mouse.isButtonDown(0), false);
        }
        if (lockButton != null)
        {
            boolean hover = lockButton.contains(mouseX - guiLeft, mouseY - guiTop);
            lockButton.draw(this, mc, guiLeft, guiTop, hover, hover && Mouse.isButtonDown(0), lockMode);
        }
        if (ConfigHandler.HOVERING_TEXT_TANKS)
        {
            for (int i = 0; i < tanks.length; i++)
            {
                if (tanks[i].inTank(this, mouseX, mouseY))
                {
                    drawHoveringText(tanks[i].getTankTooltip(), mouseX, mouseY, fontRendererObj);
                }
            }
        }
    }

    private void refreshShown()
    {
        if (isTile || !(inventory instanceof InventoryBackpack)) return;
        InventoryBackpack inv = (InventoryBackpack) inventory;
        ItemStack source = wearing ? Wearing.getWearingBackpack(player) : inv.getContainerStack();
        if (source == null) return;
        int version = RenderInfoCache.get(source).version();
        if (source == shownStack && version == shownVersion) return;
        if (source != inv.getContainerStack()) inv.setContainerStack(source);
        inv.openInventory();
        shownStack = source;
        shownVersion = version;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        refreshShown();
        for (int i = 0; i < tanks.length; i++) tanks[i].draw(this, fluidOf(i));
        drawGhosts();
        drawLocks();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        drawToggle(mouseX, mouseY);
    }

    private void drawLocks()
    {
        ContainerBackpack container = (ContainerBackpack) inventorySlots;
        if (container.locks().isEmpty()) return;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        for (int w = 0; w < layout.count(); w++)
        {
            if (!WidgetTypes.get(layout.kind(w)).sortable()) continue;
            for (int s = layout.slotFirst(w); s < layout.slotFirst(w) + layout.slots(w); s++)
            {
                if (container.isLocked(s)) Gui.drawRect(layout.slotX(s), layout.slotY(s), layout.slotX(s) + SLOT_SIZE, layout.slotY(s) + SLOT_SIZE, LOCK_TINT);
            }
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for (int w = 0; w < layout.count(); w++)
        {
            if (!WidgetTypes.get(layout.kind(w)).sortable()) continue;
            for (int s = layout.slotFirst(w); s < layout.slotFirst(w) + layout.slots(w); s++)
            {
                if (container.isLocked(s)) ActionButton.drawBadge(this, mc, layout.slotX(s) + SLOT_SIZE - ActionButton.BADGE_W, layout.slotY(s));
            }
        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private int sortableSlotAt(int x, int y)
    {
        for (int w = 0; w < layout.count(); w++)
        {
            if (!WidgetTypes.get(layout.kind(w)).sortable()) continue;
            for (int s = layout.slotFirst(w); s < layout.slotFirst(w) + layout.slots(w); s++)
            {
                int sx = layout.slotX(s), sy = layout.slotY(s);
                if (x >= sx - SLOT_REACH && x < sx + SLOT_SIZE + SLOT_REACH && y >= sy - SLOT_REACH && y < sy + SLOT_SIZE + SLOT_REACH) return s;
            }
        }
        return -1;
    }

    private void click()
    {
        mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), CLICK_PITCH));
    }

    private void drawGhosts()
    {
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        boolean any = false;
        for (int w = 0; w < layout.count(); w++)
        {
            ItemStack ghost = layout.ownsInventory(w) ? WidgetTypes.get(layout.kind(w)).ghost() : null;
            if (ghost == null) continue;
            for (int s = layout.slotFirst(w); s < layout.slotFirst(w) + layout.slots(w); s++)
            {
                if (inventorySlots.getSlot(s).getHasStack()) continue;
                itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), ghost, layout.slotX(s), layout.slotY(s));
                any = true;
            }
        }
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        if (!any) return;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        for (int w = 0; w < layout.count(); w++)
        {
            if (!layout.ownsInventory(w) || WidgetTypes.get(layout.kind(w)).ghost() == null) continue;
            for (int s = layout.slotFirst(w); s < layout.slotFirst(w) + layout.slots(w); s++)
            {
                if (inventorySlots.getSlot(s).getHasStack()) continue;
                Gui.drawRect(layout.slotX(s), layout.slotY(s), layout.slotX(s) + SLOT_SIZE, layout.slotY(s) + SLOT_SIZE, GHOST_VEIL);
            }
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (backpackButton != null && backpackTip != null && backpackButton.contains(mouseX - guiLeft, mouseY - guiTop))
        {
            drawHoveringText(backpackTip, mouseX, mouseY, fontRendererObj);
            return;
        }
        if (sortButton != null && sortTip != null && sortButton.contains(mouseX - guiLeft, mouseY - guiTop))
        {
            drawHoveringText(sortTip, mouseX, mouseY, fontRendererObj);
            return;
        }
        if (lockButton != null && lockTips != null && lockButton.contains(mouseX - guiLeft, mouseY - guiTop))
        {
            drawHoveringText(lockTips[lockMode ? 1 : 0], mouseX, mouseY, fontRendererObj);
            return;
        }
        if (mc.thePlayer.inventory.getItemStack() != null || tips == null) return;
        int x = mouseX - guiLeft, y = mouseY - guiTop;
        for (int s = 0; s < layout.slotCount(); s++)
        {
            int sx = layout.slotX(s), sy = layout.slotY(s);
            if (x < sx - 1 || x >= sx + 17 || y < sy - 1 || y >= sy + 17) continue;
            List<String> tip = tips[layout.slotWidget(s)];
            if (tip != null && !inventorySlots.getSlot(s).getHasStack()) drawHoveringText(tip, mouseX, mouseY, fontRendererObj);
            return;
        }
    }

    @Override
    protected boolean isToggleOn()
    {
        return props != null && !props.isAbilitiesOff();
    }

    @Override
    public float getZLevel()
    {
        return this.zLevel;
    }

    private void drawPieces(int[] pieces)
    {
        PieceRenderer.drawPieces(mc, pieces, guiLeft, guiTop, zLevel);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui()
    {
        super.initGui();
        tips = new List[layout.count()];
        for (int w = 0; w < layout.count(); w++)
        {
            String key = layout.ownsInventory(w) ? WidgetSkins.get(layout.kind(w)).emptyTip() : null;
            if (key != null) tips[w] = Collections.singletonList(StatCollector.translateToLocal(key));
        }
        backpackTip = backpackButton == null ? null : Collections.singletonList(StatCollector.translateToLocal(backpackButton.tipKey()));
        sortTip = Collections.singletonList(StatCollector.translateToLocal("adventurebackpack:gui.sort"));
        lockTips = new List[]{
                Collections.singletonList(StatCollector.translateToLocal("adventurebackpack:gui.lock.off")),
                Collections.singletonList(StatCollector.translateToLocal("adventurebackpack:gui.lock.on"))};
        if (guiLeft < 0) guiLeft = 0;
        if (guiTop < 0) guiTop = 0;
    }

    @Override
    public int getLeft()
    {
        return guiLeft;
    }

    @Override
    public int getTop()
    {
        return guiTop;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button)
    {
        if (clickToggle(mouseX, mouseY)) return;
        if (sortButton != null && sortButton.contains(mouseX - guiLeft, mouseY - guiTop))
        {
            ModNetwork.net.sendToServer(new StoragePacket.Message(inventorySlots.windowId, ContainerBackpack.ACTION_SORT, 0));
            click();
            return;
        }
        if (lockButton != null && lockButton.contains(mouseX - guiLeft, mouseY - guiTop))
        {
            lockMode = !lockMode;
            click();
            return;
        }
        if (lockMode && button < 2)
        {
            int slot = sortableSlotAt(mouseX - guiLeft, mouseY - guiTop);
            if (slot >= 0)
            {
                ModNetwork.net.sendToServer(new StoragePacket.Message(inventorySlots.windowId, ContainerBackpack.ACTION_LOCK, slot));
                return;
            }
        }
        if (backpackButton != null && backpackButton.contains(mouseX - guiLeft, mouseY - guiTop))
        {
            if (backpackButton.mode() == BackpackButton.BED)
            {
                TileAdventureBackpack te = (TileAdventureBackpack) inventory;
                ModNetwork.net.sendToServer(new SleepingBagPacket.SleepingBagMessage(te.xCoord, te.yCoord, te.zCoord));
                return;
            }
            if (backpackButton.mode() == BackpackButton.UNEQUIP)
            {
                ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.UNEQUIP_WEARABLE, false));
            } else
            {
                ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.EQUIP_WEARABLE, GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)));
            }
            player.closeScreen();
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void keyTyped(char key, int keycode)
    {
        if (keycode == Keybindings.gear.getKeyCode())
        {
            player.closeScreen();
        }
        super.keyTyped(key, keycode);
    }
}
