package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tnt.blockychef.common.food.recipe.AbstractFoodRecipe;
import tnt.blockychef.util.Helper;

public abstract class SingleItemRecipeBlockEntity<R extends AbstractFoodRecipe<?>> extends RecipeRemberingBlockEntity<R> implements SynchronizableBlockEntity {

    private R recipe;

    public SingleItemRecipeBlockEntity(BlockEntityType<? extends SingleItemRecipeBlockEntity<?>> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public final ItemStack getItem() {
        return inventoryHandler.getStackInSlot(0);
    }

    public final boolean hasItem() {
        return !getItem().isEmpty();
    }

    public final void setItem(ItemStack stack) {
        inventoryHandler.setStackInSlot(0, stack);
        updateRecipes();
        Helper.sendBlockEntityClientData(this);
        setChanged();
    }

    public final void updateRecipes() {
        ItemStack stack = getItem();

    }

    public void clearRecipe() {
        recipe = null;
    }
}
