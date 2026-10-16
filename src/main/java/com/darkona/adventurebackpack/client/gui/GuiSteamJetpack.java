package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.common.RenderInfoCache;
import com.darkona.adventurebackpack.common.ShownKey;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerJetpack;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.network.EquipUnequipBackWearablePacket;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import com.darkona.adventurebackpack.block.TileGear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

public class GuiSteamJetpack extends GuiWithTanks
{
    private TileGear tile;
    private static final ResourceLocation texture = Resources.guiTextures("guiSteamJetpack");
    private static GuiTank waterTank = new GuiTank(8, 8,72, 16, ConfigHandler.GUI_TANK_RENDER);
    private static GuiTank steamTank = new GuiTank(116, 8,72, 16, ConfigHandler.GUI_TANK_RENDER);
    private static GuiImageButtonNormal equipButton = new GuiImageButtonNormal(150, 64, 18, 18);
    private static GuiImageButtonNormal unequipButton = new GuiImageButtonNormal(150, 64, 18, 18);
    private static final GuiToggleButton burnButton = new GuiToggleButton(150, 44, 18, 12, "adventurebackpack:gui.toggle.jetpack");
    private boolean wearing;
    EntityPlayer player;
    InventorySteamJetpack inventory;
    int boiling = 0;
    private ItemStack shownStack;
    private int shownVersion;
    public GuiSteamJetpack(EntityPlayer player, InventorySteamJetpack inventory, TileGear tile)
    {
        super(new ContainerJetpack(player, inventory, tile));
        this.inventory = inventory;
        this.player = player;
        this.tile = tile;
        this.wearing = false;
        xSize = 176;
        ySize = 166;
    }

    public GuiSteamJetpack(EntityPlayer player, InventorySteamJetpack inventory, boolean wearing)
    {
        super(new ContainerJetpack(player, inventory,wearing));
        this.wearing = wearing;
        this.player = player;
        this.inventory = inventory;
        xSize = 176;
        ySize = 166;
        if (wearing) setToggle(burnButton);
    }

    @Override
    protected boolean isToggleOn()
    {
        return inventory.getStatus();
    }

    private void refreshShown()
    {
        ItemStack source = tile != null ? tile.getGear() : wearing ? Wearing.getWearingSteam(player) : inventory.getContainerStack();
        if (source == null) return;
        int version = RenderInfoCache.get(source).version();
        if (source == shownStack && version == shownVersion) return;
        if (source != inventory.getContainerStack()) inventory = new InventorySteamJetpack(source);
        else if (shownStack != null) inventory.openInventory();
        shownStack = source;
        shownVersion = version;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int mouseX, int mouseY)
    {
        refreshShown();
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

    private final ShownKey waterKey = new ShownKey();
    private final ShownKey steamKey = new ShownKey();
    private int shownTemperature = Integer.MIN_VALUE;
    private String waterLabel = "";
    private String steamLabel = "";
    private String temperatureLabel = "";

    private void refreshLabels(FluidTank water, FluidTank steam, int temperature)
    {
        FluidStack w = water.getFluid();
        if (waterKey.changed(w == null ? null : w.getFluid(), water.getFluidAmount(), null))
        {
            waterLabel = GearLabels.tank(water.getFluidAmount() > 0 ? FluidRegistry.getFluidName(w) : null, water.getFluidAmount());
        }
        FluidStack s = steam.getFluid();
        if (steamKey.changed(s == null ? null : s.getFluid(), steam.getFluidAmount(), null))
        {
            steamLabel = GearLabels.tank(steam.getFluidAmount() > 0 ? FluidRegistry.getFluidName(s) : null, steam.getFluidAmount());
        }
        if (temperature != shownTemperature)
        {
            temperatureLabel = GearLabels.temperature(temperature);
            shownTemperature = temperature;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.mc.renderEngine.bindTexture(texture);
        FluidTank water = inventory.getWaterTank();
        FluidTank steam = inventory.getSteamTank();
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if(     inventory.isBoiling()
                && steam.getFluidAmount() < steam.getCapacity()
                && water.getFluidAmount() > 0)
        {
            if(boiling < 83)
            {
                drawTexturedModalRect(28,40,50,172,boiling++,37);
            }else boiling = 0;
        }
        if(inventory.getBurnTicks() > 0)
        {
            int i1 =inventory.getBurnTimeRemainingScaled(13);
            this.drawTexturedModalRect(78, 48 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
        }

        int H = Math.round(((float)72 / (float)InventorySteamJetpack.MAX_TEMPERATURE) * (float)inventory.getTemperature());
        drawTexturedModalRect( 139, 8 + (72- H), 40, 167 + (72 - H), 5, H);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);

        waterTank.draw(this, water);
        steamTank.draw(this,steam);

        float factor = 0.7f;

        refreshLabels(water, steam, inventory.getTemperature());
        String show = waterLabel;

        GL11.glPushMatrix();
        GL11.glTranslatef(70, 10, 0);
        GL11.glScalef(factor, factor, factor);
        fontRendererObj.drawString(show, 0, 0, 0x373737, false);
        GL11.glScalef(1, 1, 1);
        GL11.glPopMatrix();

        show = steamLabel;

        GL11.glPushMatrix();
        GL11.glTranslatef(70, 20, 0);
        GL11.glScalef(factor, factor, factor);
        fontRendererObj.drawString(show, 0, 0, 0x373737, false);
        GL11.glScalef(1, 1, 1);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslatef(145,8,0);
        GL11.glScalef(0.9f, 0.9f, 0.9f);
        fontRendererObj.drawString(temperatureLabel, 0, 0, 0x373737, false);
        GL11.glScalef(1, 1, 1);
        GL11.glPopMatrix();
        drawToggle(mouseX, mouseY);
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
        if (keycode == Keybindings.gear.getKeyCode())
        {
            player.closeScreen();
        }
        super.keyTyped(key, keycode);
    }
}
