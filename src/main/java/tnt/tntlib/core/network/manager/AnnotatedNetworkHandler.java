package tnt.tntlib.core.network.manager;

import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;
import tnt.tntlib.api.SimpleVersion;
import tnt.tntlib.api.network.NetworkDispatcher;
import tnt.tntlib.api.network.message.ClientMessage;
import tnt.tntlib.api.network.message.EncodeableMessage;
import tnt.tntlib.api.network.message.HandledMessage;
import tnt.tntlib.api.network.message.ServerMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class AnnotatedNetworkHandler implements NetworkDispatcher {

    private final SimpleChannel channel;
    private int packetDiscriminator;

    AnnotatedNetworkHandler(String modId, SimpleVersion version, SimpleVersion.ComparationType type) {
        this.channel = NetworkRegistry.ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(modId, "network"))
                .networkProtocolVersion(version::toString)
                .clientAcceptedVersions(clientVersion -> SimpleVersion.parseString(clientVersion).matches(version, type))
                .serverAcceptedVersions(serverVersion -> SimpleVersion.parseString(serverVersion).matches(version, type))
                .simpleChannel();
    }

    @Override
    public void sendToClient(ServerPlayer player, ClientMessage message) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    @Override
    public void sendToLevel(Level level, ClientMessage message) {
        channel.send(PacketDistributor.DIMENSION.with(level::dimension), message);
    }

    @Override
    public void sendToNearby(PacketDistributor.TargetPoint point, ClientMessage message) {
        channel.send(PacketDistributor.NEAR.with(() -> point), message);
    }

    @Override
    public void sendToAll(ClientMessage message) {
        channel.send(PacketDistributor.ALL.noArg(), message);
    }

    @Override
    public void sendToAllTracking(Entity trackedEntity, ClientMessage message) {
        channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> trackedEntity), message);
    }

    @Override
    public void sendToClientAndAllTracking(Entity trackedEntity, ClientMessage message) {
        channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> trackedEntity), message);
    }

    @Override
    public void sendToChunk(LevelChunk chunk, ClientMessage message) {
        channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
    }

    @Override
    public void sendToAllConnections(List<Connection> connections, ClientMessage message) {
        channel.send(PacketDistributor.NMLIST.with(() -> connections), message);
    }

    @Override
    public void sendToServer(ServerMessage message) {
        channel.send(PacketDistributor.SERVER.noArg(), message);
    }

    <T extends EncodeableMessage & HandledMessage> void registerMessage(Class<T> type, NetworkDirection direction) {
        channel.messageBuilder(type, this.packetDiscriminator++, direction)
                .encoder(EncodeableMessage::encode)
                .decoder(data -> decode(type, data))
                .consumerNetworkThread(HandledMessage::handle)
                .add();
    }

    <T> T decode(Class<T> type, FriendlyByteBuf buffer) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor(FriendlyByteBuf.class);
            return constructor.newInstance(buffer);
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new IllegalArgumentException("Every message must contain constructor with " + FriendlyByteBuf.class.getSimpleName() + " type");
        }
    }
}
