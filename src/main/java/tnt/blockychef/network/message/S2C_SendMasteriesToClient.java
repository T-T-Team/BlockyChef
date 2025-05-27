package tnt.blockychef.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.message.Server2ClientMessage;

import java.util.ArrayList;
import java.util.List;

@Network.Message(BlockyChef.MODID)
public final class S2C_SendMasteriesToClient extends Server2ClientMessage {

    private final List<CookingMastery> list;

    public S2C_SendMasteriesToClient(List<CookingMastery> list) {
        this.list = list;
    }

    public S2C_SendMasteriesToClient(FriendlyByteBuf buf) {
        int size = buf.readInt();
        this.list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(buf.readJsonWithCodec(CookingMastery.CODEC));
        }
    }

    @Override
    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(list.size());
        list.forEach(mastery -> friendlyByteBuf.writeJsonWithCodec(CookingMastery.CODEC, mastery));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(Minecraft minecraft, NetworkEvent.Context context) {
        BlockyChef.MASTERY_MANAGER.loadFromNetwork(list);
    }
}
