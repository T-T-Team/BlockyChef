package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.recipe.GratingRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefSounds;
import tnt.blockychef.common.menu.BlockEntityStackHandler;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class GraterBlockEntity extends RecipeRememberingBlockEntity<GratingRecipe> implements Synchronizable {

    private GratingRecipe recipe;
    private int gratingAmount;

    public GraterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.GRATER, pos, state);
    }

    private void refreshRecipes() {
        ItemStack stack = getItem(0);
        if (stack.isEmpty()) {
            setRecipe(null);
        } else if (recipe == null && level != null) {
            RecipeManager manager = level.getRecipeManager();
            Optional<GratingRecipe> optional = manager.getRecipeFor(BlockyChefRecipeTypes.GRATING_RECIPE, this, level);
            optional.ifPresent(this::setRecipe);
        }
    }

    public boolean hasInputItem() {
        return !getInputItem().isEmpty();
    }

    public ItemStack getInputItem() {
        return getItem(0);
    }

    public void place(ItemStack stack) {
        setItem(0, stack);
        refreshRecipes();
        setChanged();
    }

    public void processRecipe(ServerPlayer player) {
        if (hasActiveRecipe()) {
            SoundEvent event = gratingAmount % 2 == 0 ? BlockyChefSounds.GRATER_A : BlockyChefSounds.GRATER_B;
            level.playSound(null, worldPosition, event, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (++gratingAmount >= recipe.getGratingAmount()) {
                storeRecipe(recipe);
                ItemStack output = recipe.assemble(this, level.registryAccess());
                setRecipe(null);
                setItem(0, ItemStack.EMPTY);
                MenuInventoryHelper.giveItemOrDrop(player, output);
                awardUsedRecipesAndPopExperience(player);
            }
        } else if (!level.isClientSide) {
            MenuInventoryHelper.dropInventoryContents(level, worldPosition, getItemHandler());
        }
        setChanged();
        BlockEntityHelper.sendBlockEntityClientData(this);
    }

    public boolean hasActiveRecipe() {
        refreshRecipes();
        return recipe != null;
    }

    public boolean isValidInput(ItemStack stack, Level level) {
        if (stack.isEmpty())
            return false;
        RecipeManager manager = level.getRecipeManager();
        List<GratingRecipe> gratingRecipes = manager.getAllRecipesFor(BlockyChefRecipeTypes.GRATING_RECIPE);
        for (GratingRecipe gratingRecipe : gratingRecipes) {
            if (gratingRecipe.isValidInput(stack)) {
                return true;
            }
        }
        return false;
    }

    public float getGratingProgress() {
        return recipe != null ? (float) this.gratingAmount / recipe.getGratingAmount() : 0.0F;
    }

    public int getGratingAmount() {
        return gratingAmount;
    }

    public GratingRecipe getRecipe() {
        return this.recipe;
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new BlockEntityStackHandler(1, this::setChanged);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("gratingAmount", this.gratingAmount);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.gratingAmount = tag.getInt("gratingAmount");
        this.refreshRecipes();
    }

    @Override
    public void encodeData(CompoundTag tag) {
        ItemStack stack = inventoryHandler.getStackInSlot(0);
        if (!stack.isEmpty()) {
            tag.put("item", stack.serializeNBT());
        }
        tag.putInt("gratingAmount", gratingAmount);
    }

    @Override
    public void decodeData(CompoundTag tag) {
        ItemStack stack = tag.contains("item") ? ItemStack.of(tag.getCompound("item")) : ItemStack.EMPTY;
        inventoryHandler.setStackInSlot(0, stack);
        gratingAmount = tag.getInt("gratingAmount");
    }

    private void setRecipe(@Nullable GratingRecipe recipe) {
        this.recipe = recipe;
        this.gratingAmount = 0;
        setChanged();
        BlockEntityHelper.sendBlockEntityClientData(this);
    }
}
