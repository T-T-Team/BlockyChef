package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import java.util.Arrays;

public abstract class ColorableBlockEntity extends InventoryBlockEntity implements Synchronizable, IndexedColorHolder {

    private int[] colors;

    public ColorableBlockEntity(BlockEntityType<? extends ColorableBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.colors = new int[this.getColorLayerCount()];
        Arrays.fill(this.colors, Integer.MIN_VALUE);
    }

    public abstract int getColorLayerCount();

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
        MenuInventoryHelper.encodeInventory(this.inventoryHandler, tag);
        tag.putIntArray("colors", this.colors);
    }

    @Override
    public void decodeData(CompoundTag tag) {
        MenuInventoryHelper.decodeInventory(this.inventoryHandler, tag);
        this.colors = tag.getIntArray("colors");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putIntArray("colors", colors);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        colors = tag.getIntArray("colors");
    }
}
