package mythicbotany.network;

import io.netty.buffer.ByteBuf;
import mythicbotany.MythicBotany;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Sends the server-side Alfheim portal progress to the client overlay. */
public final class PacketPortalEffect implements IMessage {
    private int portalTime;

    public PacketPortalEffect() {
    }

    public PacketPortalEffect(int portalTime) {
        this.portalTime = portalTime;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        portalTime = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(portalTime);
    }

    public static final class Handler implements IMessageHandler<PacketPortalEffect, IMessage> {
        @Override
        public IMessage onMessage(PacketPortalEffect message, MessageContext context) {
            MythicBotany.proxy.handlePortalEffect(message.portalTime);
            return null;
        }
    }
}
