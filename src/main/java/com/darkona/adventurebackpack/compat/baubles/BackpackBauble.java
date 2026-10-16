package com.darkona.adventurebackpack.compat.baubles;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.util.LogHelper;
import cpw.mods.fml.common.Loader;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class BackpackBauble
{
    public static final String[] TYPES = {"back"};

    private static boolean loaded = false;

    private BackpackBauble()
    {
    }

    public static void preInit()
    {
        loaded = Loader.isModLoaded("Baubles");
        if (!Loader.isModLoaded("Baubles|Expanded")) return;
        try
        {
            BaublesCompat.registerBackSlot();
        } catch (Throwable t)
        {
            LogHelper.error("Could not add the Baubles Expanded back slot: " + t);
        }
    }

    public static boolean isLoaded()
    {
        return loaded;
    }

    public static boolean canEquip(ItemStack stack, EntityLivingBase entity)
    {
        boolean isPlayer = entity instanceof EntityPlayer;
        BackpackProperty props = isPlayer ? BackpackProperty.get((EntityPlayer) entity) : null;
        return BaubleRules.canEquip(ConfigHandler.COMPAT_BAUBLES_ENABLED, isPlayer && props != null, props != null && props.hasWearable());
    }

    public static void onEquipped(ItemStack stack, EntityLivingBase entity)
    {
        tick(stack, entity);
    }

    public static void tick(ItemStack stack, EntityLivingBase entity)
    {
        if (!(entity instanceof EntityPlayer) || entity.worldObj == null || entity.worldObj.isRemote || stack == null) return;
        EntityPlayer player = (EntityPlayer) entity;
        BackpackProperty props = BackpackProperty.get(player);
        if (props == null) return;
        boolean linkedToThis = props.isWearableInBauble() && props.getWearable() == stack;
        boolean wears = props.hasWearable();
        boolean stale = wears && !linkedToThis && props.isWearableInBauble() && !BaublesCompat.holds(player, props.getWearable());
        switch (BaubleRules.tick(ConfigHandler.COMPAT_BAUBLES_ENABLED, linkedToThis, wears, stale))
        {
            case LINK:
                props.unlinkBauble();
                props.linkBauble(stack);
                BackpackProperty.sync(player);
                break;
            case UNLINK:
                props.unlinkBauble();
                BackpackProperty.sync(player);
                break;
            case EJECT:
                BaublesCompat.eject(player, stack);
                break;
            default:
                break;
        }
    }

    public static void onUnequipped(ItemStack stack, EntityLivingBase entity)
    {
        if (!(entity instanceof EntityPlayer) || entity.worldObj == null || entity.worldObj.isRemote) return;
        EntityPlayer player = (EntityPlayer) entity;
        BackpackProperty props = BackpackProperty.get(player);
        if (props != null && props.isWearableInBauble() && props.getWearable() == stack)
        {
            props.unlinkBauble();
            BackpackProperty.sync(player);
        }
    }

    public static ItemStack takeOutOfSlot(EntityPlayer player)
    {
        BackpackProperty props = BackpackProperty.get(player);
        if (props == null || !props.isWearableInBauble()) return null;
        ItemStack stack = props.getWearable();
        boolean removed = loaded && BaublesCompat.remove(player, stack);
        props.unlinkBauble();
        return removed ? stack : null;
    }
}
