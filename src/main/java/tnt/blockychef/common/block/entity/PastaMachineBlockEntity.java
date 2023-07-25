package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.recipe.PastaMachineRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.Helper;
import tnt.blockychef.util.MenuInventoryHelper;
import tnt.blockychef.util.RenderHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PastaMachineBlockEntity extends RecipeRememberingBlockEntity<PastaMachineRecipe> implements SelectableRecipeHolder, IndexedColorHolder {

    public static final int[] INPUTS = {0};
    public static final int[] OUTPUTS = {1, 2, 3};

    private List<PastaMachineRecipe> availableRecipes = Collections.emptyList();
    private PastaMachineRecipe recipe;
    private boolean processing;
    private int processingTime;
    private int[] colors;

    public PastaMachineBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.PASTA_MACHINE, pos, state);
        this.colors = new int[1];
        Arrays.fill(this.colors, Integer.MIN_VALUE);
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, PastaMachineBlockEntity pastaMachine) {
        if (pastaMachine.recipe != null && pastaMachine.processing) {
            ItemStack[] outputs = pastaMachine.recipe.getOutputs();
            if (MenuInventoryHelper.canFitItems(outputs, pastaMachine, OUTPUTS)) {
                if (++pastaMachine.processingTime >= pastaMachine.recipe.getProcessingTime()) {
                    pastaMachine.processingTime = 0;
                    pastaMachine.storeRecipe(pastaMachine.recipe);
                    pastaMachine.consumeIngredientsAndApplyCraftRemainder(INPUTS, OUTPUTS, in -> pastaMachine.getInputItem().shrink(1));
                    MenuInventoryHelper.insertItems(pastaMachine.recipe.getOutputs(), pastaMachine, OUTPUTS);
                    pastaMachine.refreshRecipes();
                }
            } else {
                pastaMachine.setProcessing(false);
                pastaMachine.processingTime = 0;
                Helper.sendBlockEntityClientData(pastaMachine);
            }
        }
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, PastaMachineBlockEntity pastaMachine) {
        if (pastaMachine.processing && pastaMachine.recipe != null) {
            int max = pastaMachine.recipe.getProcessingTime();
            if (pastaMachine.processingTime < max) {
                pastaMachine.processingTime++;
            }
        } else {
            pastaMachine.processingTime = 0;
        }
    }

    public ItemStack getInputItem() {
        return inventoryHandler.getStackInSlot(0);
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(INPUTS.length + OUTPUTS.length);
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
        MenuInventoryHelper.encodeInventory(inventoryHandler, tag);
        saveSharedData(tag);
    }

    @Override
    public void decodeBlockEntityData(CompoundTag tag) {
        MenuInventoryHelper.decodeInventory(inventoryHandler, tag);
        loadSharedData(tag);
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

    public void onInputChanged() {
        refreshRecipes();
        setChanged();
    }

    @Override
    public void setProcessing(boolean processing) {
        this.processing = processing;
        refreshRecipes();
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
        int prevTime = Math.max(0, processingTime - 1);
        int total = recipe.getProcessingTime();
        float previousTickProgress = prevTime / (float) total;
        float currentTickProgress = processingTime / (float) total;
        return RenderHelper.interpolate(previousTickProgress, currentTickProgress, partialTicks);
    }

    @Nullable
    public PastaMachineRecipe getRecipe() {
        return recipe;
    }

    public int getAvailableRecipeCount() {
        return availableRecipes.size();
    }

    @Override
    public void changeRecipe(int direction) {
        int index = getRecipeIndex();
        int next = index + direction;
        if (next >= 0 && next < getAvailableRecipeCount()) {
            PastaMachineRecipe recipe = availableRecipes.get(next);
            setRecipe(recipe);
        }
    }

    private void refreshRecipes() {
        if (level == null)
            return;
        availableRecipes = Helper.getAllValidRecipes(level, BlockyChefRecipeTypes.PASTA_MACHINE_RECIPE, this);
        if (availableRecipes.size() > 0 && (recipe == null || !availableRecipes.contains(recipe))) {
            recipe = availableRecipes.get(0);
        }
        if (recipe != null && !availableRecipes.contains(recipe)) {
            setRecipe(null);
        }
        Helper.sendBlockEntityClientData(this);
    }

    private void setRecipe(@Nullable PastaMachineRecipe recipe) {
        if (recipe != this.recipe) {
            this.recipe = recipe;
            this.processing = false;
            this.processingTime = 0;
            Helper.sendBlockEntityClientData(this);
        }
        setChanged();
    }

    private void saveSharedData(CompoundTag tag) {
        if (recipe != null) {
            tag.putString("recipe", recipe.getId().toString());
        }
        tag.putBoolean("processing", processing);
        tag.putInt("processingTime", processingTime);
        tag.putIntArray("colors", colors);
    }

    private void loadSharedData(CompoundTag tag) {
        refreshRecipes();
        if (tag.contains("recipe")) {
            ResourceLocation location = new ResourceLocation(tag.getString("recipe"));
            recipe = Helper.find(availableRecipes, recipe -> recipe.getId().equals(location))
                    .orElse(null);
        } else {
            recipe = null;
        }
        processing = recipe != null && tag.getBoolean("processing");
        processingTime = recipe != null ? tag.getInt("processingTime") : 0;
        colors = tag.getIntArray("colors");
    }
}
