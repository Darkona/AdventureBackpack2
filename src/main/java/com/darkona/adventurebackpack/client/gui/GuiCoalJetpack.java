package com.darkona.adventurebackpack.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerJetpack;
import com.darkona.adventurebackpack.inventory.InventoryCoalJetpack;
import com.darkona.adventurebackpack.network.EquipUnequipBackWearablePacket;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class GuiCoalJetpack extends GuiWithTanks
{
    private static final ResourceLocation texture = Resources.guiTextures("guiCoalJetpack");
    private static GuiTank waterTank = new GuiTank(8, 8, 72, 16, ConfigHandler.typeTankRender);
    private static GuiTank steamTank = new GuiTank(116, 8, 72, 16, ConfigHandler.typeTankRender);
    private static GuiImageButtonNormal equipButton = new GuiImageButtonNormal(150, 64, 18, 18);
    private static GuiImageButtonNormal unequipButton = new GuiImageButtonNormal(150, 64, 18, 18);
    private boolean wearing;
    EntityPlayer player;
    InventoryCoalJetpack inventory;
    int boiling = 0;

    public GuiCoalJetpack(EntityPlayer player, InventoryCoalJetpack inventory, boolean wearing)
    {
        super(new ContainerJetpack(player, inventory, wearing));
        this.wearing = wearing;
        this.player = player;
        this.inventory = inventory;
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int mouseX, int mouseY)
    {
        inventory.openInventory();
        this.mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if (wearing)
        {
            if (unequipButton.inButton(this, mouseX, mouseY))
            {
                unequipButton.draw(this, 20, 186);
            } else
            {
                unequipButton.draw(this, 1, 186);
            }
        } else
        {
            if (equipButton.inButton(this, mouseX, mouseY))
            {
                equipButton.draw(this, 20, 167);
            } else
            {
                equipButton.draw(this, 1, 167);
            }
        }
        //if (wearing) inventory = new InventoryCoalJetpack(Wearing.getWearingJetpack(player));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.mc.renderEngine.bindTexture(texture);
        if (wearing)
            inventory = new InventoryCoalJetpack(Wearing.getWearingJetpack(player));
        FluidTank water = inventory.getWaterTank();
        FluidTank steam = inventory.getSteamTank();
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (inventory.isBoiling() && steam.getFluidAmount() < steam.getCapacity() && water.getFluidAmount() > 0)
        {
            if (boiling < 83)
            {
                drawTexturedModalRect(28, 40, 50, 172, boiling++, 37);
            } else
                boiling = 0;
        }
        if (inventory.getBurnTicks() > 0)
        {
            int i1 = inventory.getBurnTimeRemainingScaled(13);
            this.drawTexturedModalRect(78, 48 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
        }

        int H = Math.round(((float) 72 / (float) Constants.JETPACK_MAX_TEMPERATURE) * (float) inventory.getTemperature());
        drawTexturedModalRect(139, 8 + (72 - H), 40, 167 + (72 - H), 5, H);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);

        waterTank.draw(this, water);
        steamTank.draw(this, steam);

        float factor = 0.7f;

        String show = ((water.getFluidAmount() > 0) ? Utils.capitalize(FluidRegistry.getFluidName(water.getFluid())) : "None") + "-" + water.getFluidAmount();

        GL11.glPushMatrix();
        GL11.glTranslatef(70, 10, 0);
        GL11.glScalef(factor, factor, factor);
        fontRendererObj.drawString(show, 0, 0, 0x373737, false);
        GL11.glScalef(1, 1, 1);
        GL11.glPopMatrix();

        show = ((steam.getFluidAmount() > 0) ? Utils.capitalize(FluidRegistry.getFluidName(steam.getFluid())) : "None") + "-" + steam.getFluidAmount();

        GL11.glPushMatrix();
        GL11.glTranslatef(70, 20, 0);
        GL11.glScalef(factor, factor, factor);
        fontRendererObj.drawString(show, 0, 0, 0x373737, false);
        GL11.glScalef(1, 1, 1);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslatef(145, 8, 0);
        GL11.glScalef(0.9f, 0.9f, 0.9f);
        fontRendererObj.drawString((inventory.getTemperature()) + " C", 0, 0, 0x373737, false);
        GL11.glScalef(1, 1, 1);
        GL11.glPopMatrix();

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        //int sneakKey = Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
        if (wearing)
        {
            if (unequipButton.inButton(this, mouseX, mouseY))
            {
                ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.UNEQUIP_WEARABLE, false));
                player.closeScreen();
            }
        } else
        {
            if (equipButton.inButton(this, mouseX, mouseY))
            {
                ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.EQUIP_WEARABLE, false));
                //ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.EQUIP_WEARABLE, Keyboard.isKeyDown(sneakKey)));
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