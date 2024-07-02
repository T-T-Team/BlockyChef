package tnt.tntlib.api.blockentity;

import net.minecraft.nbt.CompoundTag;

public interface Synchronizable {

    void encodeData(CompoundTag tag);

    void decodeData(CompoundTag tag);
}
