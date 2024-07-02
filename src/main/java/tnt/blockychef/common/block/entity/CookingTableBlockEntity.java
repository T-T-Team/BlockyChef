package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.client.screen.MultiVariantFurnitureBlock;
import tnt.blockychef.common.food.recipe.CookingTableRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.math.Interpolation;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.Optional;

public class CookingTableBlockEntity extends RecipeRememberingBlockEntity<CookingTableRecipe> implements Synchronizable, IndexedColorHolder, ProcessableRecipeHolder {

    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static final int[] OUTPUTS = {9, 10, 11, 12, 13, 14};

    private final Integer[] colors = new Integer[2];
    private boolean cooking;
    private int cookingTime;
    private RecipeHolder<CookingTableRecipe> recipeHolder;

    public CookingTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.COOKING_TABLE, pos, state);
        if (state.getBlock() instanceof MultiVariantFurnitureBlock variantBlock) {
            int[] baseColors = variantBlock.getVariant().getDefaultColors();
            ColorableBlockEntity.assignDefaultColors(baseColors, colors);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CookingTableBlockEntity table) {
        if (table.recipeHolder == null || !table.cooking)
            return;
        CookingTableRecipe recipe = table.recipeHolder.value();
        RecipeManager manager = level.getRecipeManager();
        if (manager.getRecipeFor(BlockyChefRecipeTypes.COOKING_TABLE_RECIPE, table, level, table.recipeHolder.id()).isEmpty()) {
            table.setRecipe(null);
            return;
        }
        if (!MenuInventoryHelper.isEmpty(table, OUTPUTS)) {
            table.setRecipe(null);
            return;
        }
        if (++table.cookingTime >= recipe.getAssemblyTime() && !level.isClientSide()) {
            table.cooking = false;
            table.cookingTime = 0;
            table.consumeIngredientsAndApplyCraftRemainder(recipe, INPUTS, OUTPUTS, in -> recipe.getInputs().forEach(ing -> ing.consume(table, in)));
            ItemStack[] outputs = recipe.getOutputs().stream().map(ItemStack::copy).toArray(ItemStack[]::new);
            MenuInventoryHelper.insertItems(outputs, table, OUTPUTS);
            table.storeRecipe(table.recipeHolder);
            table.refreshRecipe();
            BlockEntityHelper.sendBlockEntityClientData(table);
            table.setChanged();
        }
    }

    public float getAssemblyProgress(float partialTicks) {
        if (recipeHolder == null || !cooking)
            return 0.0F;
        int oldTick = Math.max(0, cookingTime - 1);
        int total = recipeHolder.value().getAssemblyTime();
        float f0 = oldTick / (float) total;
        float f1 = cookingTime / (float) total;
        return Interpolation.linear(f0, f1, partialTicks);
    }

    public boolean canCraft() {
        return recipeHolder != null && MenuInventoryHelper.isEmpty(this, OUTPUTS) && !cooking;
    }

    @Override
    public void startProcessing() {
        if (cooking)
            return;
        refreshRecipe();
        if (recipeHolder == null)
            return;
        cookingTime = 0;
        cooking = true;
        BlockEntityHelper.sendBlockEntityClientData(this);
        setChanged();
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
    }

    private void loadSharedData(CompoundTag tag) {
        ColorableBlockEntity.loadColorData(colors, tag);
        cooking = tag.getBoolean("cooking");
        cookingTime = tag.getInt("time");
        refreshRecipe();
    }

    public void refreshRecipe() {
        if (level == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        Optional<RecipeHolder<CookingTableRecipe>> optional = manager.getRecipeFor(BlockyChefRecipeTypes.COOKING_TABLE_RECIPE, this, level);
        setRecipe(optional.orElse(null));
    }

    private void setRecipe(@Nullable RecipeHolder<CookingTableRecipe> recipe) {
        if (recipeHolder != recipe) {
            recipeHolder = recipe;
            cooking = false;
            cookingTime = 0;
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
        setChanged();
    }
}
