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
import tnt.blockychef.common.food.recipe.MortarRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefSounds;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.math.Interpolation;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MortarAndPestleBlockEntity extends RecipeRememberingBlockEntity<MortarRecipe> implements Synchronizable, ProcessableRecipeHolder {

    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5};
    public static final int[] OUTPUT = {6, 7, 8};

    private RecipeHolder<MortarRecipe> activeRecipe;
    private boolean processing;
    private int currentProcessingTime;

    public MortarAndPestleBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.MORTAR_AND_PESTLE, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MortarAndPestleBlockEntity mortarAndPestle) {
        if (mortarAndPestle.activeRecipe == null || !mortarAndPestle.processing)
            return;
        RecipeManager manager = level.getRecipeManager();
        MortarRecipe recipe = mortarAndPestle.activeRecipe.value();
        if (manager.getRecipeFor(BlockyChefRecipeTypes.MORTAR_AND_PESTLE_RECIPE, mortarAndPestle, level, mortarAndPestle.activeRecipe.id()).isEmpty()) {
            mortarAndPestle.setRecipe(null);
            return;
        }
        ItemStack[] result = recipe.getOutput();
        if (!MenuInventoryHelper.canFitItems(result, mortarAndPestle, OUTPUT)) {
            mortarAndPestle.setRecipe(null);
            return;
        }
        if (canPlaySound(80, mortarAndPestle.currentProcessingTime, recipe.getProcessingTime())) {
            level.playSound(null, pos, BlockyChefSounds.MORTAR_AND_PESTLE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        if (++mortarAndPestle.currentProcessingTime >= recipe.getProcessingTime() && !level.isClientSide) {
            mortarAndPestle.currentProcessingTime = 0;
            mortarAndPestle.consumeIngredientsAndApplyCraftRemainder(recipe, INPUTS, OUTPUT, in -> {
                List<ItemStack> allConsumed = new ArrayList<>();
                recipe.getInputs().forEach(multiIngredient -> allConsumed.addAll(multiIngredient.consume(mortarAndPestle, in)));
                return allConsumed;
            });
            MenuInventoryHelper.insertItems(result, mortarAndPestle, OUTPUT);
            mortarAndPestle.storeRecipe(mortarAndPestle.activeRecipe);
            mortarAndPestle.refreshRecipe();
            BlockEntityHelper.sendBlockEntityClientData(mortarAndPestle);
        }
    }

    public float getGrindingProgress(float partialTicks) {
        if (activeRecipe == null || !processing)
            return 0.0F;
        int oldTick = Math.max(0, currentProcessingTime - 1);
        int total = activeRecipe.value().getProcessingTime();
        float f0 = oldTick / (float) total;
        float f1 = currentProcessingTime / (float) total;
        return Interpolation.linear(f0, f1, partialTicks);
    }

    public boolean isGrinding() {
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
        currentProcessingTime = 0;
        processing = true;
        BlockEntityHelper.sendBlockEntityClientData(this);
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(9);
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

    private void saveSharedData(CompoundTag tag) {
        tag.putInt("processingTime", currentProcessingTime);
        tag.putBoolean("processing", processing);
    }

    private void loadSharedData(CompoundTag tag) {
        currentProcessingTime = tag.getInt("processingTime");
        processing = tag.getBoolean("processing");
        refreshRecipe();
    }

    public void refreshRecipe() {
        if (level == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        Optional<RecipeHolder<MortarRecipe>> optional = manager.getRecipeFor(BlockyChefRecipeTypes.MORTAR_AND_PESTLE_RECIPE, this, level);
        setRecipe(optional.orElse(null));
    }

    private void setRecipe(@Nullable RecipeHolder<MortarRecipe> recipe) {
        if (recipe != this.activeRecipe) {
            this.activeRecipe = recipe;
            this.currentProcessingTime = 0;
            this.processing = false;
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
        setChanged();
    }
}
