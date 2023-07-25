package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.food.recipe.DryingRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.Helper;
import tnt.blockychef.util.MenuInventoryHelper;

import java.util.List;
import java.util.Optional;

public class DryingRackBlockEntity extends RecipeRemberingBlockEntity<DryingRecipe> implements SynchronizableBlockEntity {

    private DryingRecipe recipe;
    private int ticksDrying;

    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.DRYING_RACK, pos, state);
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(1);
    }

    public boolean isValidInput(ItemStack stack, Level level) {
        RecipeManager manager = level.getRecipeManager();
        List<DryingRecipe> recipeList = manager.getAllRecipesFor(BlockyChefRecipeTypes.DRYING_RECIPE);
        if (stack.isEmpty())
            return false;
        for (DryingRecipe dryingRecipe : recipeList) {
            if (dryingRecipe.isValidInput(stack)) {
                return true;
            }
        }
        return false;
    }

    public void setItem(ItemStack stack) {
        inventoryHandler.setStackInSlot(0, stack);
        updateRecipes();
        Helper.sendBlockEntityClientData(this);
        setChanged();
    }

    public boolean hasItem() {
        return !inventoryHandler.getStackInSlot(0).isEmpty();
    }

    public int getTicksDrying() {
        return ticksDrying;
    }

    public int getTotalTime() {
        return recipe != null ? recipe.getDryingTime() : 1;
    }

    public void clearInventoryAndProcessRecipe(@Nullable Player player) {
        if (level.isClientSide)
            return;
        if (player == null) {
            getRecipesToAwardAndPopExperience((ServerLevel) level, Vec3.atCenterOf(worldPosition));
            MenuInventoryHelper.dropInventoryContents(level, worldPosition, inventoryHandler);
        } else {
            ItemStack stack = inventoryHandler.getStackInSlot(0);
            if (!stack.isEmpty()) {
                Helper.giveItem(player, stack.copy());
            }
            setItem(ItemStack.EMPTY);
            awardUsedRecipesAndPopExperience((ServerPlayer) player);
        }
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DryingRackBlockEntity dryingRack) {
        if (dryingRack.recipe != null) {
            if (dryingRack.ticksDrying++ >= dryingRack.recipe.getDryingTime()) {
                dryingRack.completeRecipe();
            }
        } else {
            dryingRack.updateRecipes();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ticksDrying", ticksDrying);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ticksDrying = tag.getInt("ticksDrying");
        this.updateRecipes();
    }

    @Override
    public void encodeBlockEntityData(CompoundTag tag) {
        ItemStack stack = inventoryHandler.getStackInSlot(0);
        if (!stack.isEmpty()) {
            tag.put("item", stack.serializeNBT());
        }
    }

    @Override
    public void decodeBlockEntityData(CompoundTag tag) {
        ItemStack stack = tag.contains("item") ? ItemStack.of(tag.getCompound("item")) : ItemStack.EMPTY;
        inventoryHandler.setStackInSlot(0, stack);
    }

    private void updateRecipes() {
        ItemStack stack = inventoryHandler.getStackInSlot(0);
        if (stack.isEmpty()) {
            clearRecipe();
        } else {
            if (level == null)
                return;
            RecipeManager manager = level.getRecipeManager();
            Optional<DryingRecipe> optional = manager.getRecipeFor(BlockyChefRecipeTypes.DRYING_RECIPE, this, level);
            clearRecipe();
            optional.ifPresent(recipe -> this.recipe = recipe);
        }
    }

    private void clearRecipe() {
        recipe = null;
        ticksDrying = 0;
    }

    private void completeRecipe() {
        ticksDrying = 0;
        if (recipe != null) {
            ItemStack result = recipe.assemble(this, level.registryAccess());
            storeRecipe(recipe);
            setItem(result);
        }
        updateRecipes();
    }
}
