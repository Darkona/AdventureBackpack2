package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.item.ItemCrossbow;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * What the crossbow renderer needs from a stack, read from its NBT once per stack object instead of once per frame
 * (performance contract, rule 1). The server replaces the client's stack object on every change (1.7.10 sends the
 * whole slot), so a new object means new state and the same object means nothing changed.
 */
@SideOnly(Side.CLIENT)
public class CrossbowRenderState
{
    private ItemStack cached;
    public int ammo;
    public int type;
    public long lastShot;
    public long lastReload;

    /** Refreshes the fields when {@code stack} is not the object read last time. */
    public void update(ItemStack stack)
    {
        if (stack == cached) return;
        cached = stack;
        NBTTagCompound tag = stack != null && stack.hasTagCompound() ? stack.getTagCompound() : null;
        if (tag == null)
        {
            ammo = 0;
            type = CrossbowLogic.NORMAL;
            lastShot = 0;
            lastReload = 0;
            return;
        }
        ammo = tag.getInteger(ItemCrossbow.TAG_AMMO);
        type = CrossbowLogic.clampType(tag.getInteger(ItemCrossbow.TAG_AMMO_TYPE));
        lastShot = tag.getLong(ItemCrossbow.TAG_LAST_SHOT);
        lastReload = tag.getLong(ItemCrossbow.TAG_LAST_RELOAD);
    }
}
