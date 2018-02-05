package com.darkona.adventurebackpack.client.gui;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import com.darkona.adventurebackpack.common.Constants.Source;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.network.EquipUnequipBackWearablePacket;

/**
 * Created on 06/01/2015
 *
 * @author Darkona
 */
public abstract class GuiWithTanks extends GuiContainer
{
    protected EntityPlayer player;
    protected Source source;

    GuiWithTanks(Container container)
    {
        super(container);
    }

    int getLeft()
    {
        return guiLeft;
    }

    int getTop()
    {
        return guiTop;
    }

    float getZLevel()
    {
        return zLevel;
    }

    protected abstract GuiImageButtonNormal getEquipButton();

    protected abstract GuiImageButtonNormal getUnequipButton();

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (source == Source.WEARING)
        {
            if (getUnequipButton().inButton(this, mouseX, mouseY))
            {
                ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.UNEQUIP_WEARABLE));
                player.closeScreen();
            }
        }
        else if (source == Source.HOLDING)
        {
            if (getEquipButton().inButton(this, mouseX, mouseY))
            {
                ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.EQUIP_WEARABLE));
                player.closeScreen();
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char key, int keycode)
    {
        if (keycode == Keybindings.openInventory.getKeyCode())
        {
            player.closeScreen();
        }

        super.keyTyped(key, keycode);
    }

    @Override
    public void handleMouseInput()
    {
        if (Mouse.getEventDWheel() != 0)
        {
            int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            int marginX = (width - xSize) / 2;
            int marginY = (height - ySize) / 2;

            if (i > marginX && i < marginX + xSize)
            {
                if (j > marginY && j < marginY + ySize)
                {
                    return; // forbid mouseWheel when mouse over our GUI,
                    // Shift+Wheel on stacks of fluid containers places them to clients bucket slots, causes desync
                }
            }
        }

        super.handleMouseInput();
    }
}
