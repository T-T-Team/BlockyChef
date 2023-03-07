package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.util.Helper;

import java.util.Arrays;

public class KitchenCounterCornerBlockEntity extends BlockEntity implements SynchronizableBlockEntity, IndexedColorHolder {

    private int[] colors;

    public KitchenCounterCornerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.KITCHEN_COUNTER_CORNER, pos, state);
        this.colors = new int[2];
        Arrays.fill(this.colors, Integer.MIN_VALUE);
    }

    @Override
    public int getColor(int index) {
        return index >= 0 && index < colors.length ? colors[index] : Integer.MIN_VALUE;
    }

    @Override
    public void setColor(int index, int color) {
        if (index >= 0 && index < colors.length) {
            colors[index] = color;
            this.setChanged();
            Helper.sendBlockEntityClientData(this);
        }
    }

    @Override
    public void encodeBlockEntityData(CompoundTag tag) {
        tag.putIntArray("colors", colors);
    }

    @Override
    public void decodeBlockEntityData(CompoundTag tag) {
        colors = tag.getIntArray("colors");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        encodeBlockEntityData(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        decodeBlockEntityData(tag);
    }
}
