package tnt.blockychef.common.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public interface SynchronizableBlockEntity {

    void encodeBlockEntityData(CompoundTag tag);

    void decodeBlockEntityData(CompoundTag tag);
}
