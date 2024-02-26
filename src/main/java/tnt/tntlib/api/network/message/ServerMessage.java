package tnt.tntlib.api.network.message;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public interface ServerMessage extends EncodeableMessage {

    void handle(ServerPlayer sender, CustomPayloadEvent.Context context);
}
