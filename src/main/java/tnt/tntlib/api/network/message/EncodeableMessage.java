package tnt.tntlib.api.network.message;

import net.minecraft.network.FriendlyByteBuf;

public interface EncodeableMessage {

    void encode(FriendlyByteBuf buffer);
}
