package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.common.FlightCost;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Created by Darkona on 12/10/2014.
 */
public class PlayerActionPacket implements IMessageHandler<PlayerActionPacket.ActionMessage, IMessage>
{
    public static final byte spiderJump = 0;
    public static final byte JETPACK_IN_USE = 1;
    public static final byte JETPACK_NOT_IN_USE = 2;
    public static final byte COPTER_CLIMBING = 3;
    public static final byte COPTER_NOT_CLIMBING = 4;

    @Override
    public IMessage onMessage(ActionMessage message, MessageContext ctx)
    {
        if (ctx.side.isServer())
        {
            if(message.type == spiderJump){
                if(ctx.getServerHandler().playerEntity.ridingEntity != null && ctx.getServerHandler().playerEntity.ridingEntity instanceof EntityFriendlySpider)
                {
                    ((EntityFriendlySpider)ctx.getServerHandler().playerEntity.ridingEntity).setJumping(true);
                }
            }

            if(message.type == JETPACK_IN_USE || message.type == JETPACK_NOT_IN_USE)
            {
                if(Wearing.isWearingSteam(ctx.getServerHandler().playerEntity))
                {
                    InventorySteamJetpack inv = new InventorySteamJetpack(Wearing.getWearingSteam(ctx.getServerHandler().playerEntity));
                    // Thrust only with the jetpack switched on; each ignition costs a burst of steam
                    boolean start = message.type == JETPACK_IN_USE && inv.getStatus();
                    if (start && !inv.isInUse())
                    {
                        int ignition = FlightCost.ignitionSteam(inv.getSteamTank().getFluidAmount(), ConfigHandler.JETPACK_IGNITION_STEAM);
                        if (ignition > 0) inv.getSteamTank().drain(ignition, true);
                    }
                    inv.setInUse(start);
                    inv.markDirty();
                }
            }

            if (message.type == COPTER_CLIMBING || message.type == COPTER_NOT_CLIMBING)
            {
                // Read by ItemCopterPack to charge the climb; memory only, sent by the client on each change
                BackpackProperty props = BackpackProperty.get(ctx.getServerHandler().playerEntity);
                if (props != null) props.setCopterClimbing(message.type == COPTER_CLIMBING);
            }

        }
        return null;
    }

    public static class ActionMessage implements IMessage
    {

        private byte type;

        public ActionMessage()
        {
        }

        public ActionMessage(byte type)
        {
            this.type = type;

        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            this.type = buf.readByte();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(type);
        }
    }
}
