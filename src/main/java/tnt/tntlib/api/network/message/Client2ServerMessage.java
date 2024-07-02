package tnt.tntlib.api.network.message;

import net.minecraftforge.event.network.CustomPayloadEvent;

public abstract class Client2ServerMessage extends AbstractHandledMessage implements ServerMessage {

    @Override
    public final void handleMessage(CustomPayloadEvent.Context context) {
        handle(context.getSender(), context);
    }
}
