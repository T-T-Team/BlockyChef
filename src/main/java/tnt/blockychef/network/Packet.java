package tnt.blockychef.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class Packet {

    public abstract void encode(FriendlyByteBuf buffer);

    public abstract void handle(NetworkEvent.Context context);

    public final void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> this.handle(context));
        context.setPacketHandled(true);
    }
}
