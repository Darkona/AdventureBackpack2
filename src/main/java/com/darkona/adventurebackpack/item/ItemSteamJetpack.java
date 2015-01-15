package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.client.models.ModelSteamJetpack;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class ItemSteamJetpack extends ItemAB implements IBackWearableItem
{

    public static byte OFF_MODE = 0;
    public static byte NORMAL_MODE = 1;
    public static byte HOVER_MODE = 2;

    public ItemSteamJetpack()
    {
        super();
        setUnlocalizedName("steamJetpack");
        setFull3D();
        setMaxStackSize(1);
    }
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.JETPACK_GUI, GUIPacket.FROM_HOLDING));
        }
        return stack;
    }


    @Override
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack)
    {

    }

    @Override
    public void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
    {

    }

    @Override
    public void onEquipped(World world, EntityPlayer player, ItemStack stack)
    {

    }

    @Override
    public void onUnequipped(World world, EntityPlayer player, ItemStack stack)
    {

    }

    private ModelSteamJetpack model = new ModelSteamJetpack();
    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getWearableModel(ItemStack wearable)
    {
        return model.setWearable(wearable);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearableTexture(ItemStack wearable)
    {
        return Resources.modelTextures("steamJetpack");
    }
}
