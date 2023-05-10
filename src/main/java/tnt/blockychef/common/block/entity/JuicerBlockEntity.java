package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.fluid.FluidContainer;
import tnt.blockychef.common.food.recipe.JuicerRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.Helper;
import tnt.blockychef.util.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public class JuicerBlockEntity extends RecipeRemberingBlockEntity<JuicerRecipe> implements SynchronizableBlockEntity, IndexedColorHolder {

    private final FluidContainer container;
    private JuicerRecipe activeRecipe;
    private int pressCounter;
    private int[] colors;

    public JuicerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.JUICER, pos, state);
        this.container = new FluidContainer(1000, false);
        this.colors = new int[1];
        Arrays.fill(this.colors, Integer.MIN_VALUE);
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(1);
    }

    public ItemStack getInputItem() {
        return inventoryHandler.getStackInSlot(0);
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

    private void saveSharedData(CompoundTag tag) {
        tag.putInt("pressAmount", pressCounter);
        tag.putIntArray("colors", colors);
    }

    private void loadSharedData(CompoundTag tag) {
        pressCounter = tag.getInt("pressAmount");
        colors = tag.getIntArray("colors");
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
            Helper.sendBlockEntityClientData(this);
        }
        setChanged();
    }
}
