package com.darkona.adventurebackpack.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created on 08/01/2015
 *
 * @author Darkona
 */
public interface IBackWearableItem
{
    void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack);

    void onPlayerDeath(World world, EntityPlayer player, ItemStack stack);

    void onEquipped(World world, EntityPlayer player, ItemStack stack);

    void onUnequipped(World world, EntityPlayer player, ItemStack stack);

    @SideOnly(Side.CLIENT)
    ModelBiped getWearableModel(ItemStack wearable);

    @SideOnly(Side.CLIENT)
    ResourceLocation getWearableTexture(ItemStack wearable);

}
