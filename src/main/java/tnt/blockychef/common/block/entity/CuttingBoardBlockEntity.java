package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.recipe.CuttingBoardRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefSounds;
import tnt.blockychef.util.Helper;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.math.Interpolation;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CuttingBoardBlockEntity extends RecipeRememberingBlockEntity<CuttingBoardRecipe> implements SelectableRecipeHolder {

    public static final int SLOT_INPUT = 0;
    public static final int[] SLOT_OUTPUTS = {1, 2, 3};

    private List<CuttingBoardRecipe> availableRecipes = Collections.emptyList();
    private CuttingBoardRecipe recipe;
    private boolean processing;
    private int timeProcessing;

    public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.CUTTING_BOARD, pos, state);
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, CuttingBoardBlockEntity cuttingBoard) {
        if (cuttingBoard.processing) {
            if (cuttingBoard.recipe != null) {
                CuttingBoardRecipe cuttingBoardRecipe = cuttingBoard.recipe;
                ItemStack[] outputs = cuttingBoardRecipe.getOutputs();
                if (canPlaySound(60, cuttingBoard.timeProcessing, cuttingBoardRecipe.getProcessingTime())) {
                    level.playSound(null, pos, BlockyChefSounds.CUTTING_BOARD, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                if (MenuInventoryHelper.canFitItems(outputs, cuttingBoard, SLOT_OUTPUTS)) {
                    if (++cuttingBoard.timeProcessing >= cuttingBoardRecipe.getProcessingTime()) {
                        cuttingBoard.completeRecipe();
                    }
                } else {
                    cuttingBoard.setProcessing(false);
                    cuttingBoard.timeProcessing = 0;
                    BlockEntityHelper.sendBlockEntityClientData(cuttingBoard);
                }
            } else {
                cuttingBoard.setProcessing(false);
                cuttingBoard.timeProcessing = 0;
                BlockEntityHelper.sendBlockEntityClientData(cuttingBoard);
            }
        }
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, CuttingBoardBlockEntity cuttingBoard) {
        if (cuttingBoard.processing && cuttingBoard.recipe != null) {
            int max = cuttingBoard.recipe.getProcessingTime();
            if (cuttingBoard.timeProcessing < max) {
                cuttingBoard.timeProcessing++;
            }
        } else {
            cuttingBoard.timeProcessing = 0;
        }
    }

    public void onInputChanged() {
        refreshAvailableRecipes();
        setChanged();
    }

    @Override
    public void setProcessing(boolean processing) {
        this.processing = processing;
        refreshAvailableRecipes();
    }

    public boolean isProcessing() {
        return processing;
    }

    public int getRecipeIndex() {
        return recipe == null ? -1 : availableRecipes.indexOf(recipe);
    }

    public float getProcessingProgress(float partialTicks) {
        if (recipe == null)
            return 0.0F;
        int prevTime = Math.max(0, timeProcessing - 1);
        int total = recipe.getProcessingTime();
        float previousTickProgress = prevTime / (float) total;
        float currentTickProgress = timeProcessing / (float) total;
        return Interpolation.linear(previousTickProgress, currentTickProgress, partialTicks);
    }

    public ItemStack getInputItem() {
        return this.getItem(SLOT_INPUT);
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(4);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        refreshAvailableRecipes();
    }

    @Override
    public void encodeData(CompoundTag tag) {
        MenuInventoryHelper.encodeInventory(getItemHandler(), tag);
        saveCommonData(tag);
    }

    @Override
    public void decodeData(CompoundTag tag) {
        MenuInventoryHelper.decodeInventory(getItemHandler(), tag);
        loadCommonData(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveCommonData(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadCommonData(tag);
    }

    @Nullable
    public CuttingBoardRecipe getRecipe() {
        return recipe;
    }

    public boolean hasMultipleRecipes() {
        return availableRecipes.size() > 1;
    }

    public int getAvailableRecipeCount() {
        return availableRecipes.size();
    }

    @Override
    public void changeRecipe(int direction) {
        int index = getRecipeIndex();
        int next = index + direction;
        if (next >= 0 && next < getAvailableRecipeCount()) {
            CuttingBoardRecipe recipe = availableRecipes.get(next);
            setRecipe(recipe);
        }
    }

    private void saveCommonData(CompoundTag tag) {
        if (recipe != null) {
            tag.putString("recipe", recipe.getId().toString());
        }
        tag.putBoolean("processing", processing);
        tag.putInt("processingTime", timeProcessing);
    }

    private void loadCommonData(CompoundTag tag) {
        refreshAvailableRecipes();
        if (tag.contains("recipe")) {
            ResourceLocation location = ResourceLocation.parse(tag.getString("recipe"));
            recipe = Helper.find(availableRecipes, recipe -> recipe.getId().equals(location))
                    .orElse(null);
        } else {
            recipe = null;
        }
        processing = recipe != null && tag.getBoolean("processing");
        timeProcessing = recipe != null ? tag.getInt("processingTime") : 0;
    }

    private void refreshAvailableRecipes() {
        if (level == null)
            return;
        availableRecipes = Helper.getAllValidRecipes(level, BlockyChefRecipeTypes.CUTTING_BOARD_RECIPE, this);
        if (availableRecipes.size() > 0 && (recipe == null || !availableRecipes.contains(recipe))) {
            recipe = availableRecipes.get(0);
        }
        if (recipe != null && !availableRecipes.contains(recipe)) {
            setRecipe(null);
        }
        BlockEntityHelper.sendBlockEntityClientData(this);
    }

    private void setRecipe(@Nullable CuttingBoardRecipe recipe) {
        boolean changed = this.recipe != recipe;
        this.recipe = recipe;
        if (changed) {
            timeProcessing = 0;
            processing = false;
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    private void completeRecipe() {
        timeProcessing = 0;
        storeRecipe(recipe);
        ItemStack[] outputs = recipe.getOutputs();
        consumeIngredientsAndApplyCraftRemainder(recipe, new int[] {SLOT_INPUT}, SLOT_OUTPUTS, in -> {
            ItemStack consumed = getInputItem().copy();
            consumed.setCount(1);
            getInputItem().shrink(1);
            return Collections.singletonList(consumed);
        });
        MenuInventoryHelper.insertItems(outputs, this, SLOT_OUTPUTS);
        refreshAvailableRecipes();
    }
}
