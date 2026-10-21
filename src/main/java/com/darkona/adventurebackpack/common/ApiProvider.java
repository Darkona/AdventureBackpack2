package com.darkona.adventurebackpack.common;

import adventurebackpack.api.BackpackWidget;
import adventurebackpack.api.BackpackWidgetSkin;
import com.darkona.adventurebackpack.client.gui.widget.FrameSkin;
import com.darkona.adventurebackpack.client.gui.widget.WidgetSkin;
import com.darkona.adventurebackpack.client.gui.widget.WidgetSkins;
import com.darkona.adventurebackpack.inventory.WidgetLayouts;
import com.darkona.adventurebackpack.inventory.widget.ExternalWidgetType;
import com.darkona.adventurebackpack.inventory.widget.ExternalWidgets;

import adventurebackpack.api.AdventureBackpackAPI;
import adventurebackpack.api.FluidEffect;
import adventurebackpack.api.IApiProvider;
import adventurebackpack.api.IBackpackAbility;
import adventurebackpack.api.IBackpackView;
import com.darkona.adventurebackpack.fluids.FluidEffectRegistry;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class ApiProvider implements IApiProvider
{
    private ApiProvider()
    {
    }

    public static void install()
    {
        AdventureBackpackAPI.install(new ApiProvider());
    }

    @Override
    public ItemStack getWornWearable(EntityPlayer player)
    {
        if (player == null || BackpackProperty.get(player) == null) return null;
        ItemStack worn = Wearing.getWearingWearable(player);
        return worn == null ? null : worn.copy();
    }

    @Override
    public IBackpackView getWornBackpack(EntityPlayer player)
    {
        if (player == null || BackpackProperty.get(player) == null) return null;
        ItemStack worn = Wearing.getWearingBackpack(player);
        return worn == null ? null : new BackpackSnapshot(new InventoryBackpack(worn.copy()));
    }

    @Override
    public void registerFluidEffect(FluidEffect effect)
    {
        if (effect == null) throw new IllegalArgumentException("The fluid effect is null");
        FluidEffectRegistry.registerFluidEffect(effect);
    }

    @Override
    public void registerAbility(String colorName, IBackpackAbility ability)
    {
        AbilityRegistry.INSTANCE.register(colorName, ability);
    }

    @Override
    public void registerFuel(String fluidName, double multiplier)
    {
        CodeRegistrations.INSTANCE.addFuel(fluidName, multiplier);
        GeneralReference.init();
    }

    @Override
    public void registerAirFluid(String fluidName)
    {
        CodeRegistrations.INSTANCE.addAirFluid(fluidName);
    }

    @Override
    public void registerWidget(BackpackWidget widget)
    {
        ExternalWidgets.register(widget);
        WidgetLayouts.invalidate();
    }

    @Override
    public void registerWidgetSkin(String widgetId, BackpackWidgetSkin skin)
    {
        ExternalWidgetType type = ExternalWidgets.get(widgetId);
        if (type == null) throw new IllegalStateException("register the widget " + widgetId + " before its skin");
        if (skin == null) throw new IllegalArgumentException("null skin for " + widgetId);
        int texture = WidgetSkins.texture(skin.getTexture(), skin.getTextureSize());
        WidgetSkins.register(type.kind(), new FrameSkin(texture, skin.getFrameU(), skin.getFrameV(), skin.getTint(), WidgetSkin.DEFAULT_LAYER, skin.getEmptyTip()));
    }
}
