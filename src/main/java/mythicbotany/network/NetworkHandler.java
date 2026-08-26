package mythicbotany.network;

import mythicbotany.MythicBotany;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/** Small common network channel used by the client-only empty left-click event. */
public final class NetworkHandler {
    public static final SimpleNetworkWrapper CHANNEL =
            NetworkRegistry.INSTANCE.newSimpleChannel(MythicBotany.MODID);
    private static int nextMessageId;

    private NetworkHandler() {
    }

    public static void init() {
        CHANNEL.registerMessage(PacketLeftClick.Handler.class, PacketLeftClick.class,
                nextMessageId++, Side.SERVER);
        CHANNEL.registerMessage(PacketInfuserEffect.Handler.class, PacketInfuserEffect.class,
                nextMessageId++, Side.CLIENT);
        CHANNEL.registerMessage(PacketPortalEffect.Handler.class, PacketPortalEffect.class,
                nextMessageId++, Side.CLIENT);
    }

    public static void sendInfuserEffect(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos,
                                         int mana, int requirement, boolean complete) {
        if (world == null || world.isRemote) {
            return;
        }
        CHANNEL.sendToAllAround(new PacketInfuserEffect(pos, mana, requirement, complete),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(),
                        pos.getX(), pos.getY(), pos.getZ(), 64.0D));
    }

    public static void sendToServer(PacketLeftClick message) {
        CHANNEL.sendToServer(message);
    }

    public static void sendPortalEffect(net.minecraft.entity.player.EntityPlayerMP player,
                                        int portalTime) {
        CHANNEL.sendTo(new PacketPortalEffect(portalTime), player);
    }
}
