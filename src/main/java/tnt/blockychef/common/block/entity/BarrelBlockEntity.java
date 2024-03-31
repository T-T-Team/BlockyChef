package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.recipe.BarrelRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.math.Interpolation;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.*;

public class BarrelBlockEntity extends RecipeRememberingBlockEntity<BarrelRecipe> implements ProcessableRecipeHolder, Synchronizable {

    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5};
    public static final int[] OUTPUTS = {6, 7, 8};

    private RecipeHolder<BarrelRecipe> activeRecipe;
    private boolean fermenting;
    private int fermentingTime;

    public BarrelBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.BARREL, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BarrelBlockEntity barrel) {
        if (barrel.fermenting && barrel.activeRecipe == null) {
            barrel.refreshRecipe();
            barrel.fermenting = barrel.activeRecipe != null;
            return;
        }
        if (barrel.activeRecipe == null || !barrel.fermenting) {
            return;
        }
        BarrelRecipe recipe = barrel.activeRecipe.value();
        RecipeManager manager = level.getRecipeManager();
        if (manager.getRecipeFor(BlockyChefRecipeTypes.BARREL_RECIPE, barrel, level, barrel.activeRecipe.id()).isEmpty()) {
            barrel.setRecipe(null);
            return;
        }
        ItemStack[] outputs = recipe.getOutputs();
        if (!MenuInventoryHelper.canFitItems(outputs, barrel, OUTPUTS)) {
            barrel.setRecipe(null);
            return;
        }
        if (++barrel.fermentingTime >= recipe.getFermentTime() && !level.isClientSide) {
            barrel.fermentingTime = 0;
            barrel.consumeIngredientsAndApplyCraftRemainder(recipe, INPUTS, OUTPUTS, in -> recipe.getInputs().forEach(multiIngredient -> multiIngredient.consume(barrel, in)));
            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            ItemStack[] assembledOutputs = Arrays.stream(outputs).map(ItemStack::copy).toArray(ItemStack[]::new);
            MenuInventoryHelper.insertItems(assembledOutputs, barrel, OUTPUTS);
            barrel.storeRecipe(barrel.activeRecipe);
            barrel.refreshRecipe();
            BlockEntityHelper.sendBlockEntityClientData(barrel);
        }
    }

    public List<ItemStack> getInputItems() {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int slot : INPUTS) {
            ItemStack stack = getItem(slot);
            if (!stack.isEmpty()) {
                itemStacks.add(stack);
            }
        }
        return itemStacks;
    }

    public List<ItemStack> getOutputs() {
        return activeRecipe != null ? Arrays.asList(activeRecipe.value().getOutputs()) : Collections.emptyList();
    }

    public int getFermentingTime() {
        return fermentingTime;
    }

    public int getTotalFermentTime() {
        return activeRecipe != null ? activeRecipe.value().getFermentTime() : 1;
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(INPUTS.length + OUTPUTS.length);
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

    public float getFermentingProgress(float partialTicks) {
        if (activeRecipe == null || !fermenting)
            return 0.0F;
        int oldTick = Math.max(0, fermentingTime - 1);
        int total = activeRecipe.value().getFermentTime();
        float f0 = oldTick / (float) total;
        float f1 = fermentingTime / (float) total;
        return Interpolation.linear(f0, f1, partialTicks);
    }

    public boolean isFermenting() {
        return fermenting;
    }

    public boolean hasRecipe() {
        return activeRecipe != null;
    }

    @Override
    public void startProcessing() {
        if (fermenting)
            return;
        refreshRecipe();
        if (activeRecipe == null)
            return;
        fermentingTime = 0;
        fermenting = true;
        BlockEntityHelper.sendBlockEntityClientData(this);
    }

    public void refreshRecipe() {
        if (level == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        Optional<RecipeHolder<BarrelRecipe>> optional = manager.getRecipeFor(BlockyChefRecipeTypes.BARREL_RECIPE, this, level);
        setRecipe(optional.orElse(null));
    }

    @Override
    public void startOpen(Player pPlayer) {
        if (!remove && !pPlayer.isSpectator()) {
            this.openContainer(level, worldPosition, getBlockState());
        }
    }

    @Override
    public void stopOpen(Player pPlayer) {
        if (!remove && !pPlayer.isSpectator()) {
            this.closeContainer(level, worldPosition, getBlockState());
        }
    }

    private void setRecipe(@Nullable RecipeHolder<BarrelRecipe> recipe) {
        if (activeRecipe != recipe) {
            activeRecipe = recipe;
            fermentingTime = 0;
            fermenting = false;
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
        setChanged();
    }

    private void saveSharedData(CompoundTag tag) {
        tag.putInt("fermentingTime", fermentingTime);
        tag.putBoolean("fermenting", fermenting);
    }

    private void loadSharedData(CompoundTag tag) {
        fermentingTime = tag.getInt("fermentingTime");
        fermenting = tag.getBoolean("fermenting");
        refreshRecipe();
    }

    private void openContainer(Level level, BlockPos pos, BlockState state) {
        level.playSound(null, pos, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS);
    }

    private void closeContainer(Level level, BlockPos pos, BlockState state) {
        level.playSound(null, pos, SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS);
    }
}
