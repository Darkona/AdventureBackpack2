package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.handlers.GuiHandler;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
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
public class GUIPacket implements IMessageHandler<GUIPacket.GUImessage, IMessage>
{


    public static final byte FROM_KEYBIND = 0;
    public static final byte FROM_HOLDING = 1;
    public static final byte FROM_TILE = 2;

    public static final byte BACKPACK_GUI = 1;
    public static final byte COPTER_GUI = 2;
    public static final byte JETPACK_GUI = 3;

    @Override
    public IMessage onMessage(GUImessage message, MessageContext ctx)
    {
        if (ctx.side.isServer())
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if (player != null)
            {
                int playerX = (int) player.posX;
                int playerY = (int) player.posY;
                int playerZ = (int) player.posZ;
                World world = player.worldObj;

                if (message.type == COPTER_GUI)
                {
                    if(message.from == FROM_KEYBIND)
                    {
                        if (Wearing.isWearingCopter(player))
                        {
                            FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.COPTER_WEARING, world, playerX, playerY, playerZ);
                            return null;
                        }
                    }
                    if(message.from == FROM_HOLDING)
                    {
                        if (Wearing.isHoldingCopter(player))
                        {
                            FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.COPTER_HOLDING, world, playerX, playerY, playerZ);
                            return null;
                        }
                    }
                }
                if (message.type == JETPACK_GUI)
                {
                    if(message.from == FROM_KEYBIND)
                    {
                        if (Wearing.isWearingJetpack(player))
                        {
                            FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.JETPACK_WEARING, world, playerX, playerY, playerZ);
                            return null;
                        }
                    }
                    if(message.from == FROM_HOLDING)
                    {
                        if (Wearing.isHoldingJetpack(player))
                        {
                            FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.JETPACK_HOLDING, world, playerX, playerY, playerZ);
                            return null;
                        }
                    }
                }
                if (message.type == BACKPACK_GUI)
                {
                    Integer currentDimID = (player.worldObj.provider.dimensionId);
                    for (String id : ConfigHandler.forbiddenDimensions)
                    {
                        if (id.equals(currentDimID.toString())) return null;
                    }

                    if (message.from == FROM_KEYBIND)
                    {
                        if (Wearing.isWearingBackpack(player))
                        {
                            FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.BACKPACK_WEARING, world, playerX, playerY, playerZ);
                            return null;
                        }
                    }
                    if (message.from == FROM_HOLDING)
                    {
                        if (Wearing.isHoldingBackpack(player))
                        {
                            FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.BACKPACK_HOLDING, world, playerX, playerY, playerZ);
                            return null;
                        }
                    }
                    if (message.from == FROM_TILE)
                    {
                        if (player.openContainer instanceof ContainerBackpack)
                        {
                            TileAdventureBackpack te = (TileAdventureBackpack) ((ContainerBackpack) player.openContainer).inventory;
                            FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.BACKPACK_TILE, world, te.xCoord, te.yCoord, te.zCoord);
                            return null;
                        }
                    }
                }
            }

        }
        return null;
    }

    public static class GUImessage implements IMessage
    {

        private byte type;
        private byte from;

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
