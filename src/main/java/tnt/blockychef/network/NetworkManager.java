package tnt.blockychef.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tnt.blockychef.BlockyChef;

import java.util.function.Function;

public final class NetworkManager {

    private static final String PROTOCOL_VERSION = "blockychef-v1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(BlockyChef.resource("channel"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    public static void dispatchServerPacket(Packet packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void dispatchClientPacket(ServerPlayer serverPlayerRef, Packet packet) {
        CHANNEL.sendTo(packet, serverPlayerRef.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static final class Registry {

        private static byte id;

        public static void register() {

        }

        private static <T extends Packet> void register(Class<T> aClass, Function<FriendlyByteBuf, T> decoder) {
            CHANNEL.registerMessage(id++, aClass, Packet::encode, decoder, Packet::handle);
        }
    }
}
