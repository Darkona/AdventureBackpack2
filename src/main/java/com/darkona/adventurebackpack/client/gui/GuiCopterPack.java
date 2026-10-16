package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.common.RenderInfoCache;
import com.darkona.adventurebackpack.common.ShownKey;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerCopter;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.util.Wearing;
import com.darkona.adventurebackpack.network.EquipUnequipBackWearablePacket;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import com.darkona.adventurebackpack.block.TileGear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

public class GuiCopterPack extends GuiWithTanks
{
    private TileGear tile;
    private InventoryCopterPack inventory;
    private static final ResourceLocation texture = Resources.guiTextures("guiCopterPack");
    private static GuiTank fuelTank = new GuiTank(8, 8,72, 32, ConfigHandler.GUI_TANK_RENDER);
    private static GuiImageButtonNormal equipButton = new GuiImageButtonNormal(150, 64, 18, 18);
    private static GuiImageButtonNormal unequipButton = new GuiImageButtonNormal(150, 64, 18, 18);
    private static final GuiToggleButton engineButton = new GuiToggleButton(128, 66, 18, 12, "adventurebackpack:gui.toggle.copter");
    private ItemStack shownCopter;
    private boolean shownEngine;

    private boolean wearing;
    EntityPlayer player;

    public GuiCopterPack(EntityPlayer player, InventoryCopterPack inv, TileGear tile)
    {
        super(new ContainerCopter(player, inv, tile));
        this.inventory = inv;
        this.player = player;
        this.tile = tile;
        this.wearing = false;
        xSize = 176;
        ySize = 166;
    }

    public GuiCopterPack(EntityPlayer player, InventoryCopterPack inv, boolean wearing)
    {
        super(new ContainerCopter(player, inv, wearing));
        this.inventory = inv;
        xSize = 176;
        ySize = 166;
        this.wearing = wearing;
        this.player = player;
        if (wearing) setToggle(engineButton);
    }

    @Override
    protected boolean isToggleOn()
    {
        ItemStack copter = Wearing.getWearingCopter(player);
        if (copter != shownCopter)
        {
            shownCopter = copter;
            shownEngine = copter != null && copter.hasTagCompound() && copter.getTagCompound().getByte("status") != ItemCopterPack.OFF_MODE;
        }
        return shownEngine;
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
        }else if (tile == null)
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

    private ItemStack shownStack;
    private int shownVersion = -1;

    private void refreshShown()
    {
        ItemStack source = tile != null ? tile.getGear() : wearing ? Wearing.getWearingCopter(player) : inventory.getParentItemStack();
        if (source == null) return;
        int version = RenderInfoCache.get(source).version();
        if (source == shownStack && version == shownVersion) return;
        if (source != inventory.getParentItemStack()) inventory.setContainerStack(source);
        inventory.openInventory();
        shownStack = source;
        shownVersion = version;
    }

    private final ShownKey labelKey = new ShownKey();
    private String nameLabel = "";
    private String amountLabel = "";
    private String capacityLabel = "";
    private String consumptionLabel;
    private int shownCapacity = -1;

    private void refreshLabels(FluidTank fuel)
    {
        FluidStack stack = fuel.getFluid();
        Fluid fluid = stack == null ? null : stack.getFluid();
        int amount = stack == null ? 0 : stack.amount;
        boolean fluidChanged = labelKey.changed(fluid, amount, stack);
        if (!fluidChanged && fuel.getCapacity() == shownCapacity) return;
        shownCapacity = fuel.getCapacity();
        String fluidName = fluid == null ? null : fluid.getName();
        nameLabel = GearLabels.fuelName(fluidName);
        amountLabel = GearLabels.fuelAmount(stack != null, amount);
        capacityLabel = Integer.toString(shownCapacity);
        consumptionLabel = fluid == null ? null : GearLabels.consumption(GeneralReference.liquidFuels.containsKey(fluidName),
                GeneralReference.liquidFuels.get(fluidName));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        refreshShown();
        FluidTank fuel = inventory.getFuelTank();
        fuelTank.draw(this, fuel);

        GL11.glPushMatrix();
        refreshLabels(fuel);
        int offsetY = 8;
        int offsetX = 83;
        fontRendererObj.drawString(nameLabel, 1 + offsetX, offsetY, 0x373737, false);
        fontRendererObj.drawString(amountLabel, 1 + offsetX, 10 + offsetY, 0x373737, false);
        fontRendererObj.drawString(capacityLabel, 1 + offsetX, 20 + offsetY, 0x373737, false);

        if (consumptionLabel != null)
        {
            fontRendererObj.drawString(consumptionLabel, 1 + offsetX, 40 + offsetY, 0x373737, false);
        }
        GL11.glPopMatrix();
        drawToggle(mouseX, mouseY);
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
        if (clickToggle(mouseX, mouseY)) return;
        if(wearing)
        {
            if(unequipButton.inButton(this,mouseX,mouseY))
            {
                ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.UNEQUIP_WEARABLE,false));
                player.closeScreen();
                return;
            }
        } else if (tile == null)
        {
            if(equipButton.inButton(this,mouseX,mouseY))
            {
                ModNetwork.net.sendToServer(new EquipUnequipBackWearablePacket.Message(EquipUnequipBackWearablePacket.EQUIP_WEARABLE, GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)));
                player.closeScreen();
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char key, int keycode)
    {
        if (keycode == Keybindings.gear.getKeyCode())
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
