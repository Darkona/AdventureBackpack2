package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;

import java.util.UUID;

/**
 * Created on 16/10/2014
 *
 * @author Darkona
 */
public class CowAbilityPacket implements IMessageHandler<CowAbilityPacket.CowAbilityMessage, IMessage>
{

    public static final byte CONSUME_WHEAT = 0;

    @Override
    public IMessage onMessage(CowAbilityMessage message, MessageContext ctx)
    {
        if (ctx.side.isClient())
        {
            EntityPlayer player = Minecraft.getMinecraft().theWorld.func_152378_a(UUID.fromString(message.playerID));

            if (player.openContainer instanceof ContainerBackpack)
            {
                ContainerBackpack cont = ((ContainerBackpack) player.openContainer);
                cont.detectAndSendChanges();
                IInventoryAdventureBackpack inv = cont.inventory;
                switch (message.action)
                {
                    case CONSUME_WHEAT:
                        inv.consumeInventoryItem(Items.wheat);
                }
            }
        }
        return null;
    }

    public static class CowAbilityMessage implements IMessage
    {

        private byte action;
        private String playerID;

        public CowAbilityMessage()
        {
        }

        public CowAbilityMessage(String playerID, byte action)
        {
            this.playerID = playerID;
            this.action = action;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {

            playerID = ByteBufUtils.readUTF8String(buf);
            action = buf.readByte();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, playerID);
            buf.writeByte(action);
        }
    }

}
