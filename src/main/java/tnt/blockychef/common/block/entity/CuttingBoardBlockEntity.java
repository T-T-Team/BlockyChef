package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.recipe.CuttingBoardRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.Helper;
import tnt.blockychef.util.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CuttingBoardBlockEntity extends RecipeRemberingBlockEntity<CuttingBoardRecipe> implements SynchronizableBlockEntity {

    public static final int SLOT_INPUT = 0;
    public static final int[] SLOT_OUTPUTS = {1, 2, 3};

    private Set<CuttingBoardRecipe> availableRecipes = Collections.emptySet();
    private CuttingBoardRecipe recipe;
    private boolean processing;
    private int timeProcessing;

    public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.CUTTING_BOARD, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CuttingBoardBlockEntity cuttingBoard) {
        if (cuttingBoard.processing && cuttingBoard.recipe != null) {
            ItemStack[] outputs = cuttingBoard.recipe.getOutputs();
            if (MenuInventoryHelper.canFitItems(outputs, cuttingBoard, SLOT_OUTPUTS)) {
                if (++cuttingBoard.timeProcessing >= cuttingBoard.recipe.getProcessingTime()) {
                    cuttingBoard.completeRecipe();
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
        refreshAvailableRecipes();
    }

    public boolean isProcessing() {
        return processing;
    }

    public float getProcessingProgress(float partialTicks) {
        if (recipe == null)
            return 0.0F;
        int prevTime = Math.max(0, timeProcessing - 1);
        int total = recipe.getProcessingTime();
        float previousTickProgress = prevTime / (float) total;
        float currentTickProgress = timeProcessing / (float) total;
        return previousTickProgress + (currentTickProgress - previousTickProgress) * partialTicks;
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
        refreshAvailableRecipes();
        if (recipe != null) {
            tag.putString("recipe", recipe.getId().toString());
        }
        tag.putBoolean("processing", processing);
        tag.putInt("processingTime", timeProcessing);
    }

    @Override
    public void decodeBlockEntityData(CompoundTag tag) {
        refreshAvailableRecipes();
        if (tag.contains("recipe")) {
            ResourceLocation location = new ResourceLocation(tag.getString("recipe"));
            recipe = Helper.find(availableRecipes, recipe -> recipe.getId().equals(location))
                        .orElse(null);
        }
        processing = tag.getBoolean("processing");
        timeProcessing = tag.getInt("processingTime");
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

    private void refreshAvailableRecipes() {
        if (level == null)
            return;
        availableRecipes = new HashSet<>(Helper.getAllValidRecipes(level, BlockyChefRecipeTypes.CUTTING_BOARD_RECIPE, this));
    }

    private void setRecipe(@Nullable CuttingBoardRecipe recipe) {
        boolean changed = this.recipe != recipe;
        this.recipe = recipe;
        if (changed) {
            Helper.sendBlockEntityClientData(this);
        }
    }

    private void completeRecipe() {
        storeRecipe(recipe);
        ItemStack[] outputs = recipe.getOutputs();
        MenuInventoryHelper.insertItems(outputs, this, SLOT_OUTPUTS);
        ItemStack stack = getInputItem();
        stack.shrink(1);
        setRecipe(null);
    }
}
