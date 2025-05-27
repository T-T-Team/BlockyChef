package tnt.blockychef.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.ApplianceEventConsumer;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.message.Client2ServerMessage;

@Network.Message(BlockyChef.MODID)
public class C2S_SendApplianceEvent extends Client2ServerMessage {

    private final BlockPos pos;
    private final int id;

    public <T extends BlockEntity & ApplianceEventConsumer> C2S_SendApplianceEvent(T obj, int eventId) {
        this.pos = obj.getBlockPos();
        this.id = eventId;
    }

    public C2S_SendApplianceEvent(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.id = buffer.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(pos);
        friendlyByteBuf.writeInt(id);
    }

    @Override
    public void handle(ServerPlayer serverPlayer, NetworkEvent.Context context) {
        ServerLevel level = serverPlayer.serverLevel();
        if (!level.isLoaded(pos)) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ApplianceEventConsumer consumer) {
            consumer.onEvent(serverPlayer, id);
        }
    }
}
