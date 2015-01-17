package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.client.models.ModelSteamJetpack;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.network.PlayerActionPacket;
import com.darkona.adventurebackpack.network.messages.PlayerSoundPacket;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
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
        InventorySteamJetpack inv = new InventorySteamJetpack(stack);
        inv.openInventory();
        boolean mustPlay = !inv.isInUse();

        inv.consumeFuel();
        inv.runBoiler();

        //Elevation
        if (Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed())
        {
            inv.setInUse(true);
            elevate(player);
        }else
        {
            inv.setInUse(false);
        }

        //Ground detection
        boolean nearGround = false;
        for (int i = (int)player.posY; i > player.posY - 5; i--)
        {
            if (world.getBlock((int)player.posX,i,(int)player.posZ).getMaterial() != Material.air && !player.onGround)
            {
                nearGround = true;
                break;
            }
        }

        //Emergency braking
        if(nearGround && player.motionY < 0 && !player.onGround && !inv.isInUse())
        {
            inv.setInUse(true);
            player.motionY *=0.4;
            player.fallDistance = 0;
            player.moveFlying(player.moveStrafing-1f,player.moveForward-1f,0.005f);
        }

        if(world.isRemote)
        {
            //Messaging
            if(inv.isInUse())
            {
                ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(PlayerActionPacket.JETPACK_IN_USE));
                //Sound
                if(mustPlay)
                {
                    ModNetwork.sendToNearby(new PlayerSoundPacket.Message(PlayerSoundPacket.JETPACK_FIZZ,player.getUniqueID().toString(),true), player);
                }else
                {
                    if(nearGround && !player.onGround)ModNetwork.sendToNearby(new PlayerSoundPacket.Message(PlayerSoundPacket.JETPACK_FIZZ,player.getUniqueID().toString(),true), player);
                }
            }else
            {
                ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(PlayerActionPacket.JETPACK_NOT_IN_USE));
            }
        }

        inv.closeInventory();
    }

    public static void elevate(EntityPlayer player)
    {
        if (player.motionY <= 0.32 && player.posY < 100){
            player.motionY += 0.09;
        }else
        {
            if (player.posY < 100) player.motionY = Math.max(player.motionY, 0.32);
            if (player.posY > 100) player.motionY = 0.32 - ((player.posY % 100) / 100);
        }
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
