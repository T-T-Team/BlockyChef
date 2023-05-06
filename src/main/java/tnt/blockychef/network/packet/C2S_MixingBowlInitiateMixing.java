package tnt.blockychef.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import tnt.blockychef.common.block.entity.MixingBowlBlockEntity;
import tnt.blockychef.network.Packet;

public class C2S_MixingBowlInitiateMixing extends Packet {

    private final BlockPos pos;

    public C2S_MixingBowlInitiateMixing(BlockPos pos) {
        this.pos = pos;
    }

    public C2S_MixingBowlInitiateMixing(FriendlyByteBuf byteBuf) {
        this(byteBuf.readBlockPos());
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        ServerLevel level = player.getLevel();
        if (!level.isLoaded(pos)) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MixingBowlBlockEntity mixingBowl) {
            mixingBowl.startMixing();
        }
    }
}
