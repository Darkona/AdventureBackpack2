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
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.common.RenderInfoCache;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.darkona.adventurebackpack.inventory.BackpackLayout;
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
    protected IInventoryAdventureBackpack inventory;
    protected boolean isTile;
    protected boolean wearing;
    protected int X;
    protected int Y;
    protected int Z;
    private EntityPlayer player;
    private static final ResourceLocation texture = Resources.guiTextures("guiBackpackNew");
    private final BackpackLayout layout;
    private final int[] background;
    private final GuiImageButtonNormal bedButton;
    private final GuiImageButtonNormal equipButton;
    private final GuiImageButtonNormal unequipButton;
    private final GuiToggleButton abilitiesButton;
    private BackpackProperty props;
    private final GuiTank tankLeft;
    private final GuiTank tankRight;
    private List<String> quiverTip;
    private FluidTank lft;
    private FluidTank rgt;
    private ItemStack shownStack;
    private int shownVersion;
    public int lefties;
    public int topsies;

    public GuiAdvBackpack(EntityPlayer player, TileAdventureBackpack tileBackpack)
    {
        super(new ContainerBackpack(player, tileBackpack, ContainerBackpack.SOURCE_TILE));
        this.inventory = tileBackpack;
        this.isTile = true;
        layout = ((ContainerBackpack) inventorySlots).layout;
        background = BackpackBackground.compose(layout);
        xSize = layout.width();
        ySize = layout.height();
        bedButton = equipButton = unequipButton = new GuiImageButtonNormal(layout.buttonX(), layout.buttonY(), 18, 18);
        abilitiesButton = new GuiToggleButton(layout.toggleX(), layout.toggleY(), 18, 12, "adventurebackpack:gui.toggle.abilities");
        tankLeft = new GuiTank(layout.tankX(0), layout.tankY(), layout.tankHeight(), layout.tankWidth(), ConfigHandler.GUI_TANK_RENDER);
        tankRight = layout.tanks() > 1 ? new GuiTank(layout.tankX(1), layout.tankY(), layout.tankHeight(), layout.tankWidth(), ConfigHandler.GUI_TANK_RENDER) : null;
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
        layout = ((ContainerBackpack) inventorySlots).layout;
        background = BackpackBackground.compose(layout);
        xSize = layout.width();
        ySize = layout.height();
        bedButton = equipButton = unequipButton = new GuiImageButtonNormal(layout.buttonX(), layout.buttonY(), 18, 18);
        abilitiesButton = new GuiToggleButton(layout.toggleX(), layout.toggleY(), 18, 12, "adventurebackpack:gui.toggle.abilities");
        tankLeft = new GuiTank(layout.tankX(0), layout.tankY(), layout.tankHeight(), layout.tankWidth(), ConfigHandler.GUI_TANK_RENDER);
        tankRight = layout.tanks() > 1 ? new GuiTank(layout.tankX(1), layout.tankY(), layout.tankHeight(), layout.tankWidth(), ConfigHandler.GUI_TANK_RENDER) : null;
        this.player = player;
        ItemStack worn = wearing ? Wearing.getWearingBackpack(player) : null;
        if (worn != null && ConfigHandler.BACKPACK_ABILITIES && BackpackAbilities.hasAbility(BackpackNames.getBackpackColorName(worn)))
        {
            props = BackpackProperty.get(player);
            if (props != null) setToggle(abilitiesButton);
        }
        this.lefties = guiLeft;
        this.topsies = guiTop;
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
        GL11.glColor4f(1, 1, 1, 1);

        this.mc.renderEngine.bindTexture(texture);

        drawPieces(background);

        if (isTile)
        {
            if (bedButton.inButton(this, mouseX, mouseY))
            {
                bedButton.draw(this, 20, 227);
            } else
            {
                bedButton.draw(this, 1, 227);
            }
        }else
        {
            if(wearing)
            {
                if(unequipButton.inButton(this,mouseX,mouseY))
                {
                    unequipButton.draw(this,96,227);
                }else
                {
                    unequipButton.draw(this,77,227);
                }
            }else
            {
                if(equipButton.inButton(this,mouseX,mouseY))
                {
                    equipButton.draw(this,96,208);
                }else
                {
                    equipButton.draw(this,77,208);
                }
            }
        }
        if(ConfigHandler.HOVERING_TEXT_TANKS)
        {
            if (tankLeft.inTank(this, mouseX, mouseY))
            {
                drawHoveringText(tankLeft.getTankTooltip(), mouseX, mouseY, fontRendererObj);
            }

            if (tankRight != null && tankRight.inTank(this, mouseX, mouseY))
            {
                drawHoveringText(tankRight.getTankTooltip(), mouseX, mouseY, fontRendererObj);
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
        lft = inventory.getLeftTank();
        rgt = inventory.getRightTank();
        tankLeft.draw(this, lft);
        if (tankRight != null) tankRight.draw(this, rgt);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        drawToggle(mouseX, mouseY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (layout.quiver() == 0 || mc.thePlayer.inventory.getItemStack() != null) return;
        int x = mouseX - guiLeft, y = mouseY - guiTop;
        for (int q = 0; q < layout.quiver(); q++)
        {
            int qx = layout.quiverX(q), qy = layout.quiverY(q);
            if (x >= qx - 1 && x < qx + 17 && y >= qy - 1 && y < qy + 17)
            {
                Slot slot = inventorySlots.getSlot(layout.quiverStart() + q);
                if (!slot.getHasStack()) drawHoveringText(quiverTip, mouseX, mouseY, fontRendererObj);
                return;
            }
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
        float px = 1f / 256f;
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        for (int i = 0; i < pieces.length; i += BackpackBackground.STRIDE)
        {
            int x = guiLeft + pieces[i], y = guiTop + pieces[i + 1];
            int u = pieces[i + 2], v = pieces[i + 3], w = pieces[i + 4], h = pieces[i + 5];
            t.addVertexWithUV(x, y + h, zLevel, u * px, (v + h) * px);
            t.addVertexWithUV(x + w, y + h, zLevel, (u + w) * px, (v + h) * px);
            t.addVertexWithUV(x + w, y, zLevel, (u + w) * px, v * px);
            t.addVertexWithUV(x, y, zLevel, u * px, v * px);
        }
        t.draw();
    }

    @Override
    public void initGui()
    {
        super.initGui();
        quiverTip = Collections.singletonList(StatCollector.translateToLocal("adventurebackpack:gui.quiver.slot"));
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
        if(isTile)
        {
            if (bedButton.inButton(this, mouseX, mouseY))
            {
                TileAdventureBackpack te = (TileAdventureBackpack) inventory;
                ModNetwork.net.sendToServer(new SleepingBagPacket.SleepingBagMessage(te.xCoord, te.yCoord, te.zCoord));
                return;
            }
        }else
        {
            if(wearing)
            {
                if(unequipButton.inButton(this,mouseX,mouseY))
                {
                    ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.UNEQUIP_WEARABLE,false));
                    player.closeScreen();
                    return;
                }
            } else
            {
                if(equipButton.inButton(this,mouseX,mouseY))
                {
                    ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.EQUIP_WEARABLE, GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)));
                    player.closeScreen();
                    return;
                }
            }
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
