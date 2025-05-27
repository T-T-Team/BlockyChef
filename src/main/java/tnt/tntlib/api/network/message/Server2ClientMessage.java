package tnt.tntlib.api.network.message;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

public abstract class Server2ClientMessage extends AbstractHandledMessage implements ClientMessage {

    @OnlyIn(Dist.CLIENT)
    @Override
    public final void handleMessage(NetworkEvent.Context context) {
        handle(Minecraft.getInstance(), context);
    }
}
