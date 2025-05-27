package tnt.tntlib.api.network.message;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

public interface ClientMessage extends EncodeableMessage {

    @OnlyIn(Dist.CLIENT)
    void handle(Minecraft minecraft, NetworkEvent.Context context);
}
