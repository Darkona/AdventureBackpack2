package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import com.darkona.adventurebackpack.config.GeneralConfig;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.BackCraftContainer;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.network.GuiBackpackMessage;
import com.darkona.adventurebackpack.network.MessageConstants;
import com.darkona.adventurebackpack.util.Resources;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class GuiCraftAdvBackpack extends GuiContainer implements IBackpackGui
{
    protected IAdvBackpack inventory;
    protected boolean source;
    private boolean wearing;
    protected int X;
    protected int Y;
    protected int Z;
    protected EntityPlayer player;
    /* Tanks */
    private static GuiTankCraft tankLeft = new GuiTankCraft(8, 7, 64, 16, GeneralConfig.GUI_TANK_RES);
    private static GuiTankCraft tankRight = new GuiTankCraft(153, 7, 64, 16, GeneralConfig.GUI_TANK_RES);
    public int lefties;
    public int topsies;

    /* Buttons */
    private static GuiImageButtonCraft backButton = new GuiImageButtonCraft(114, 24, 18, 18);
    private static final ResourceLocation texture = Resources.guiTextures("guiBackpackCraft");

    public GuiCraftAdvBackpack(EntityPlayer player, TileAdventureBackpack tile)
    {
        super(new BackCraftContainer(player, tile));
        this.inventory = tile;
        this.source = true;
        xSize = 176;
        ySize = 166;
        X = tile.xCoord;
        Y = tile.yCoord;
        Z = tile.zCoord;
        this.lefties = guiLeft;
        this.topsies = guiTop;
    }

    public GuiCraftAdvBackpack(EntityPlayer player, InventoryItem item, boolean wearing)
    {
        super(new BackCraftContainer(player, player.worldObj, item));
        inventory = item;
        this.player = player;
        this.wearing = wearing;
        source = false;
        xSize = 176;
        ySize = 166;
        lefties = guiLeft;
        topsies = guiTop;
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
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.renderEngine.bindTexture(texture);

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        //Buttons
        int srcX = 177;
        int srcY = 111;

        if (backButton.inButton(this, mouseX, mouseY))
        {
            backButton.draw(this, srcX + 19, srcY);
        } else
        {
            backButton.draw(this, srcX, srcY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {

        this.fontRendererObj.drawString("Crafting Bench", 92, 7, 4210752);
        GuiCraftAdvBackpack.tankLeft.draw(this, inventory.getLeftTank().getFluid());
        GuiCraftAdvBackpack.tankRight.draw(this, inventory.getRightTank().getFluid());
        if (tankLeft.inTank(this, mouseX, mouseY))
        {
            drawHoveringText(tankLeft.getTankTooltip(), mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
        }

        if (tankRight.inTank(this, mouseX, mouseY))
        {
            drawHoveringText(tankRight.getTankTooltip(), mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
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
        return this.zLevel;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button)
    {
        if (backButton.inButton(this, mouseX, mouseY))
        {
            if (source)
            {
                ModNetwork.networkWrapper.sendToServer(new GuiBackpackMessage(MessageConstants.NORMAL_GUI, MessageConstants.FROM_TILE));
            } else
            {
                ModNetwork.networkWrapper
                        .sendToServer(new GuiBackpackMessage(MessageConstants.NORMAL_GUI, wearing ?
                                MessageConstants.FROM_KEYBIND : MessageConstants.FROM_HOLDING));
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
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
