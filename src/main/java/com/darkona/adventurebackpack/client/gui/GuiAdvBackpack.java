package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.BackpackContainer;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.network.SleepingBagPacket;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

/**
 * Created on 12/10/2014
 * @author Darkona
 *
 */
@SideOnly(Side.CLIENT)
public class GuiAdvBackpack extends GuiContainer implements IBackpackGui
{

    protected IAdvBackpack inventory;
    protected boolean source;
    protected boolean wearing;
    protected int X;
    protected int Y;
    protected int Z;
    private EntityPlayer player;
    private static final ResourceLocation texture = Resources.guiTextures("guiBackpackNew");
    private static GuiImageButtonNormal bedButton = new GuiImageButtonNormal(5, 91, 18, 18);
    private static GuiTank tankLeft = new GuiTank(25, 7, 100, 16, ConfigHandler.GUI_TANK_RENDER);
    private static GuiTank tankRight = new GuiTank(207, 7, 100, 16, ConfigHandler.GUI_TANK_RENDER);
    private FluidStack lft;
    private FluidStack rgt;
    public int lefties;
    public int topsies;


    public GuiAdvBackpack(EntityPlayer player, TileAdventureBackpack tileBackpack)
    {
        super(new BackpackContainer(player, tileBackpack, BackpackContainer.SOURCE_TILE));
        this.inventory = tileBackpack;
        this.source = true;
        xSize = 248;
        ySize = 207;
        this.X = tileBackpack.xCoord;
        this.Y = tileBackpack.yCoord;
        this.Z = tileBackpack.zCoord;
        this.player = player;
        this.lefties = guiLeft;
        this.topsies = guiTop;
    }

    public GuiAdvBackpack(EntityPlayer player, InventoryBackpack item, boolean wearing)
    {
        super(new BackpackContainer(player, item, wearing ?  BackpackContainer.SOURCE_WEARING : BackpackContainer.SOURCE_HOLDING));
        this.inventory = item;
        this.wearing = wearing;
        this.source = false;
        xSize = 248;
        ySize = 207;
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
        int srcX = 1;
        int srcY = 227;
        if (source)
        {
            if (bedButton.inButton(this, mouseX, mouseY))
            {
                bedButton.draw(this, srcX + 19, srcY);

            } else
            {
                bedButton.draw(this, srcX, srcY);
            }
        }




     /*   zLevel +=1;
        if (tankLeft.inTank(this, mouseX, mouseY))
        {
            drawHoveringText(tankLeft.getTankTooltip(), mouseX, mouseY, fontRendererObj);
        }

        if (tankRight.inTank(this, mouseX, mouseY))
        {
            drawHoveringText(tankRight.getTankTooltip(), mouseX, mouseY, fontRendererObj);
        }*/
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        inventory.openInventory();
        lft = inventory.getLeftTank().getFluid();
        rgt = inventory.getRightTank().getFluid();
        tankLeft.draw(this, lft);
        tankRight.draw(this, rgt);
/*
        if (tankLeft.inTank(this, mouseX, mouseY))
        {
            drawHoveringText(tankLeft.getTankTooltip(), mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
        }

        if (tankRight.inTank(this, mouseX, mouseY))
        {
            drawHoveringText(tankRight.getTankTooltip(), mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
        }
*/

        GL11.glPushMatrix();
        //GL11.glTranslatef(8f,64f,0f);
        GL11.glScalef(0.6f, 0.6f, 0.6f);
        String name = (lft != null) ? lft.getLocalizedName() : "None";
        String amount = (lft != null ? lft.amount : "Empty").toString();
        String capacity = Integer.toString(inventory.getLeftTank().getCapacity());
        int offsetY = 32;
        int offsetX = 8;
        fontRendererObj.drawString(getFirstWord(name), 1 + offsetX , 64 + offsetY,0x373737 ,false);
        fontRendererObj.drawString(amount, 1 + offsetX, 79 + offsetY, 0x373737,false);
        fontRendererObj.drawString(capacity , 1 + offsetX, 94 + offsetY, 0x373737,false);

        name = (rgt != null) ? rgt.getLocalizedName() : "None";
        amount = (rgt != null ? rgt.amount : "Empty").toString();
        fontRendererObj.drawString(getFirstWord(name), 369 + offsetX , 64 + offsetY, 0x373737,false);
        fontRendererObj.drawString(amount, 369 + offsetX, 79 + offsetY, 0x373737,false);
        fontRendererObj.drawString(capacity, 369 + offsetX, 94 + offsetY, 0x373737,false);

        GL11.glPopMatrix();
    }

    private String getFirstWord(String text) {
        if (text.indexOf(' ') > -1) { // Check if there is more than one word.
            String firstWord =  text.substring(0, text.indexOf(' '));
            String secondWord = text.substring(text.indexOf(' ')+1);
            return firstWord.equals("Molten") ? secondWord : firstWord;// Extract first word.
        } else {
            return text; // Text is the first word itself.
        }
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
            ModNetwork.net.sendToServer(new SleepingBagPacket.SleepingBagMessage(te.xCoord, te.yCoord, te.zCoord));
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
