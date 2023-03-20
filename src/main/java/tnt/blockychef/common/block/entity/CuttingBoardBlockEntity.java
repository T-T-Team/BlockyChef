package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.RecipeProcessingType;
import tnt.blockychef.common.food.recipe.CuttingBoardRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.Helper;

import javax.annotation.Nullable;
import java.util.Optional;

public class CuttingBoardBlockEntity extends RecipeRemberingBlockEntity<CuttingBoardRecipe> implements SynchronizableBlockEntity {

    public static final int SLOT_INPUT = 0;
    public static final int[] SLOT_OUTPUTS = { 1, 2, 3 };

    private CuttingBoardRecipe recipe;
    private RecipeProcessingType selectedType;
    private boolean processing;
    private int timeProcessing;

    public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.CUTTING_BOARD, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CuttingBoardBlockEntity cuttingBoard) {
        if (cuttingBoard.processing && cuttingBoard.recipe != null && cuttingBoard.selectedType != null) {
            CuttingBoardRecipe.CuttingBoardSubRecipe subRecipe = cuttingBoard.recipe.getSubRecipe(cuttingBoard.selectedType);
            ItemStack[] outputs = subRecipe.getOutputs();
            if (Helper.canFitItems(outputs, cuttingBoard, SLOT_OUTPUTS)) {
                if (++cuttingBoard.timeProcessing >= subRecipe.getTime()) {
                    cuttingBoard.completeRecipe(subRecipe);
                }
            } else {
                cuttingBoard.setProcessing(false);
                cuttingBoard.timeProcessing = 0;
                Helper.sendBlockEntityClientData(cuttingBoard);
            }
        }
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
        refreshRecipes();
    }

    public boolean isProcessing() {
        return processing;
    }

    public float getProcessingProgress(float partialTicks) {
        if (selectedType == null || recipe == null)
            return 0.0F;
        int prevTime = Math.max(0, timeProcessing - 1);
        int total = recipe.getSubRecipe(selectedType).getTime();
        float previousTickProgress = prevTime / (float) total;
        float currentTickProgress = timeProcessing / (float) total;
        return previousTickProgress + (currentTickProgress - previousTickProgress) * partialTicks;
    }

    public void setRecipeProcessingType(@Nullable RecipeProcessingType processingType) {
        this.selectedType = processingType;
    }

    public ItemStack getInputItem() {
        return this.getItem(SLOT_INPUT);
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(4);
    }

    @Override
    public void encodeBlockEntityData(CompoundTag tag) {

    }

    @Override
    public void decodeBlockEntityData(CompoundTag tag) {

    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        refreshRecipes();
    }

    private void refreshRecipes() {
        if (level == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        Optional<CuttingBoardRecipe> optional = manager.getRecipeFor(BlockyChefRecipeTypes.CUTTING_BOARD_RECIPE, this, level);
        optional.ifPresent(this::setRecipe);
    }

    private void setRecipe(@Nullable CuttingBoardRecipe recipe) {
        boolean changed = this.recipe != recipe;
        this.recipe = recipe;
        if (recipe == null) {
            setRecipeProcessingType(null);
        } else {
            setRecipeProcessingType(recipe.getFirstProcessingType());
        }
        if (changed) {
            Helper.sendBlockEntityClientData(this);
        }
    }

    private void completeRecipe(CuttingBoardRecipe.CuttingBoardSubRecipe subRecipe) {
        storeRecipe(recipe);
        ItemStack[] outputs = subRecipe.getOutputs();
        Helper.insertItems(outputs, this, SLOT_OUTPUTS);
        ItemStack stack = getInputItem();
        stack.shrink(1);
        setRecipe(null);
    }
}
