package tnt.blockychef.common.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import tnt.blockychef.common.food.recipe.AbstractFoodRecipe;
import tnt.blockychef.common.food.recipe.BurnableRecipe;
import tnt.blockychef.util.Helper;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;

import java.util.Optional;

public abstract class CookingSlot<R extends AbstractFoodRecipe<B> & BurnableRecipe, B extends RecipeRememberingBlockEntity<R> & Synchronizable> {

    private final int slotIndex;
    protected final B blockEntity;
    protected int progressionTimer;
    protected int totalTimer;
    protected float burnAmount;
    protected RecipeHolder<R> recipe;

    public CookingSlot(int slotIndex, B blockEntity) {
        this.slotIndex = slotIndex;
        this.blockEntity = blockEntity;
    }

    public abstract RecipeType<R> getRecipeType();

    public abstract Optional<RecipeHolder<R>> getRecipe(RecipeManager manager, ItemStack input);

    public void loadRecipe(RecipeManager manager) {
        ItemStack input = getItem();
        RecipeHolder<R> rRecipe = getRecipe(manager, input).orElse(null);
        if (rRecipe == null || recipe != rRecipe) {
            progressionTimer = 0;
            totalTimer = 0;
            burnAmount = 0;
            recipe = rRecipe;
        }
        if (recipe != null) {
            this.recipeLoaded(recipe);
        }
        BlockEntityHelper.sendBlockEntityClientData(blockEntity);
        blockEntity.setChanged();
    }

    public float getProgress() {
        if (recipe != null && recipe.value().isBurning()) {
            return 0.0F;
        }
        return progressionTimer / (float) totalTimer;
    }

    public float getBurnProgress() {
        if (recipe != null && recipe.value().isBurning()) {
            return progressionTimer / (float) totalTimer;
        }
        return burnAmount;
    }

    public ItemStack getItem() {
        return blockEntity.getItem(getSlotIndex());
    }

    public final int getSlotIndex() {
        return this.slotIndex;
    }

    protected void recipeLoaded(RecipeHolder<R> recipe) {}

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("progression", progressionTimer);
        tag.putInt("total", totalTimer);
        tag.putFloat("burn", burnAmount);
        if (recipe != null) {
            tag.putString("recipeId", recipe.id().toString());
        }
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        progressionTimer = tag.getInt("progression");
        totalTimer = tag.getInt("total");
        burnAmount = tag.getFloat("burn");

        if (blockEntity.getLevel() != null && tag.contains("recipeId")) {
            Optional<RecipeHolder<R>> optional = Helper.findRecipeByIdFor(blockEntity.getLevel().getRecipeManager(), getRecipeType(), new ResourceLocation(tag.getString("recipeId")));
            recipe = optional.orElse(null);
        }
    }

    public interface RecipeProvider<R extends AbstractFoodRecipe<?>> {
        Optional<R> getRecipe(RecipeManager manager, ItemStack stack);
    }
}
