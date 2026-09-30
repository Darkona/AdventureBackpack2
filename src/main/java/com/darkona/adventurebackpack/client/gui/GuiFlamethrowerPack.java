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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * GUI of the Flamethrower Pack: its two fuel tanks, the fuel container slots and the equip or unequip button. It reads the
 * stack's tag only when the stack it shows is replaced by a synced one, never on every frame.
 */
@SideOnly(Side.CLIENT)
public class GuiFlamethrowerPack extends GuiWithTanks
{
    private static final ResourceLocation texture = Resources.guiTextures("guiFlamethrowerPack");
    private static final GuiTank leftTank = new GuiTank(8, 8, 72, 16, ConfigHandler.GUI_TANK_RENDER);
    private static final GuiTank rightTank = new GuiTank(116, 8, 72, 16, ConfigHandler.GUI_TANK_RENDER);
    private static final GuiImageButtonNormal equipButton = new GuiImageButtonNormal(150, 64, 18, 18);
    private static final GuiImageButtonNormal unequipButton = new GuiImageButtonNormal(150, 64, 18, 18);

    private final InventoryFlamethrowerPack inventory;
    private final boolean wearing;
    private final EntityPlayer player;
    private ItemStack shown;
    private int shownFuel = -1;
    private String fuelLabel = "";
    private int shownOn = -1;
    private String pilotLabel = "";

    public GuiFlamethrowerPack(EntityPlayer player, InventoryFlamethrowerPack inventory, boolean wearing)
    {
        super(new ContainerFlamethrowerPack(player, inventory, wearing));
        this.inventory = inventory;
        this.wearing = wearing;
        this.player = player;
        this.shown = inventory.getParentItemStack();
        xSize = 176;
        ySize = 166;
    }

    /** Follows the synced stack: a new object means new numbers. */
    private void refresh()
    {
        ItemStack current = wearing ? Wearing.getWearingWearable(player) : player.getCurrentEquippedItem();
        if (current != null && current != shown && current.getItem() instanceof ItemFlamethrowerPack)
        {
            shown = current;
            inventory.setContainerStack(current);
        }
        int fuel = inventory.getFuel();
        if (fuel != shownFuel)
        {
            shownFuel = fuel;
            fuelLabel = I18n.format("adventurebackpack:gui.flamethrower.fuel", fuel, inventory.getCapacity());
        }
        int on = inventory.isOn() ? 1 : 0;
        if (on != shownOn)
        {
            shownOn = on;
            pilotLabel = on == 1 ? I18n.format("adventurebackpack:gui.flamethrower.lit") : I18n.format("adventurebackpack:gui.flamethrower.out");
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1, 1, 1, 1);
        this.mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
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
        fontRendererObj.drawString(fuelLabel, 52, 12, 0x373737, false);
        fontRendererObj.drawString(pilotLabel, 52, 24, 0x373737, false);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (wearing && unequipButton.inButton(this, mouseX, mouseY))
        {
            ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.UNEQUIP_WEARABLE, false));
            player.closeScreen();
            return;
        }
        if (!wearing && equipButton.inButton(this, mouseX, mouseY))
        {
            ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.EQUIP_WEARABLE, GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)));
            player.closeScreen();
            return;
        }
        // A click on our button is not also a click on the container (no slot, no drop of the held cursor stack)
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char key, int keycode)
    {
        if (keycode == Keybindings.openBackpack.getKeyCode())
        {
            player.closeScreen();
        }
        super.keyTyped(key, keycode);
    }
}
