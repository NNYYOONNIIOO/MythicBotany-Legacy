package mythicbotany.network;

import io.netty.buffer.ByteBuf;
import mythicbotany.MythicBotany;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Client-side Terra Plate-style particle update for the mana infuser. */
public class PacketInfuserEffect implements IMessage {
    private int x;
    private int y;
    private int z;
    private int mana;
    private int requirement;
    private boolean complete;

    public PacketInfuserEffect() {
    }

    public PacketInfuserEffect(BlockPos pos, int mana, int requirement, boolean complete) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.mana = mana;
        this.requirement = requirement;
        this.complete = complete;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        mana = buf.readInt();
        requirement = buf.readInt();
        complete = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(mana);
        buf.writeInt(requirement);
        buf.writeBoolean(complete);
    }

    public static class Handler implements IMessageHandler<PacketInfuserEffect, IMessage> {
        @Override
        public IMessage onMessage(final PacketInfuserEffect message, MessageContext context) {
            MythicBotany.proxy.handleInfuserEffect(message.x, message.y, message.z, message.mana,
                    message.requirement, message.complete);
            return null;
        }
    }
}
