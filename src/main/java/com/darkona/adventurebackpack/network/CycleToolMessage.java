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
public class CycleToolMessage implements IMessage
{

    private int directionOfCycle;
    private int slot;
    private byte typeOfAction;

    public CycleToolMessage()
    {
    }

    public CycleToolMessage(int directionOfCycle, int slot, byte typeOfAction)
    {
        this.typeOfAction = typeOfAction;
        this.directionOfCycle = directionOfCycle;
        this.slot = slot;

    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.typeOfAction = buf.readByte();
        this.directionOfCycle = buf.readInt();
        this.slot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(typeOfAction);
        buf.writeInt(directionOfCycle);
        buf.writeInt(slot);
    }

    public static class CycleToolMessageServerHandler implements IMessageHandler<CycleToolMessage, IMessage>
    {

        @Override
        public IMessage onMessage(CycleToolMessage message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            switch (message.typeOfAction)
            {
                case MessageConstants.CYCLE_TOOL_ACTION:
                    Actions.cycleTool(player, message.directionOfCycle, message.slot);
                    break;
                case MessageConstants.TOGGLE_HOSE_TANK:
                    Actions.switchHose(player, Actions.HOSE_TOGGLE, message.directionOfCycle, message.slot);
                    break;
                case MessageConstants.SWITCH_HOSE_ACTION:
                    Actions.switchHose(player, Actions.HOSE_SWITCH, message.directionOfCycle, message.slot);
                    break;
            }
            return null;
        }
    }

    public static class CycleToolMessageClientHandler implements IMessageHandler<CycleToolMessage, IMessage>
    {

        @Override
        public IMessage onMessage(CycleToolMessage message, MessageContext ctx)
        {
            return null;
        }
    }
}
