package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerCopter;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.network.EquipUnequipBackWearablePacket;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Created on 03/01/2015
 *
 * @author Darkona
 */
public class GuiCopterPack extends GuiWithTanks
{
    private InventoryCopterPack inventory;
    private static final ResourceLocation texture = Resources.guiTextures("guiCopterPack");
    private static GuiTank fuelTank = new GuiTank(8, 8,72, 32, ConfigHandler.GUI_TANK_RENDER);
    private static GuiImageButtonNormal equipButton = new GuiImageButtonNormal(150, 64, 18, 18);
    private static GuiImageButtonNormal unequipButton = new GuiImageButtonNormal(150, 64, 18, 18);

    private boolean wearing;
    EntityPlayer player;

    public GuiCopterPack(EntityPlayer player, InventoryCopterPack inv, boolean wearing)
    {
        super(new ContainerCopter(player, inv, wearing));
        this.inventory = wearing ? (InventoryCopterPack)BackpackProperty.get(player).getInventory() : inv;
        xSize = 176;
        ySize = 166;
        this.wearing = wearing;
        this.player = player;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int mouseX, int mouseY)
    {
        GL11.glColor4f(1, 1, 1, 1);
        this.mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if(wearing)
        {
            if(unequipButton.inButton(this,mouseX,mouseY))
            {
                unequipButton.draw(this,20,186);
            }else
            {
                unequipButton.draw(this,1,186);
            }
        }else
        {
            if(equipButton.inButton(this,mouseX,mouseY))
            {
                equipButton.draw(this,20,167);
            }else
            {
                equipButton.draw(this,1,167);
            }
        }


    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        inventory.openInventory();
        FluidTank fuel = inventory.getFuelTank();
        fuelTank.draw(this, fuel);

        GL11.glPushMatrix();
        String name = (fuel.getFluid() != null) ? Utils.capitalize(fuel.getFluid().getFluid().getName()) : "None";
        String amount = (fuel.getFluid() != null) ? ""+ fuel.getFluid().amount : "0";
        String capacity = Integer.toString(fuel.getCapacity());
        int offsetY = 8;
        int offsetX = 83;
        fontRendererObj.drawString(name, 1 + offsetX, offsetY, 0x373737, false);
        fontRendererObj.drawString(amount, 1 + offsetX, 10 + offsetY, 0x373737, false);
        fontRendererObj.drawString(capacity, 1 + offsetX, 20 + offsetY, 0x373737, false);

        if(fuel.getFluid()!=null)
        {
            Float f = GeneralReference.liquidFuels.get(name);
            String conLev = (f != null) ? f.toString() : "0";
            if(conLev != null && !conLev.isEmpty())
                fontRendererObj.drawString("Consumption: " + conLev , 1 + offsetX, 40 + offsetY, 0x373737, false);
        }
        GL11.glPopMatrix();
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
    public float getZLevel()
    {
        return zLevel;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        int sneakKey = Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
        if(wearing)
        {
            if(unequipButton.inButton(this,mouseX,mouseY))
            {
                ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.UNEQUIP_WEARABLE,false));
                player.closeScreen();
            }
        } else
        {
            if(equipButton.inButton(this,mouseX,mouseY))
            {
                ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.EQUIP_WEARABLE, Keyboard.isKeyDown(sneakKey)));
                player.closeScreen();
            }
        }
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

    @Override
    public void onGuiClosed()
    {
        if (inventory != null)
        {
            inventory.closeInventory();
        }
        super.onGuiClosed();
    }
}
