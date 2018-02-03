package com.darkona.adventurebackpack.client.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.Constants.Source;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import com.darkona.adventurebackpack.inventory.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.network.PlayerActionPacket;
import com.darkona.adventurebackpack.network.SleepingBagPacket;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.TinkersUtils;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
@SideOnly(Side.CLIENT)
public class GuiAdvBackpack extends GuiWithTanks
{
    private static final ResourceLocation TEXTURE = Resources.guiTextures("guiBackpackNew");
    private static final ResourceLocation TINKERS_ICONS = TinkersUtils.getTinkersIcons();

    private static GuiImageButtonNormal bedButton = new GuiImageButtonNormal(5, 91, 18, 18);
    private static GuiImageButtonNormal equipButton = new GuiImageButtonNormal(5, 91, 18, 18);
    private static GuiImageButtonNormal unequipButton = new GuiImageButtonNormal(5, 91, 18, 18);
    private static GuiTank tankLeft = new GuiTank(25, 7, 100, 16, ConfigHandler.typeTankRender);
    private static GuiTank tankRight = new GuiTank(207, 7, 100, 16, ConfigHandler.typeTankRender);

    private IInventoryAdventureBackpack inventory;

    private boolean isHoldingSpace;

    public GuiAdvBackpack(EntityPlayer player, TileAdventureBackpack tileBackpack, Source source)
    {
        super(new ContainerBackpack(player, tileBackpack, source));
        this.player = player;
        inventory = tileBackpack;
        this.source = source;
        xSize = 248;
        ySize = 207;
    }

    public GuiAdvBackpack(EntityPlayer player, InventoryBackpack inventoryBackpack, Source source)
    {
        super(new ContainerBackpack(player, inventoryBackpack, source));
        this.player = player;
        inventory = inventoryBackpack;
        this.source = source;
        xSize = 248;
        ySize = 207;
    }

    private boolean isBedButtonCase()
    {
        return source == Source.TILE
                || (ConfigHandler.allowPortableSleepingBag && source == Source.WEARING && GuiScreen.isShiftKeyDown());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        GL11.glColor4f(1, 1, 1, 1);
        this.mc.renderEngine.bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        // Buttons and button highlight
        if (isBedButtonCase())
        {
            if (bedButton.inButton(this, mouseX, mouseY))
                bedButton.draw(this, 20, 227);
            else
                bedButton.draw(this, 1, 227);
        }
        else if (source == Source.WEARING)
        {
            if (unequipButton.inButton(this, mouseX, mouseY))
                unequipButton.draw(this, 96, 227);
            else
                unequipButton.draw(this, 77, 227);
        }
        else if (source == Source.HOLDING)
        {
            if (equipButton.inButton(this, mouseX, mouseY))
                equipButton.draw(this, 96, 208);
            else
                equipButton.draw(this, 77, 208);
        }
        if (ConfigHandler.tanksHoveringText)
        {
            if (tankLeft.inTank(this, mouseX, mouseY))
                drawHoveringText(tankLeft.getTankTooltip(), mouseX, mouseY, fontRendererObj);

            if (tankRight.inTank(this, mouseX, mouseY))
                drawHoveringText(tankRight.getTankTooltip(), mouseX, mouseY, fontRendererObj);
        }

        if (ConfigHandler.IS_TCONSTRUCT && ConfigHandler.allowRepairTinkerTools)
        {
            this.mc.getTextureManager().bindTexture(TINKERS_ICONS);
            this.drawTexturedModalRect(this.guiLeft + 169, this.guiTop + 77, 0, 233, 18, 18);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        inventory.openInventory();
        FluidTank lft = inventory.getLeftTank();
        FluidTank rgt = inventory.getRightTank();
        tankLeft.draw(this, lft);
        tankRight.draw(this, rgt);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    protected GuiImageButtonNormal getEquipButton()
    {
        return equipButton;
    }

    @Override
    protected GuiImageButtonNormal getUnequipButton()
    {
        return unequipButton;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button)
    {
        if (isBedButtonCase() && bedButton.inButton(this, mouseX, mouseY))
        {
            if (source == Source.TILE)
            {
                TileAdventureBackpack te = (TileAdventureBackpack) inventory;
                ModNetwork.net.sendToServer(new SleepingBagPacket.SleepingBagMessage(true, te.xCoord, te.yCoord, te.zCoord));
            }
            else
            {
                int posX = MathHelper.floor_double(player.posX);
                int posY = MathHelper.floor_double(player.posY) - 1;
                int posZ = MathHelper.floor_double(player.posZ);
                ModNetwork.net.sendToServer(new SleepingBagPacket.SleepingBagMessage(false, posX, posY, posZ));
            }
        }
        else
        {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (!isHoldingSpace)
        {
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
            {
                isHoldingSpace = true;
                ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(PlayerActionPacket.GUI_HOLDING_SPACE));
                inventory.getExtendedProperties().setBoolean(Constants.HOLDING_SPACE_TAG, true);
            }
        }
        else
        {
            if (!Keyboard.isKeyDown(Keyboard.KEY_SPACE))
            {
                isHoldingSpace = false;
                ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(PlayerActionPacket.GUI_NOT_HOLDING_SPACE));
                inventory.getExtendedProperties().removeTag(Constants.HOLDING_SPACE_TAG);
            }
        }
    }
}
