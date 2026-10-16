package com.darkona.adventurebackpack.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerFlamethrowerPack;
import com.darkona.adventurebackpack.inventory.InventoryFlamethrowerPack;
import com.darkona.adventurebackpack.item.ItemFlamethrowerPack;
import com.darkona.adventurebackpack.network.EquipUnequipBackWearablePacket;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import com.darkona.adventurebackpack.block.TileGear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiFlamethrowerPack extends GuiWithTanks
{
    private TileGear tile;
    private static final ResourceLocation texture = Resources.guiTextures("guiFlamethrowerPack");
    private static final GuiTank leftTank = new GuiTank(8, 8, 72, 16, ConfigHandler.GUI_TANK_RENDER);
    private static final GuiTank rightTank = new GuiTank(116, 8, 72, 16, ConfigHandler.GUI_TANK_RENDER);
    private static final GuiImageButtonNormal equipButton = new GuiImageButtonNormal(150, 64, 18, 18);
    private static final GuiImageButtonNormal unequipButton = new GuiImageButtonNormal(150, 64, 18, 18);
    private static final GuiToggleButton pilotButton = new GuiToggleButton(150, 44, 18, 12, "adventurebackpack:gui.toggle.pilot");

    private InventoryFlamethrowerPack inventory;
    private boolean wearing;
    private EntityPlayer player;
    private ItemStack shown;
    private int shownFuel = -1;
    private int shownOn = -1;

    public GuiFlamethrowerPack(EntityPlayer player, InventoryFlamethrowerPack inventory, TileGear tile)
    {
        super(new ContainerFlamethrowerPack(player, inventory, tile));
        this.inventory = inventory;
        this.player = player;
        this.tile = tile;
        this.wearing = false;
        this.shown = inventory.getParentItemStack();
        xSize = 176;
        ySize = 166;
    }

    public GuiFlamethrowerPack(EntityPlayer player, InventoryFlamethrowerPack inventory, boolean wearing)
    {
        super(new ContainerFlamethrowerPack(player, inventory, wearing));
        this.inventory = inventory;
        this.wearing = wearing;
        this.player = player;
        this.shown = inventory.getParentItemStack();
        xSize = 176;
        ySize = 166;
        if (wearing) setToggle(pilotButton);
    }

    @Override
    protected boolean isToggleOn()
    {
        return shownOn == 1;
    }

    private void refresh()
    {
        ItemStack current = tile != null ? tile.getGear() : wearing ? Wearing.getWearingWearable(player) : player.getCurrentEquippedItem();
        if (current != null && current != shown && current.getItem() instanceof ItemFlamethrowerPack)
        {
            shown = current;
            inventory.setContainerStack(current);
        }
        int fuel = inventory.getFuel();
        int on = inventory.isOn() ? 1 : 0;
        if (fuel != shownFuel || on != shownOn)
        {
            shownFuel = fuel;
            shownOn = on;
            setLabels(I18n.format("adventurebackpack:gui.flamethrower.fuel"), fuel + "/" + inventory.getCapacity(), "",
                    I18n.format(on == 1 ? "adventurebackpack:gui.flamethrower.lit" : "adventurebackpack:gui.flamethrower.out"));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1, 1, 1, 1);
        this.mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        if (tile != null) return;
        GuiImageButtonNormal button = wearing ? unequipButton : equipButton;
        int v = wearing ? 186 : 167;
        button.draw(this, button.inButton(this, mouseX, mouseY) ? 20 : 1, v);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        refresh();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        leftTank.draw(this, inventory.getLeftTank());
        rightTank.draw(this, inventory.getRightTank());
        drawLabels(10, 0x373737);
        drawToggle(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (clickToggle(mouseX, mouseY)) return;
        if (wearing && unequipButton.inButton(this, mouseX, mouseY))
        {
            ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.UNEQUIP_WEARABLE, false));
            player.closeScreen();
            return;
        }
        if (!wearing && tile == null && equipButton.inButton(this, mouseX, mouseY))
        {
            ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.EQUIP_WEARABLE, GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)));
            player.closeScreen();
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
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
