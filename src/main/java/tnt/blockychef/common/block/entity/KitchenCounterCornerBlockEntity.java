package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;

import java.util.Arrays;

public class KitchenCounterCornerBlockEntity extends BlockEntity implements Synchronizable, IndexedColorHolder {

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
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    @Override
    public void encodeData(CompoundTag tag) {
        tag.putIntArray("colors", colors);
    }

    @Override
    public void decodeData(CompoundTag tag) {
        colors = tag.getIntArray("colors");
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
