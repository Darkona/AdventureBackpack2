package com.darkona.adventurebackpack.compat.baubles;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.util.LogHelper;
import cpw.mods.fml.common.Loader;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * The Adventure Backpack worn in a Baubles slot: the "back" slot of Baubles Expanded, or the belt slot of the
 * original Baubles. No Baubles class is named here, so this class loads without Baubles; the calls into the API
 * go through {@link BaublesCompat} and only happen when Baubles is loaded.
 * <p>
 * The stack stays in the Baubles slot, where Baubles saves it, drops it on death and shows it to grave mods.
 * {@link BackpackProperty} points at that same stack ({@link BackpackProperty#linkBauble}), so abilities, the
 * open key, the hose, the overlays and the model on the back work exactly as for a backpack worn the usual
 * way. The properties never save a linked stack, so it cannot be duplicated, and a player wears one wearable at
 * most: a backpack only goes into the slot when nothing is worn, and the usual equip refuses while one is linked.
 *
 * @author Javier Darkona
 */
public final class BackpackBauble
{
    /** Slot types the backpack fits in Baubles Expanded. */
    public static final String[] TYPES = {"back"};

    private static boolean loaded = false;

    private BackpackBauble()
    {
    }

    /** preInit. The back slot is added whenever Baubles Expanded is there, whatever the config says, so client and server always have the same slots. */
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

    /** Server: links at once so there is no tick without the backpack worn. */
    public static void onEquipped(ItemStack stack, EntityLivingBase entity)
    {
        tick(stack, entity);
    }

    /** Server, every tick while the backpack is in a slot. Field comparisons only, unless the link is broken. */
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

    /** Server: the stack left its slot (taken out, swapped, or emptied by a grave mod). */
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

    /**
     * Server: the usual unequip (GUI button) of a backpack worn from a slot. Takes it out of the slot, which
     * unlinks it, and returns the stack to hand to the player, or null when no slot holds it any more.
     */
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
