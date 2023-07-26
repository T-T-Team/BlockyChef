package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.data.fluids.FluidHolder;
import tnt.blockychef.common.food.fluid.FluidContainer;
import tnt.blockychef.common.food.recipe.JuicerRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public class JuicerBlockEntity extends RecipeRememberingBlockEntity<JuicerRecipe> implements Synchronizable, IndexedColorHolder, FluidHolder {

    public static final int FLUID_CAPACITY = 500;

    private final FluidContainer container;
    private JuicerRecipe activeRecipe;
    private int pressCounter;
    private int[] colors;

    public JuicerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.JUICER, pos, state);
        this.container = new FluidContainer(FLUID_CAPACITY, false);
        this.colors = new int[1];
        Arrays.fill(this.colors, Integer.MIN_VALUE);
    }

    @Override
    public boolean hasFluid(FluidStack fluid) {
        return container.hasFluid(fluid);
    }

    @Override
    public boolean extract(FluidStack fluid) {
        boolean result = container.extract(fluid);
        setChanged();
        BlockEntityHelper.sendBlockEntityClientData(this);
        return result;
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(1);
    }

    public void setInputItem(ItemStack stack) {
        stack.setCount(1);
        inventoryHandler.setStackInSlot(0, stack);
        refreshRecipe();
        setChanged();
    }

    public ItemStack getInputItem() {
        return inventoryHandler.getStackInSlot(0);
    }

    public boolean hasInputItem() {
        return !getInputItem().isEmpty();
    }

    public boolean isNotFull() {
        return !container.isFull();
    }

    public float getProgress() {
        return activeRecipe != null ? pressCounter / (float) activeRecipe.getPressAmount() : 0.0F;
    }

    public void processRecipe(Player player) {
        if (activeRecipe == null) {
            if (hasInputItem()) {
                MenuInventoryHelper.dropInventoryContents(level, worldPosition, inventoryHandler);
            }
            return;
        }
        if (++pressCounter >= activeRecipe.getPressAmount()) {
            FluidStack result = activeRecipe.getOutput().copy();
            container.insert(result);
            storeRecipe(activeRecipe);
            getInputItem().shrink(1);
            pressCounter = 0;
            refreshRecipe();
            setChanged();
            if (!level.isClientSide) {
                awardUsedRecipesAndPopExperience((ServerPlayer) player);
                BlockEntityHelper.sendBlockEntityClientData(this);
            }
        }
    }

    public FluidContainer getFluids() {
        return container;
    }

    public int getPressAmount() {
        return pressCounter;
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

    private void saveSharedData(CompoundTag tag) {
        tag.putInt("pressAmount", pressCounter);
        tag.putIntArray("colors", colors);
        tag.put("fluids", container.serialize());
    }

    private void loadSharedData(CompoundTag tag) {
        pressCounter = tag.getInt("pressAmount");
        colors = tag.getIntArray("colors");
        container.deserialize(tag.getCompound("fluids"));
        refreshRecipe();
    }

    private void refreshRecipe() {
        if (level == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        Optional<JuicerRecipe> optional = manager.getRecipeFor(BlockyChefRecipeTypes.JUICER_RECIPE, this, level);
        setRecipe(optional.orElse(null));
    }

    private void setRecipe(@Nullable JuicerRecipe recipe) {
        if (recipe != activeRecipe) {
            activeRecipe = recipe;
            pressCounter = 0;
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
        setChanged();
    }
}
