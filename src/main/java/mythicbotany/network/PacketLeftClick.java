package mythicbotany.network;

import io.netty.buffer.ByteBuf;
import mythicbotany.item.ItemAlfsteelSword;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Server-side relay for the 1.12.2 LeftClickEmpty event. */
public class PacketLeftClick implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketLeftClick, IMessage> {
        @Override
        public IMessage onMessage(PacketLeftClick message, MessageContext context) {
            final EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack stack = player.getHeldItemMainhand();
                if (!stack.isEmpty() && stack.getItem() instanceof ItemAlfsteelSword) {
                    ((ItemAlfsteelSword) stack.getItem()).trySpawnBurst(player, stack);
                }
            });
            return null;
        }
    }
}

