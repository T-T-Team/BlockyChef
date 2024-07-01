package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.recipe.DoughMakerRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefSounds;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.math.Interpolation;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public class DoughMakerBlockEntity extends RecipeRememberingBlockEntity<DoughMakerRecipe> implements Synchronizable, IndexedColorHolder, ProcessableRecipeHolder {

    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5};
    public static final int[] OUTPUTS = {6, 7, 8};

    private RecipeHolder<DoughMakerRecipe> activeRecipe;
    private boolean processing;
    private int processingTime;
    private final Integer[] colors;

    public DoughMakerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.DOUGH_MAKER, pos, state);
        this.colors = new Integer[1];
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DoughMakerBlockEntity doughMaker) {
        if (doughMaker.activeRecipe == null || !doughMaker.processing) {
            return;
        }
        RecipeManager manager = level.getRecipeManager();
        if (manager.getRecipeFor(BlockyChefRecipeTypes.DOUGH_MAKER_RECIPE, doughMaker, level, doughMaker.activeRecipe.id()).isEmpty()) {
            doughMaker.setRecipe(null);
            return;
        }
        ItemStack[] outputs = doughMaker.activeRecipe.value().getOutputs();
        if (!MenuInventoryHelper.canFitItems(outputs, doughMaker, OUTPUTS)) {
            doughMaker.setRecipe(null);
            return;
        }
        int craftTime = doughMaker.activeRecipe.value().getProcessingTime();
        if (canPlaySound(40, doughMaker.processingTime, craftTime)) {
            level.playSound(null, pos, BlockyChefSounds.DOUGH_MAKER, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        if (++doughMaker.processingTime >= craftTime && !level.isClientSide) {
            doughMaker.processingTime = 0;
            doughMaker.consumeIngredientsAndApplyCraftRemainder(doughMaker.activeRecipe.value(), INPUTS, OUTPUTS, in -> doughMaker.activeRecipe.value().getInputs().forEach(multiIngredient -> multiIngredient.consume(doughMaker, in)));
            ItemStack[] assembled = Arrays.stream(outputs).map(ItemStack::copy).toArray(ItemStack[]::new);
            MenuInventoryHelper.insertItems(assembled, doughMaker, OUTPUTS);
            doughMaker.storeRecipe(doughMaker.activeRecipe);
            doughMaker.refreshRecipe();
            BlockEntityHelper.sendBlockEntityClientData(doughMaker);
        }
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(INPUTS.length + OUTPUTS.length);
    }

    @Override
    public @Nullable Integer getColor(int index) {
        return index >= 0 && index < colors.length ? colors[index] : null;
    }

    @Override
    public void setColor(int index, @Nullable Integer color) {
        if (index >= 0 && index < colors.length) {
            colors[index] = color;
            this.setChanged();
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    @Override
    public void encodeData(CompoundTag tag) {
        MenuInventoryHelper.encodeInventory(inventoryHandler, tag);
        saveSharedData(tag);
    }

    @Override
    public void decodeData(CompoundTag tag) {
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
        BlockEntityHelper.sendBlockEntityClientData(this);
    }

    public float getProcessingProgress(float partialTicks) {
        if (activeRecipe == null || !processing)
            return 0.0F;
        int oldTick = Math.max(0, processingTime - 1);
        int total = activeRecipe.value().getProcessingTime();
        float f0 = oldTick / (float) total;
        float f1 = processingTime / (float) total;
        return Interpolation.linear(f0, f1, partialTicks);
    }

    public void refreshRecipe() {
        if (level == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        Optional<RecipeHolder<DoughMakerRecipe>> optional = manager.getRecipeFor(BlockyChefRecipeTypes.DOUGH_MAKER_RECIPE, this, level);
        setRecipe(optional.orElse(null));
    }

    private void saveSharedData(CompoundTag tag) {
        tag.putBoolean("processing", processing);
        tag.putInt("processingTime", processingTime);
        ColorableBlockEntity.saveColorData(colors, tag);
    }

    private void loadSharedData(CompoundTag tag) {
        processing = tag.getBoolean("processing");
        processingTime = tag.getInt("processingTime");
        ColorableBlockEntity.loadColorData(colors, tag);
        refreshRecipe();
    }

    private void setRecipe(@Nullable RecipeHolder<DoughMakerRecipe> recipe) {
        if (activeRecipe != recipe) {
            activeRecipe = recipe;
            processing = false;
            processingTime = 0;
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
        setChanged();
    }
}
