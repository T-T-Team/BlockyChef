package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tnt.blockychef.client.screen.MultiVariantFurnitureBlock;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.Arrays;

public abstract class ColorableBlockEntity extends InventoryBlockEntity implements Synchronizable, IndexedColorHolder {

    private final Integer[] colors;

    public ColorableBlockEntity(BlockEntityType<? extends ColorableBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.colors = new Integer[this.getColorLayerCount()];
        if (state.getBlock() instanceof MultiVariantFurnitureBlock variantBlock) {
            int[] defaultColors = variantBlock.getVariant().getDefaultColors();
            assignDefaultColors(defaultColors, colors);
        }
    }

    public abstract int getColorLayerCount();

    @Override
    public @Nullable Integer getColor(int index) {
        return checkIndex(index, this.colors) ? colors[index] : null;
    }

    @Override
    public void setColor(int index, @Nullable Integer color) {
        if (checkIndex(index, this.colors)) {
            colors[index] = color;
            this.setChanged();
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    @Override
    public void encodeData(CompoundTag tag) {
        MenuInventoryHelper.encodeInventory(this.inventoryHandler, tag);
        saveColorData(colors, tag);
    }

    @Override
    public void decodeData(CompoundTag tag) {
        MenuInventoryHelper.decodeInventory(this.inventoryHandler, tag);
        loadColorData(colors, tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveColorData(colors, tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadColorData(colors, tag);
    }

    public static boolean checkIndex(int index, Integer[] colorStorage) {
        return index >= 0 && index < colorStorage.length;
    }

    public static void assignDefaultColors(int[] defaultColorList, Integer[] dest) {
        for (int i = 0; i < Math.min(defaultColorList.length, dest.length); i++) {
            dest[i] = defaultColorList[i];
        }
    }

    public static void saveColorData(Integer[] colors, CompoundTag target) {
        ListTag tag = new ListTag();
        for (int i = 0; i < colors.length; i++) {
            Integer color = colors[i];
            if (color != null) {
                long data = (((long) i) << 32) | (color & 0xffffffffL);
                tag.add(LongTag.valueOf(data));
            }
        }
        target.put("colorList", tag);
    }

    public static void loadColorData(Integer[] colors, CompoundTag target) {
        Arrays.fill(colors, null);
        ListTag tag = target.getList("colorList", Tag.TAG_LONG);
        for (int i = 0; i < Math.min(colors.length, tag.size()); i++) {
            long data = ((LongTag) tag.get(i)).getAsLong();
            int index = (int) (data >> 32);
            int color = (int) data;
            if (checkIndex(index, colors)) {
                colors[index] = color;
            }
        }
    }
}
