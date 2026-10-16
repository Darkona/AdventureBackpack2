package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.block.TileStation;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.ContainerFuelStation;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiFuelStation extends GuiWithTanks
{
    private static final ResourceLocation TEXTURE = Resources.guiTextures("guiFuelStation");

    private final TileStation station;
    private final GuiTank tankView = new GuiTank(8, 8, 72, 32, ConfigHandler.GUI_TANK_RENDER);
    private int shownFluid = -1;
    private int shownAmount = -1;
    private String nameText = "";
    private String amountText = "";
    private String costText = "";

    public GuiFuelStation(EntityPlayer player, TileStation station)
    {
        super(new ContainerFuelStation(player, station));
        this.station = station;
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        FluidTank tank = station.getTank();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        try
        {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            tankView.draw(this, tank);
        } finally
        {
            GL11.glPopAttrib();
        }
        refreshTexts(tank);
        fontRendererObj.drawString(nameText, 66, 8, 0x373737, false);
        fontRendererObj.drawString(amountText, 66, 18, 0x373737, false);
        fontRendererObj.drawString(costText, 66, 28, 0x373737, false);
    }

    private void refreshTexts(FluidTank tank)
    {
        int fluid = station.syncedFluidId();
        int amount = tank.getFluidAmount();
        if (fluid == shownFluid && amount == shownAmount) return;
        shownFluid = fluid;
        shownAmount = amount;
        FluidStack stack = tank.getFluid();
        if (fluid == 0 || stack == null)
        {
            nameText = StatCollector.translateToLocal("adventurebackpack:gui.fuelstation.empty");
            costText = "";
        } else
        {
            nameText = stack.getLocalizedName();
            costText = StatCollector.translateToLocalFormatted("adventurebackpack:gui.fuelstation.cost", GeneralReference.fuelMultiplier(stack.getFluid()));
        }
        amountText = amount + " / " + tank.getCapacity() + " mB";
    }
}
