package tnt.tntlib.api.network.message;

import net.minecraftforge.network.NetworkEvent;

public abstract class Client2ServerMessage extends AbstractHandledMessage implements ServerMessage {

    @Override
    public final void handleMessage(NetworkEvent.Context context) {
        handle(context.getSender(), context);
    }
}
