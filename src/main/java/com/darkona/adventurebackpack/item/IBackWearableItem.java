package com.darkona.adventurebackpack.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created on 08/01/2015
 *
 * @author Darkona
 */
public interface IBackWearableItem
{
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack);

    public void onPlayerDeath(World world, EntityPlayer player, ItemStack stack);

    public void onEquipped(World world, EntityPlayer player, ItemStack stack);

    public void onUnequipped(World world, EntityPlayer player, ItemStack stack);

    @SideOnly(Side.CLIENT)
    public ModelBiped getWearableModel(ItemStack wearable);

    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearableTexture(ItemStack wearable);

}
