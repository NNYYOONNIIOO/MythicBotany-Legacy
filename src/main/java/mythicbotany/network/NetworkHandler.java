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
    }

    public static void sendToServer(PacketLeftClick message) {
        CHANNEL.sendToServer(message);
    }
}

