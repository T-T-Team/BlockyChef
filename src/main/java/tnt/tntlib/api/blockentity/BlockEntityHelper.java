package tnt.tntlib.api.blockentity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tnt.tntlib.core.network.TNTNetworkManager;
import tnt.tntlib.core.network.message.S2C_SendBlockEntityData;

import java.util.Objects;

public final class BlockEntityHelper {

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createBlockEntityTicker(BlockEntityType<A> typeToTest, BlockEntityType<E> wantedType, BlockEntityTicker<? super E> ticker) {
        return typeToTest == wantedType ? (BlockEntityTicker<A>) ticker : null;
    }

    public static <B extends BlockEntity & Synchronizable> void sendBlockEntityClientData(B blockEntity) {
        Level level = Objects.requireNonNull(blockEntity, "blockEntity cannot be null").getLevel();
        if (level == null || level.isClientSide)
            return;
        TNTNetworkManager.NETWORK.sendToLevel(level, S2C_SendBlockEntityData.create(blockEntity));
    }

    private BlockEntityHelper() {}
}
