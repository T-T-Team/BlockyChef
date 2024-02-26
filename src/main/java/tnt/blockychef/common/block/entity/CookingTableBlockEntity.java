package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.client.screen.MultiVariantFurnitureBlock;
import tnt.blockychef.common.food.recipe.CookingTableRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;

public class CookingTableBlockEntity extends RecipeRememberingBlockEntity<CookingTableRecipe> implements Synchronizable, IndexedColorHolder {

    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static final int[] OUTPUTS = {9, 10, 11, 12, 13, 14};

    private final Integer[] colors = new Integer[2];
    private boolean cooking;
    private int cookingTime;
    private int totalCookTime;
    private CookingTableRecipe recipe;

    public CookingTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.COOKING_TABLE, pos, state);
        if (state.getBlock() instanceof MultiVariantFurnitureBlock variantBlock) {
            int[] baseColors = variantBlock.getVariant().getDefaultColors();
            ColorableBlockEntity.assignDefaultColors(baseColors, colors);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CookingTableBlockEntity table) {

    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(INPUTS.length + OUTPUTS.length);
    }

    @Override
    public void encodeData(CompoundTag compoundTag) {
        MenuInventoryHelper.encodeInventory(inventoryHandler, compoundTag);
        saveSharedData(compoundTag);
    }

    @Override
    public void decodeData(CompoundTag compoundTag) {
        MenuInventoryHelper.decodeInventory(inventoryHandler, compoundTag);
        loadSharedData(compoundTag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveSharedData(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadSharedData(tag);
    }

    @Override
    public @Nullable Integer getColor(int index) {
        return ColorableBlockEntity.checkIndex(index, colors) ? colors[index] : null;
    }

    @Override
    public void setColor(int index, @Nullable Integer color) {
        if (ColorableBlockEntity.checkIndex(index, colors)) {
            colors[index] = color;
            setChanged();
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    private void saveSharedData(CompoundTag tag) {
        ColorableBlockEntity.saveColorData(colors, tag);
        tag.putBoolean("cooking", cooking);
        tag.putInt("time", cookingTime);
        tag.putInt("totalTime", totalCookTime);
    }

    private void loadSharedData(CompoundTag tag) {
        ColorableBlockEntity.loadColorData(colors, tag);
        cooking = tag.getBoolean("cooking");
        cookingTime = tag.getInt("time");
        totalCookTime = tag.getInt("totalTime");
    }
}
