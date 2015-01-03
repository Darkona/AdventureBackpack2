package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.handlers.GuiHandler;
import com.darkona.adventurebackpack.inventory.BackpackContainer;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/**
 * Created by Darkona on 12/10/2014.
 */
public class GUIPacket implements IMessageHandler<GUIPacket.GUImessage,IMessage>
{

    @Override
    public IMessage onMessage(GUImessage message, MessageContext ctx)
    {
        if(ctx.side.isServer())
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if (player != null)
            {
                int playerX = (int) player.posX;
                int playerY = (int) player.posY;
                int playerZ = (int) player.posZ;
                World world = player.worldObj;
                if(message.type == MessageConstants.COPTER_GUI)
                {
                    if(Wearing.isHoldingCopter(player))
                    {
                        FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.COPTER_HOLDING, world, playerX, playerY, playerZ);
                    }
                }

                if (message.type == MessageConstants.NORMAL_GUI)
                {
                    if (message.from == MessageConstants.FROM_KEYBIND)
                    {
                        if (Wearing.isWearingBackpack(player))
                        {
                            FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.BACKPACK_WEARING, world, playerX, playerY, playerZ);
                        }
                    }
                    if (message.from == MessageConstants.FROM_HOLDING)
                    {
                        if (Wearing.isHoldingBackpack(player))
                        {
                            FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.BACKPACK_HOLDING, world, playerX, playerY, playerZ);
                        }
                    }
                }

                if (message.from == MessageConstants.FROM_TILE)
                {

                    if (message.type == MessageConstants.NORMAL_GUI)
                    {

                        if (player.openContainer instanceof BackpackContainer)
                        {
                            TileAdventureBackpack te = (TileAdventureBackpack) ((BackpackContainer) player.openContainer).inventory;
                            FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.BACKPACK_TILE, world, te.xCoord, te.yCoord, te.zCoord);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static class GUImessage implements IMessage
    {
        private byte from;
        private byte type;

        public GUImessage()
        {
        }

        public GUImessage(byte type, byte from)
        {
            this.type = type;
            this.from = from;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            this.type = buf.readByte();
            this.from = buf.readByte();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(type);
            buf.writeByte(from);
        }
    }
}
