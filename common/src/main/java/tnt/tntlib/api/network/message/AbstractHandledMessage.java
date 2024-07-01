package tnt.tntlib.api.network.message;

import net.minecraftforge.event.network.CustomPayloadEvent;

public abstract class AbstractHandledMessage implements EncodeableMessage, HandledMessage {

    public abstract void handleMessage(CustomPayloadEvent.Context context);

    @Override
    public final void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> this.handleMessage(context));
        context.setPacketHandled(true);
    }
}
