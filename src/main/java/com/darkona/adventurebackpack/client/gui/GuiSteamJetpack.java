package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.block.TileGear;
import com.darkona.adventurebackpack.common.RenderInfoCache;
import com.darkona.adventurebackpack.common.ShownKey;
import com.darkona.adventurebackpack.common.layout.gear.GearWidgets;
import com.darkona.adventurebackpack.inventory.ContainerJetpack;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

@SideOnly(Side.CLIENT)
public class GuiSteamJetpack extends GuiGear
{
    private static final String TOGGLE = "adventurebackpack:gui.toggle.jetpack";
    private static final int BURN_STEPS = 100;

    InventorySteamJetpack inventory;
    private ItemStack shownStack;
    private int shownVersion;
    private final ShownKey waterKey = new ShownKey();
    private final ShownKey steamKey = new ShownKey();
    private int shownTemperature = Integer.MIN_VALUE;
    private String waterLabel = "";
    private String steamLabel = "";
    private String temperatureLabel = "";

    public GuiSteamJetpack(EntityPlayer player, InventorySteamJetpack inventory, TileGear tile)
    {
        super(new ContainerJetpack(player, inventory, tile), player, tile, false, TOGGLE);
        this.inventory = inventory;
    }

    public GuiSteamJetpack(EntityPlayer player, InventorySteamJetpack inventory, boolean wearing)
    {
        super(new ContainerJetpack(player, inventory, wearing), player, null, wearing, TOGGLE);
        this.inventory = inventory;
    }

    @Override
    protected boolean isToggleOn()
    {
        return inventory.getStatus();
    }

    @Override
    protected FluidTank fluid(int tank)
    {
        return tank == 0 ? inventory.getWaterTank() : inventory.getSteamTank();
    }

    @Override
    protected float barFill(int bar)
    {
        if (bar == GearWidgets.BAR_TEMPERATURE) return (float) inventory.getTemperature() / InventorySteamJetpack.MAX_TEMPERATURE;
        return inventory.getBurnTicks() > 0 ? (float) inventory.getBurnTimeRemainingScaled(BURN_STEPS) / BURN_STEPS : 0;
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

    private void refreshLabels(FluidTank water, FluidTank steam, int temperature)
    {
        boolean changed = false;
        FluidStack w = water.getFluid();
        if (waterKey.changed(w == null ? null : w.getFluid(), water.getFluidAmount(), null))
        {
            waterLabel = GearLabels.tank(water.getFluidAmount() > 0 ? FluidRegistry.getFluidName(w) : null, water.getFluidAmount());
            changed = true;
        }
        FluidStack s = steam.getFluid();
        if (steamKey.changed(s == null ? null : s.getFluid(), steam.getFluidAmount(), null))
        {
            steamLabel = GearLabels.tank(steam.getFluidAmount() > 0 ? FluidRegistry.getFluidName(s) : null, steam.getFluidAmount());
            changed = true;
        }
        if (temperature != shownTemperature)
        {
            temperatureLabel = GearLabels.temperature(temperature);
            shownTemperature = temperature;
            changed = true;
        }
        if (changed) setLabels(waterLabel, steamLabel, temperatureLabel);
    }

    @Override
    protected void update()
    {
        refreshShown();
        refreshLabels(inventory.getWaterTank(), inventory.getSteamTank(), inventory.getTemperature());
    }
}
