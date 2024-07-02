package tnt.tntlib.api.network;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;
import tnt.tntlib.api.network.message.ClientMessage;
import tnt.tntlib.api.network.message.ServerMessage;

import java.util.List;

public interface NetworkDispatcher {

    void sendToServer(ServerMessage message);

    void sendToClient(ServerPlayer player, ClientMessage message);

    void sendToAll(ClientMessage message);

    void sendToLevel(Level level, ClientMessage message);

    void sendToNearby(PacketDistributor.TargetPoint point, ClientMessage message);

    void sendToAllTracking(Entity trackedEntity, ClientMessage message);

    void sendToClientAndAllTracking(Entity trackedEntity, ClientMessage message);

    void sendToChunk(LevelChunk chunk, ClientMessage message);

    void sendToAllConnections(List<Connection> connections, ClientMessage message);
}
