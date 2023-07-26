package tnt.blockychef.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.ProcessableRecipeHolder;
import tnt.tntlib.api.network.Network;
import tnt.tntlib.api.network.message.Client2ServerMessage;

@Network.Message(BlockyChef.MODID)
public class C2S_InitiateRecipeProcessing extends Client2ServerMessage {

    private final BlockPos pos;

    public C2S_InitiateRecipeProcessing(BlockPos pos) {
        this.pos = pos;
    }

    public C2S_InitiateRecipeProcessing(FriendlyByteBuf byteBuf) {
        this(byteBuf.readBlockPos());
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void handle(ServerPlayer player, NetworkEvent.Context context) {
        ServerLevel level = player.serverLevel();
        if (!level.isLoaded(pos)) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ProcessableRecipeHolder holder) {
            holder.startProcessing();
        }
    }
}
