package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.data.fluids.FluidHolder;
import tnt.blockychef.common.food.fluid.FluidContainer;
import tnt.blockychef.common.food.recipe.MixerRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefSounds;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MixerBlockEntity extends RecipeRememberingBlockEntity<MixerRecipe> implements Synchronizable, IndexedColorHolder, FluidHolder {

    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5};
    public static final int FLUID_CAPACITY = 500;

    private final FluidContainer container;
    private MixerRecipe.RpmValue selectedRpm = MixerRecipe.RpmValue.MEDIUM;
    private MixerRecipe activeRecipe;
    private final Integer[] colors;

    public MixerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.MIXER, pos, state);
        this.container = new FluidContainer(FLUID_CAPACITY, false);
        this.colors = new Integer[1];
    }

    @Override
    public boolean hasFluid(FluidStack fluid) {
        return container.hasFluid(fluid);
    }

    @Override
    public boolean extract(FluidStack fluid) {
        boolean extracted = container.extract(fluid);
        if (extracted) {
            this.setChanged();
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
        return extracted;
    }

    public boolean canBlend() {
        if (activeRecipe == null)
            return false;
        FluidStack result = activeRecipe.getOutput();
        FluidStack check = result.copy();
        check.setAmount(1);
        if (!container.getFluids().isEmpty() && !container.hasFluid(check))
            return false;
        return container.getAmount() + result.getAmount() <= container.getCapacity();
    }

    public void blend(Player player) {
        refreshRecipe();
        if (activeRecipe == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        if (manager.getRecipeFor(BlockyChefRecipeTypes.MIXER_RECIPE, this, level, activeRecipe.getId()).isEmpty())
            return;
        MixerRecipe.RpmValue recipeRpm = activeRecipe.getRpm();
        int rpmDiff = recipeRpm.ordinal() - selectedRpm.ordinal();
        if (rpmDiff <= 0) {
            consumeIngredientsAndApplyCraftRemainder(activeRecipe, INPUTS, new int[0], in -> {
                List<ItemStack> allConsumed = new ArrayList<>();
                activeRecipe.getInputs().forEach(multiIngredient -> allConsumed.addAll(multiIngredient.consume(this, in)));
                return allConsumed;
            });
            if (rpmDiff == 0) {
                FluidStack stack = activeRecipe.getOutput().copy();
                container.insert(stack);
                storeRecipe(activeRecipe);
                if (!level.isClientSide) {
                    awardUsedRecipesAndPopExperience((ServerPlayer) player);
                }
            }
        }
        level.playSound(null, worldPosition, BlockyChefSounds.MIXER, SoundSource.BLOCKS, 0.8F, 0.8F + selectedRpm.ordinal() * 0.2F);
        refreshRecipe();
        BlockEntityHelper.sendBlockEntityClientData(this);
    }

    public void setSelectedRpm(MixerRecipe.RpmValue value) {
        this.selectedRpm = value;
        setChanged();
    }

    public int getCurrentRpmIndex() {
        return selectedRpm.ordinal();
    }

    public FluidContainer getFluids() {
        return container;
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(INPUTS.length);
    }

    @Override
    public @Nullable Integer getColor(int index) {
        return index >= 0 && index < colors.length ? colors[index] : null;
    }

    @Override
    public void setColor(int index, @Nullable Integer color) {
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

    public void refreshRecipe() {
        if (level == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        Optional<MixerRecipe> optional = manager.getRecipeFor(BlockyChefRecipeTypes.MIXER_RECIPE, this, level);
        setRecipe(optional.orElse(null));
    }

    private void saveSharedData(CompoundTag tag) {
        ColorableBlockEntity.saveColorData(colors, tag);
        tag.putInt("rpm", selectedRpm.ordinal());
        tag.put("fluids", container.serialize());
    }

    private void loadSharedData(CompoundTag tag) {
        ColorableBlockEntity.loadColorData(colors, tag);
        selectedRpm = MixerRecipe.RpmValue.values()[tag.getInt("rpm") % MixerRecipe.RpmValue.values().length];
        container.deserialize(tag.getCompound("fluids"));
        refreshRecipe();
    }

    private void setRecipe(@Nullable MixerRecipe recipe) {
        if (recipe != activeRecipe) {
            activeRecipe = recipe;
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
        setChanged();
    }
}
