package com.darkona.adventurebackpack.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import com.darkona.adventurebackpack.inventory.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.network.EquipUnequipBackWearablePacket;
import com.darkona.adventurebackpack.network.SleepingBagPacket;
import com.darkona.adventurebackpack.util.Resources;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
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
    private static GuiImageButtonNormal bedButton = new GuiImageButtonNormal(5, 91, 18, 18);
    private static GuiImageButtonNormal equipButton = new GuiImageButtonNormal(5, 91, 18, 18);
    private static GuiImageButtonNormal unequipButton = new GuiImageButtonNormal(5, 91, 18, 18);
    private static GuiTank tankLeft = new GuiTank(25, 7, 100, 16, ConfigHandler.typeTankRender);
    private static GuiTank tankRight = new GuiTank(207, 7, 100, 16, ConfigHandler.typeTankRender);
    @SuppressWarnings("FieldCanBeLocal")
    private FluidTank lft;
    @SuppressWarnings("FieldCanBeLocal")
    private FluidTank rgt;
    public int lefties;
    public int topsies;

    public GuiAdvBackpack(EntityPlayer player, TileAdventureBackpack tileBackpack)
    {
        super(new ContainerBackpack(player, tileBackpack, ContainerBackpack.SOURCE_TILE));
        this.inventory = tileBackpack;
        this.isTile = true;
        xSize = 248;
        ySize = 207;
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
        if (isTile)
        {
            if (bedButton.inButton(this, mouseX, mouseY))
            {
                bedButton.draw(this, 20, 227);
            } else
            {
                bedButton.draw(this, 1, 227);
            }
        } else
        {
            if (wearing)
            {
                if (unequipButton.inButton(this, mouseX, mouseY))
                {
                    unequipButton.draw(this, 96, 227);
                } else
                {
                    unequipButton.draw(this, 77, 227);
                }
            } else
            {
                if (equipButton.inButton(this, mouseX, mouseY))
                {
                    equipButton.draw(this, 96, 208);
                } else
                {
                    equipButton.draw(this, 77, 208);
                }
            }
        }
        //zLevel +=1;
        if (ConfigHandler.tanksHoveringText)
        {
            if (tankLeft.inTank(this, mouseX, mouseY))
            {
                drawHoveringText(tankLeft.getTankTooltip(), mouseX, mouseY, fontRendererObj);
            }

            if (tankRight.inTank(this, mouseX, mouseY))
            {
                drawHoveringText(tankRight.getTankTooltip(), mouseX, mouseY, fontRendererObj);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        inventory.openInventory();
        lft = inventory.getLeftTank();
        rgt = inventory.getRightTank();
        tankLeft.draw(this, lft);
        tankRight.draw(this, rgt);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);

        /*if (!ConfigHandler.tanksHoveringText);
        {
            GL11.glPushMatrix();
            //GL11.glTranslatef(8f,64f,0f);
            GL11.glScalef(0.6f, 0.6f, 0.6f);
            String name = (lft.getFluid() != null) ? lft.getFluid().getLocalizedName() : "None";
            String amount = (lft.getFluid() != null ? lft.getFluid().amount : "Empty").toString();
            String capacity = Integer.toString(inventory.getLeftTank().getCapacity());
            int offsetY = 32;
            int offsetX = 8;
            fontRendererObj.drawString(Utils.getFirstWord(name), 1 + offsetX, 64 + offsetY, 0x373737, false);
            fontRendererObj.drawString(amount, 1 + offsetX, 79 + offsetY, 0x373737, false);
            fontRendererObj.drawString(capacity, 1 + offsetX, 94 + offsetY, 0x373737, false);

            name = (rgt.getFluid() != null) ? rgt.getFluid().getLocalizedName() : "None";
            amount = (rgt.getFluid() != null ? rgt.getFluid().amount : "Empty").toString();
            fontRendererObj.drawString(Utils.getFirstWord(name), 369 + offsetX, 64 + offsetY, 0x373737, false);
            fontRendererObj.drawString(amount, 369 + offsetX, 79 + offsetY, 0x373737, false);
            fontRendererObj.drawString(capacity, 369 + offsetX, 94 + offsetY, 0x373737, false);

            GL11.glPopMatrix();
        }*/
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
        //int sneakKey = Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
        if (isTile)
        {
            if (bedButton.inButton(this, mouseX, mouseY))
            {
                TileAdventureBackpack te = (TileAdventureBackpack) inventory;
                ModNetwork.net.sendToServer(new SleepingBagPacket.SleepingBagMessage(te.xCoord, te.yCoord, te.zCoord));
            }
        } else
        {
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
        }
        super.mouseClicked(mouseX, mouseY, button);
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
}
