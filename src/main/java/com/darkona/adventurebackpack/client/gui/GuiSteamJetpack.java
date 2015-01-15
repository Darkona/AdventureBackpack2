package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerJetpack;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.network.EquipUnequipBackWearablePacket;
import com.darkona.adventurebackpack.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class GuiSteamJetpack extends GuiWithTanks
{
    private static final ResourceLocation texture = Resources.guiTextures("guiSteamJetpack");
    private static GuiTank waterTank = new GuiTank(8, 8,72, 16, ConfigHandler.GUI_TANK_RENDER);
    private static GuiTank steamTank = new GuiTank(116, 8,72, 16, ConfigHandler.GUI_TANK_RENDER);
    private static GuiImageButtonNormal equipButton = new GuiImageButtonNormal(150, 64, 18, 18);
    private static GuiImageButtonNormal unequipButton = new GuiImageButtonNormal(150, 64, 18, 18);
    private boolean wearing;
    EntityPlayer player;
    InventorySteamJetpack inv;
    public GuiSteamJetpack(EntityPlayer player, InventorySteamJetpack inv, boolean wearing)
    {
        super(new ContainerJetpack(player, inv));
        this.wearing = wearing;
        this.player = player;
        this.inv = inv;
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int mouseX, int mouseY)
    {
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
        inv.openInventory();
        FluidTank water = inv.getWaterTank();
        FluidTank steam = inv.getSteamTank();
        //zLevel += 5;
        waterTank.draw(this, water);
        FluidStack waterStack = water.getFluid();
        FluidStack steamStack = steam.getFluid();

        GL11.glPushMatrix();
        String name = (waterStack != null) ? waterStack.getFluid().getName() : "None";
        String amount = (waterStack != null) ? ""+waterStack.amount : "0";
        String capacity = Integer.toString(water.getCapacity());
        int offsetY = 7;
        int offsetX = 29;
        fontRendererObj.drawString(name + "-" + amount + "/" + capacity, 1 + offsetX, offsetY, 0x373737, false);
        GL11.glPopMatrix();
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
    protected void keyTyped(char key, int keycode)
    {
        if (keycode == Keybindings.openBackpack.getKeyCode())
        {
            player.closeScreen();
        }
        super.keyTyped(key, keycode);
    }
}
