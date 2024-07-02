package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tnt.blockychef.client.screen.MultiVariantFurnitureBlock;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;

import javax.annotation.Nullable;

public class KitchenCounterCornerBlockEntity extends BlockEntity implements Synchronizable, IndexedColorHolder {

    private final Integer[] colors;

    public KitchenCounterCornerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.KITCHEN_COUNTER_CORNER, pos, state);
        this.colors = new Integer[2];
        if (state.getBlock() instanceof MultiVariantFurnitureBlock variantBlock) {
            int[] defaultColors = variantBlock.getVariant().getDefaultColors();
            ColorableBlockEntity.assignDefaultColors(defaultColors, colors);
        }
    }

    @Override
    public @Nullable Integer getColor(int index) {
        return index >= 0 && index < colors.length ? colors[index] : null;
    }

    @Override
    public void setColor(int index, @Nullable Integer color) {
        if (index >= 0 && index < colors.length) {
            colors[index] = color;
            this.setChanged();
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    @Override
    public void encodeData(CompoundTag tag) {
        ColorableBlockEntity.saveColorData(colors, tag);
    }

    @Override
    public void decodeData(CompoundTag tag) {
        ColorableBlockEntity.loadColorData(colors, tag);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        encodeData(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        decodeData(tag);
    }
}
