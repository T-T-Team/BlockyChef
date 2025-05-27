package tnt.tntlib.api.network.message;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class AbstractHandledMessage implements EncodeableMessage, HandledMessage {

    public abstract void handleMessage(NetworkEvent.Context context);

    @Override
    public final void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> this.handleMessage(ctx));
        ctx.setPacketHandled(true);
    }
}
