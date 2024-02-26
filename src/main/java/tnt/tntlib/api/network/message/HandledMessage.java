package tnt.tntlib.api.network.message;

import net.minecraftforge.event.network.CustomPayloadEvent;

public interface HandledMessage {

    void handle(CustomPayloadEvent.Context context);
}
