package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.recipe.DoughMakerRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.Helper;
import tnt.blockychef.util.MenuInventoryHelper;
import tnt.blockychef.util.RenderHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public class DoughMakerBlockEntity extends RecipeRemberingBlockEntity<DoughMakerRecipe> implements SynchronizableBlockEntity, IndexedColorHolder, ProcessableRecipeHolder {

    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5};
    public static final int[] OUTPUTS = {6, 7, 8};

    private DoughMakerRecipe activeRecipe;
    private boolean processing;
    private int processingTime;
    private int[] colors;

    public DoughMakerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.DOUGH_MAKER, pos, state);
        this.colors = new int[1];
        Arrays.fill(this.colors, Integer.MIN_VALUE);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DoughMakerBlockEntity doughMaker) {
        if (doughMaker.activeRecipe == null || !doughMaker.processing) {
            return;
        }
        RecipeManager manager = level.getRecipeManager();
        if (manager.getRecipeFor(BlockyChefRecipeTypes.DOUGH_MAKER_RECIPE, doughMaker, level, doughMaker.activeRecipe.getId()).isEmpty()) {
            doughMaker.setRecipe(null);
            return;
        }
        ItemStack[] outputs = doughMaker.activeRecipe.getOutputs();
        if (!MenuInventoryHelper.canFitItems(outputs, doughMaker, OUTPUTS)) {
            doughMaker.setRecipe(null);
            return;
        }
        if (++doughMaker.processingTime >= doughMaker.activeRecipe.getProcessingTime() && !level.isClientSide) {
            doughMaker.processingTime = 0;
            doughMaker.activeRecipe.getInputs().forEach(ingredient -> ingredient.consume(doughMaker, INPUTS));
            doughMaker.activeRecipe.returnItemsToContainer(doughMaker, level, pos);
            ItemStack[] assembled = Arrays.stream(outputs).map(ItemStack::copy).toArray(ItemStack[]::new);
            MenuInventoryHelper.insertItems(assembled, doughMaker, OUTPUTS);
            doughMaker.storeRecipe(doughMaker.activeRecipe);
            doughMaker.refreshRecipe();
            Helper.sendBlockEntityClientData(doughMaker);
        }
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

    public boolean isProcessing() {
        return processing;
    }

    public boolean hasRecipe() {
        return activeRecipe != null;
    }

    @Override
    public void startProcessing() {
        if (processing)
            return;
        refreshRecipe();
        if (activeRecipe == null)
            return;
        processingTime = 0;
        processing = true;
        Helper.sendBlockEntityClientData(this);
    }

    public float getProcessingProgress(float partialTicks) {
        if (activeRecipe == null || !processing)
            return 0.0F;
        int oldTick = Math.max(0, processingTime - 1);
        int total = activeRecipe.getProcessingTime();
        float f0 = oldTick / (float) total;
        float f1 = processingTime / (float) total;
        return RenderHelper.interpolate(f0, f1, partialTicks);
    }

    public void refreshRecipe() {
        if (level == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        Optional<DoughMakerRecipe> optional = manager.getRecipeFor(BlockyChefRecipeTypes.DOUGH_MAKER_RECIPE, this, level);
        setRecipe(optional.orElse(null));
    }

    private void saveSharedData(CompoundTag tag) {
        tag.putBoolean("processing", processing);
        tag.putInt("processingTime", processingTime);
        tag.putIntArray("colors", colors);
    }

    private void loadSharedData(CompoundTag tag) {
        processing = tag.getBoolean("processing");
        processingTime = tag.getInt("processingTime");
        colors = tag.getIntArray("colors");
        refreshRecipe();
    }

    private void setRecipe(@Nullable DoughMakerRecipe recipe) {
        if (activeRecipe != recipe) {
            activeRecipe = recipe;
            processing = false;
            processingTime = 0;
            Helper.sendBlockEntityClientData(this);
        }
        setChanged();
    }
}
