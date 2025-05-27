package tnt.tntlib.api.network.message;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface HandledMessage {

    void handle(Supplier<NetworkEvent.Context> context);
}
