package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import com.darkona.adventurebackpack.config.GeneralConfig;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.BackpackContainer;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.network.GuiBackpackMessage;
import com.darkona.adventurebackpack.network.MessageConstants;
import com.darkona.adventurebackpack.network.SleepingBagMessage;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

/**
 * Created by Darkona on 12/10/2014.
 */
@SideOnly(Side.CLIENT)
public class GuiAdvBackpack extends GuiContainer implements IBackpackGui
{

    protected IAdvBackpack inventory;
    protected boolean source;
    private boolean wearing;
    protected int X;
    protected int Y;
    protected int Z;
    private EntityPlayer player;
    private static final ResourceLocation texture = Resources.guiTextures("guiBackpack");
    private static GuiImageButtonNormal bedButton = new GuiImageButtonNormal(71, 15, 18, 18);
    private static GuiImageButtonNormal craftButton = new GuiImageButtonNormal(90, 15, 18, 18);
    private static GuiTank tankLeft = new GuiTank(26, 7, 64, 16, GeneralConfig.GUI_TANK_RES);
    private static GuiTank tankRight = new GuiTank(134, 7, 64, 16, GeneralConfig.GUI_TANK_RES);
    private FluidStack lft;
    private FluidStack rgt;
    public int lefties;
    public int topsies;


    public GuiAdvBackpack(EntityPlayer player, TileAdventureBackpack tileBackpack)
    {
        super(new BackpackContainer(player.inventory, tileBackpack, BackpackContainer.SOURCE_TILE));
        this.inventory = tileBackpack;
        this.source = true;
        xSize = 176;
        ySize = 166;
        this.X = tileBackpack.xCoord;
        this.Y = tileBackpack.yCoord;
        this.Z = tileBackpack.zCoord;
        this.player = player;
        this.lefties = guiLeft;
        this.topsies = guiTop;
    }

    public GuiAdvBackpack(EntityPlayer player, InventoryItem item, boolean wearing)
    {
        super(new BackpackContainer(player.inventory, item, BackpackContainer.SOURCE_ITEM));
        this.inventory = item;
        this.wearing = wearing;
        this.source = false;
        xSize = 176;
        ySize = 166;
        this.player = player;
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

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        // Buttons and button highlight
        int srcX = 177;
        int srcY = 35;
        if (source)
        {
            if (bedButton.inButton(this, mouseX, mouseY))
            {
                bedButton.draw(this, srcX + 19, srcY + 19);

            } else
            {
                bedButton.draw(this, srcX, srcY + 19);
            }
        }

        if (craftButton.inButton(this, mouseX, mouseY))
        {
            craftButton.draw(this, srcX + 19, srcY);
        } else
        {
            craftButton.draw(this, srcX, srcY);
        }

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {

        inventory.openInventory();
        lft = inventory.getLeftTank().getFluid();
        rgt = inventory.getRightTank().getFluid();

        tankLeft.draw(this, lft);
        tankRight.draw(this, rgt);

        if (tankLeft.inTank(this, mouseX, mouseY))
        {
            drawHoveringText(tankLeft.getTankTooltip(), mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
        }

        if (tankRight.inTank(this, mouseX, mouseY))
        {
            drawHoveringText(tankRight.getTankTooltip(), mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
        }

        /*GL11.glPushMatrix();
        GL11.glScalef(0.8f, 0.8f, 0.8f);*/
        String name = "Adventure Backpack";
        fontRendererObj.drawString(name, ((xSize - fontRendererObj.getStringWidth(name)) / 2), 4, 0x404040);
        /*GL11.glPopMatrix();*/
    }

    @Override
    public float getZLevel()
    {
        return this.zLevel;
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
    protected void mouseClicked(int mouseX, int mouseY, int button)
    {
        if (bedButton.inButton(this, mouseX, mouseY) && source)
        {
            TileAdventureBackpack te = (TileAdventureBackpack) inventory;
            ModNetwork.networkWrapper.sendToServer(new SleepingBagMessage(te.xCoord, te.yCoord, te.zCoord));
        } else if (craftButton.inButton(this, mouseX, mouseY))
        {
            if (source)
            {
                ModNetwork.networkWrapper
                        .sendToServer(new GuiBackpackMessage(MessageConstants.CRAFT_GUI,
                                MessageConstants.FROM_TILE));
            } else
            {
                ModNetwork.networkWrapper
                        .sendToServer(new GuiBackpackMessage(MessageConstants.CRAFT_GUI,
                                wearing ? MessageConstants.FROM_KEYBIND : MessageConstants.FROM_HOLDING));
            }

        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int par3)
    {

        super.mouseMovedOrUp(mouseX, mouseY, par3);
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
