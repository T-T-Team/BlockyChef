package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.recipe.MortarRecipe;
import tnt.blockychef.common.food.recipe.MultiIngredient;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.Helper;
import tnt.blockychef.util.MenuInventoryHelper;
import tnt.blockychef.util.RenderHelper;

import javax.annotation.Nullable;
import java.util.Optional;

public class MortarAndPestleBlockEntity extends RecipeRemberingBlockEntity<MortarRecipe> implements SynchronizableBlockEntity, ProcessableRecipeHolder {

    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5};
    public static final int OUTPUT = 6;

    private MortarRecipe activeRecipe;
    private boolean processing;
    private int currentProcessingTime;

    public MortarAndPestleBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.MORTAR_AND_PESTLE, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MortarAndPestleBlockEntity mortarAndPestle) {
        if (mortarAndPestle.activeRecipe == null || !mortarAndPestle.processing)
            return;
        RecipeManager manager = level.getRecipeManager();
        if (manager.getRecipeFor(BlockyChefRecipeTypes.MORTAR_AND_PESTLE_RECIPE, mortarAndPestle, level, mortarAndPestle.activeRecipe.getId()).isEmpty()) {
            mortarAndPestle.setRecipe(null);
            return;
        }
        ItemStack result = mortarAndPestle.activeRecipe.getResultItem(level.registryAccess());
        if (!MenuInventoryHelper.canFitItems(new ItemStack[] {result}, mortarAndPestle, OUTPUT)) {
            mortarAndPestle.setRecipe(null);
            return;
        }
        if (++mortarAndPestle.currentProcessingTime >= mortarAndPestle.activeRecipe.getProcessingTime() && !level.isClientSide) {
            mortarAndPestle.currentProcessingTime = 0;
            for (MultiIngredient ingredient : mortarAndPestle.activeRecipe.getInputs()) {
                ingredient.consume(mortarAndPestle, INPUTS);
            }
            ItemStack[] output = new ItemStack[] { mortarAndPestle.activeRecipe.assemble(mortarAndPestle, level.registryAccess()) };
            MenuInventoryHelper.insertItems(output, mortarAndPestle, OUTPUT);
            mortarAndPestle.storeRecipe(mortarAndPestle.activeRecipe);
            mortarAndPestle.refreshRecipe();
            Helper.sendBlockEntityClientData(mortarAndPestle);
        }
    }

    public float getGrindingProgress(float partialTicks) {
        if (activeRecipe == null || !processing)
            return 0.0F;
        int oldTick = Math.max(0, currentProcessingTime - 1);
        int total = activeRecipe.getProcessingTime();
        float f0 = oldTick / (float) total;
        float f1 = currentProcessingTime / (float) total;
        return RenderHelper.interpolate(f0, f1, partialTicks);
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
        Helper.sendBlockEntityClientData(this);
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(7);
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
        Optional<MortarRecipe> optional = manager.getRecipeFor(BlockyChefRecipeTypes.MORTAR_AND_PESTLE_RECIPE, this, level);
        MortarRecipe recipe = optional.orElse(null);
        setRecipe(recipe);
    }

    private void setRecipe(@Nullable MortarRecipe recipe) {
        if (recipe != this.activeRecipe) {
            this.activeRecipe = recipe;
            this.currentProcessingTime = 0;
            this.processing = false;
            Helper.sendBlockEntityClientData(this);
        }
        setChanged();
    }
}
