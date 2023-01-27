package tnt.blockychef.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import tnt.blockychef.common.block.entity.SynchronizableBlockEntity;
import tnt.blockychef.network.Packet;

public class S2C_SendBlockEntityData extends Packet {

    private final BlockPos pos;
    private final CompoundTag updateTag;

    private S2C_SendBlockEntityData(BlockPos pos, CompoundTag updateTag) {
        this.pos = pos;
        this.updateTag = updateTag;
    }

    public S2C_SendBlockEntityData(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.updateTag = buffer.readNbt();
    }

    public static <B extends BlockEntity & SynchronizableBlockEntity> S2C_SendBlockEntityData createUpdatePacket(B blockEntity) {
        CompoundTag tag = new CompoundTag();
        blockEntity.encodeBlockEntityData(tag);
        return new S2C_SendBlockEntityData(blockEntity.getBlockPos(), tag);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeNbt(updateTag);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(NetworkEvent.Context context) {
        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SynchronizableBlockEntity synchronizableBlock) {
            synchronizableBlock.decodeBlockEntityData(updateTag);
        }
    }
}
