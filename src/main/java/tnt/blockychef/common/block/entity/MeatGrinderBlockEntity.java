package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.recipe.MeatGrinderRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefSounds;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.Optional;

public class MeatGrinderBlockEntity extends RecipeRememberingBlockEntity<MeatGrinderRecipe> implements Synchronizable {

    private RecipeHolder<MeatGrinderRecipe> recipe;
    private int grindAmount;

    public MeatGrinderBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.MEAT_GRINDER, pos, state);
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(1);
    }

    public ItemStack getInputItem() {
        return inventoryHandler.getStackInSlot(0);
    }

    public boolean hasInput() {
        return !getInputItem().isEmpty();
    }

    public void setInput(ItemStack stack) {
        inventoryHandler.setStackInSlot(0, stack);
        setChanged();
        refreshRecipe();
    }

    public int getGrindAmount() {
        return grindAmount;
    }

    public MeatGrinderRecipe getRecipe() {
        return recipe != null ? recipe.value() : null;
    }

    public void processRecipe(Player player) {
        if (recipe == null) {
            return;
        }
        level.playSound(null, worldPosition, BlockyChefSounds.MEAT_GRINDER, SoundSource.BLOCKS, 1.0F, 1.0F);
        MeatGrinderRecipe grinderRecipe = recipe.value();
        if (++grindAmount >= grinderRecipe.getProcessingAmount()) {
            ItemStack result = grinderRecipe.assemble(this, level.registryAccess());
            inventoryHandler.setStackInSlot(0, result);
            storeRecipe(recipe);
            grindAmount = 0;
            if (!level.isClientSide) {
                awardUsedRecipesAndPopExperience((ServerPlayer) player);
                MenuInventoryHelper.dropInventoryContents(level, worldPosition, inventoryHandler);
                BlockEntityHelper.sendBlockEntityClientData(this);
            }
        }
    }

    @Override
    public void encodeData(CompoundTag tag) {
        MenuInventoryHelper.encodeInventory(inventoryHandler, tag);
        tag.putInt("grindAmount", grindAmount);
    }

    @Override
    public void decodeData(CompoundTag tag) {
        MenuInventoryHelper.decodeInventory(inventoryHandler, tag);
        grindAmount = tag.getInt("grindAmount");
        refreshRecipe();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        grindAmount = tag.getInt("grindAmount");
        refreshRecipe();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("grindAmount", grindAmount);
    }

    private void refreshRecipe() {
        if (level == null)
            return;
        RecipeManager recipeManager = level.getRecipeManager();
        Optional<RecipeHolder<MeatGrinderRecipe>> optional = recipeManager.getRecipeFor(BlockyChefRecipeTypes.MEAT_GRINDER_RECIPE, this, level);
        setRecipe(optional.orElse(null));
        if (!level.isClientSide) {
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    private void setRecipe(@Nullable RecipeHolder<MeatGrinderRecipe> recipe) {
        boolean changed = this.recipe != recipe;
        this.recipe = recipe;
        if (changed) {
            grindAmount = 0;
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }
}
