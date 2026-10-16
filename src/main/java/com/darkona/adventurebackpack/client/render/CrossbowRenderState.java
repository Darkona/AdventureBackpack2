package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.item.ItemCrossbow;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@SideOnly(Side.CLIENT)
public class CrossbowRenderState
{
    private ItemStack cached;
    public int ammo;
    public long lastShot;
    public long lastReload;
    public boolean reloadSwap;
    public int mode;
    public boolean fed;
    public int quiverShots;

    public void update(ItemStack stack)
    {
        if (stack == cached) return;
        cached = stack;
        NBTTagCompound tag = stack != null && stack.hasTagCompound() ? stack.getTagCompound() : null;
        if (tag == null)
        {
            ammo = 0;
            lastShot = 0;
            lastReload = 0;
            reloadSwap = false;
            mode = 0;
            fed = false;
            quiverShots = 0;
            return;
        }
        ammo = tag.getInteger(ItemCrossbow.TAG_AMMO);
        lastShot = tag.getLong(ItemCrossbow.TAG_LAST_SHOT);
        lastReload = tag.getLong(ItemCrossbow.TAG_LAST_RELOAD);
        reloadSwap = tag.getBoolean(ItemCrossbow.TAG_RELOAD_SWAP);
        mode = tag.getInteger(ItemCrossbow.TAG_MODE);
        fed = tag.getBoolean(ItemCrossbow.TAG_FED);
        quiverShots = tag.getInteger(ItemCrossbow.TAG_QUIVER_SHOTS);
    }
}
