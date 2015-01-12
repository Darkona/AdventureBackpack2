package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.ai.EntityAIHorseFollowOwner;
import com.darkona.adventurebackpack.events.WearableEvent;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.network.messages.PlayerSoundPacket;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import java.util.UUID;

/**
 * Created on 17/10/2014
 *
 * @author Darkona
 */
public class GeneralEventHandler
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

    @SubscribeEvent
    public void makeHorsesFollowOwner(EntityJoinWorldEvent event)
    {
        if(!ConfigHandler.BACKPACK_ABILITIES)return;
        if(event.entity instanceof EntityHorse && ((EntityHorse)event.entity).isTame())
        {

            EntityHorse horse = ((EntityHorse)event.entity);
            if(!horse.isDead && horse.hasCustomNameTag())
            {
                boolean set = true;
                if(horse.worldObj.func_152378_a(UUID.fromString(horse.func_152119_ch())) != null)
                {
                    for (Object entry : horse.tasks.taskEntries)
                    {
                        if (((EntityAITasks.EntityAITaskEntry) entry).action instanceof EntityAIHorseFollowOwner)
                        {
                            set = false;
                        }
                    }
                }
                if(set)
                {
                    horse.tasks.addTask(4, new EntityAIHorseFollowOwner(horse, 1.5d, 2.0f, 20.0f));

                    if (horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.followRange) != null)
                    {
                        horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.followRange).setBaseValue(100.0D);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void backpackUnequipped(WearableEvent.UnequipWearableEvent event)
    {


    }

}
