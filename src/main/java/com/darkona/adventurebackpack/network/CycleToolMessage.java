package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.common.Actions;
import com.darkona.adventurebackpack.util.LogHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created by Darkona on 12/10/2014.
 *
 * @
 */
public class CycleToolMessage implements IMessage {

    private int directionOfCycle;
    private int slot;
    private boolean typeOfAction;

    public CycleToolMessage() {
    }

    public CycleToolMessage(int directionOfCycle, int slot, boolean typeOfAction) {
        this.typeOfAction = typeOfAction;
        this.directionOfCycle = directionOfCycle;
        this.slot = slot;

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.typeOfAction = buf.readBoolean();
        this.directionOfCycle = buf.readInt();
        this.slot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(typeOfAction);
        buf.writeInt(directionOfCycle);
        buf.writeInt(slot);
    }

    public static class CycleToolMessageServerHandler implements IMessageHandler<CycleToolMessage, IMessage> {

        @Override
        public IMessage onMessage(CycleToolMessage message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (message.typeOfAction) {
                LogHelper.debug("CycleToolMessage received - Cycle tool");
                Actions.cycleTool(player, message.directionOfCycle, message.slot);
            } else {
                LogHelper.debug("CycleToolMessage received - Switch Hose Mode");
                Actions.switchHose(ctx.getServerHandler().playerEntity, message.directionOfCycle, message.slot);
            }
            return null;
        }
    }

    public static class CycleToolMessageClientHandler implements IMessageHandler<CycleToolMessage, IMessage> {

        @Override
        public IMessage onMessage(CycleToolMessage message, MessageContext ctx) {
            return null;
        }
    }
}
