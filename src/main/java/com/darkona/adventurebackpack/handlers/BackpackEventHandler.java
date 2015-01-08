package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.network.messages.PlayerSoundPacket;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
        if (!ConfigHandler.BACKPACK_ABILITIES) return;
        if (event.item.getItem() instanceof ItemAppleGold &&
                //((ItemAppleGold) event.item.getItem()).getRarity(event.item) == EnumRarity.epic &&
                BackpackNames.getBackpackColorName(Wearing.getWearingBackpack(player)).equals("Rainbow"))
        {
            if (Wearing.getWearingBackpack(player).stackTagCompound.getInteger("lastTime") > 0) return;
            Wearing.getWearingBackpack(player).getTagCompound().setInteger("lastTime", Utils.secondsToTicks(150));
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
                ModNetwork.sendToNearby(PlayerSoundPacket.makeNyanMessage(player, PlayerSoundPacket.play), player);
            }
        }

    }

    @SubscribeEvent
    public void detectBow(ArrowNockEvent event)
    {
        if (!ConfigHandler.BACKPACK_ABILITIES) return;
        if (Wearing.isWearingBackpack(event.entityPlayer))
        {
            InventoryBackpack backpack = new InventoryBackpack(Wearing.getWearingBackpack(event.entityPlayer));
            if (BackpackNames.getBackpackColorName(backpack.getParentItemStack()).equals("Skeleton") && backpack.hasItem(Items.arrow))
            {
                event.entityPlayer.setItemInUse(event.result, event.result.getMaxItemUseDuration());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void detectArrow(ArrowLooseEvent event)
    {
        if (!ConfigHandler.BACKPACK_ABILITIES) return;
        if (Wearing.isWearingBackpack(event.entityPlayer))
        {
            InventoryBackpack backpack = new InventoryBackpack(Wearing.getWearingBackpack(event.entityPlayer));
            if (BackpackNames.getBackpackColorName(backpack.getParentItemStack()).equals("Skeleton") && backpack.hasItem(Items.arrow))
            {
                ServerActions.leakArrow(event.entityPlayer, event.bow, event.charge);
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
        if (event.entity != null && event.entity instanceof EntityPlayer)
        {
            ServerActions.electrify((EntityPlayer) event.entity);
        }
    }

}
