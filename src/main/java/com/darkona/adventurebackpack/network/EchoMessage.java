package com.darkona.adventurebackpack.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;


public class EchoMessage implements IMessage {

    public String salutation;

    public EchoMessage() {
    }

    ;

    public EchoMessage(String salutation) {
        this.salutation = salutation;
    }

    ;


    @Override
    public void fromBytes(ByteBuf buf) {
        this.salutation = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.salutation);
    }
}
