package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.recipe.MixingBowlRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefSounds;
import tnt.blockychef.common.menu.BlockEntityStackHandler;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.math.Interpolation;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MixingBowlBlockEntity extends RecipeRememberingBlockEntity<MixingBowlRecipe> implements Synchronizable, ProcessableRecipeHolder {

    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5};
    public static final int[] OUTPUTS = {6, 7, 8};

    private MixingBowlRecipe activeRecipe;
    private boolean mixing;
    private int mixingTime;

    public MixingBowlBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.MIXING_BOWL, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MixingBowlBlockEntity mixingBowl) {
        if (mixingBowl.activeRecipe == null || !mixingBowl.mixing) {
            return;
        }
        MixingBowlRecipe recipe = mixingBowl.activeRecipe;
        RecipeManager manager = level.getRecipeManager();
        if (manager.getRecipeFor(BlockyChefRecipeTypes.MIXING_BOWL_RECIPE, mixingBowl, level, mixingBowl.activeRecipe.getId()).isEmpty()) {
            mixingBowl.setRecipe(null);
            return;
        }
        ItemStack[] outputs = recipe.getOutputs();
        if (!MenuInventoryHelper.canFitItems(outputs, mixingBowl, OUTPUTS)) {
            mixingBowl.setRecipe(null);
            return;
        }
        if (canPlaySound(60, mixingBowl.mixingTime, recipe.getMixingTime())) {
            level.playSound(null, pos, BlockyChefSounds.MIXING_BOWL, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        if (++mixingBowl.mixingTime >= recipe.getMixingTime() && !level.isClientSide) {
            mixingBowl.mixingTime = 0;
            mixingBowl.consumeIngredientsAndApplyCraftRemainder(recipe, INPUTS, OUTPUTS, in -> {
                List<ItemStack> allConsumed = new ArrayList<>();
                recipe.getInputs().forEach(multiIngredient -> allConsumed.addAll(multiIngredient.consume(mixingBowl, in)));
                return allConsumed;
            });
            ItemStack[] assembledOutputs = Arrays.stream(outputs).map(ItemStack::copy).toArray(ItemStack[]::new);
            MenuInventoryHelper.insertItems(assembledOutputs, mixingBowl, OUTPUTS);
            mixingBowl.storeRecipe(mixingBowl.activeRecipe);
            mixingBowl.refreshRecipe();
            BlockEntityHelper.sendBlockEntityClientData(mixingBowl);
        }
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new BlockEntityStackHandler(INPUTS.length + OUTPUTS.length, this::setChanged);
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
    public void encodeData(CompoundTag tag) {
        MenuInventoryHelper.encodeInventory(inventoryHandler, tag);
        saveSharedData(tag);
    }

    @Override
    public void decodeData(CompoundTag tag) {
        MenuInventoryHelper.decodeInventory(inventoryHandler, tag);
        loadSharedData(tag);
    }

    public float getMixingProgress(float partialTicks) {
        if (activeRecipe == null || !mixing)
            return 0.0F;
        int oldTick = Math.max(0, mixingTime - 1);
        int total = activeRecipe.getMixingTime();
        float f0 = oldTick / (float) total;
        float f1 = mixingTime / (float) total;
        return Interpolation.linear(f0, f1, partialTicks);
    }

    public boolean isMixing() {
        return mixing;
    }

    public boolean hasRecipe() {
        return activeRecipe != null;
    }

    @Override
    public void startProcessing() {
        if (mixing)
            return;
        refreshRecipe();
        if (activeRecipe == null)
            return;
        mixingTime = 0;
        mixing = true;
        BlockEntityHelper.sendBlockEntityClientData(this);
        setChanged();
    }

    public void refreshRecipe() {
        if (level == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        Optional<MixingBowlRecipe> optional = manager.getRecipeFor(BlockyChefRecipeTypes.MIXING_BOWL_RECIPE, this, level);
        setRecipe(optional.orElse(null));
    }

    private void setRecipe(@Nullable MixingBowlRecipe recipe) {
        if (activeRecipe != recipe) {
            activeRecipe = recipe;
            mixingTime = 0;
            mixing = false;
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
        setChanged();
    }

    private void saveSharedData(CompoundTag tag) {
        tag.putInt("mixingTime", mixingTime);
        tag.putBoolean("mixing", mixing);
    }

    private void loadSharedData(CompoundTag tag) {
        mixingTime = tag.getInt("mixingTime");
        mixing = tag.getBoolean("mixing");
        refreshRecipe();
    }
}
