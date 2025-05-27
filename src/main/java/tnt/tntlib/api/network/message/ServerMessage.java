package tnt.tntlib.api.network.message;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public interface ServerMessage extends EncodeableMessage {

    void handle(ServerPlayer sender, NetworkEvent.Context context);
}
