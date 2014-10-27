package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.Actions;
import com.darkona.adventurebackpack.events.UnequipBackpackEvent;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.misc.NyanMovingSound;
import com.darkona.adventurebackpack.network.MessageConstants;
import com.darkona.adventurebackpack.network.NyanCatMessage;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

/**
 * Created on 17/10/2014
 *
 * @author Darkona
 */
public class BackpackEventHandler
{
    /**
     * @param event
     */
    @SubscribeEvent
    public void eatGoldenApple(PlayerUseItemEvent.Finish event)
    {
        EntityPlayer player = event.entityPlayer;

        if (event.item.getItem() instanceof ItemAppleGold &&
                //((ItemAppleGold) event.item.getItem()).getRarity(event.item) == EnumRarity.epic &&
                BackpackNames.getBackpackColorName(Wearing.getWearingBackpack(player)).equals("Rainbow"))
        {
            if (Wearing.getWearingBackpack(player).stackTagCompound.getInteger("lastTime") > 0) return;
            if (!player.worldObj.isRemote)
            {
                String nyanString =
                        EnumChatFormatting.RED + "N" +
                        EnumChatFormatting.GOLD + "Y" +
                        EnumChatFormatting.YELLOW + "A" +
                        EnumChatFormatting.GREEN + "N" +
                        EnumChatFormatting.AQUA + "C" +
                        EnumChatFormatting.BLUE + "A" +
                        EnumChatFormatting.DARK_PURPLE + "T";

                LogHelper.info(nyanString);
                player.addChatComponentMessage(new ChatComponentText(nyanString));
                ModNetwork.networkWrapper
                        .sendToServer(new NyanCatMessage(MessageConstants.PLAY_NYAN, player.getPersistentID().toString()));
                ModNetwork.networkWrapper.sendToAllAround(
                        new NyanCatMessage(
                                MessageConstants.PLAY_NYAN,
                                player.getPersistentID().toString()),
                        new NetworkRegistry.TargetPoint(
                                player.dimension,
                                player.posX,
                                player.posY,
                                player.posZ,
                                30.0D));
            }

        }

    }

    @SubscribeEvent
    public void detectBow(ArrowNockEvent event)
    {
        if (Wearing.isWearingBackpack(event.entityPlayer))
        {
            InventoryItem backpack = new InventoryItem(Wearing.getWearingBackpack(event.entityPlayer));
            if (BackpackNames.getBackpackColorName(backpack.getInventoryItem()).equals("Skeleton") && backpack.hasItem(Items.arrow))
            {
                event.entityPlayer.setItemInUse(event.result, event.result.getMaxItemUseDuration());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void detectArrow(ArrowLooseEvent event)
    {
        if (Wearing.isWearingBackpack(event.entityPlayer))
        {
            InventoryItem backpack = new InventoryItem(Wearing.getWearingBackpack(event.entityPlayer));
            if (BackpackNames.getBackpackColorName(backpack.getInventoryItem()).equals("Skeleton") && backpack.hasItem(Items.arrow))
            {
                Actions.leakArrow(event.entityPlayer, event.bow, event.charge);
                event.setCanceled(true);
            }
        }
    }

    /**
     * @param event
     */
    @SubscribeEvent
    public void detectLightning(EntityStruckByLightningEvent event)
    {
        if (event.entity !=null && event.entity instanceof EntityPlayer)
        {
            Actions.electrify((EntityPlayer) event.entity);
        }
    }

    /**
     * @param event
     */
    /*@SubscribeEvent


    public void stopMusic(UnequipBackpackEvent event)
    {
        EntityPlayer player = event.entityPlayer;
        if (event.backpack.getTagCompound().getString("colorName").equals("Nyan"))
        {
            event.backpack.getTagCompound().setInteger("lastTime", 0);
            if (Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(NyanMovingSound.instance) &&
                    NyanMovingSound.instance.getPlayer() == player)
            {
                Minecraft.getMinecraft().getSoundHandler().stopSound(NyanMovingSound.instance);
                ModNetwork.networkWrapper.sendToAllAround(
                        new NyanCatMessage(
                                MessageConstants.STOP_NYAN,
                                player.getPersistentID().toString()),
                        new NetworkRegistry.TargetPoint(
                                player.dimension,
                                player.posX,
                                player.posY,
                                player.posZ,
                                30.0D));
            }
        }
    }*/
}
