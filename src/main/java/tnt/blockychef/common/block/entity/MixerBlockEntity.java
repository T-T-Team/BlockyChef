package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.data.fluids.FluidHolder;
import tnt.blockychef.common.food.fluid.Fluid;
import tnt.blockychef.common.food.fluid.FluidContainer;
import tnt.blockychef.common.food.fluid.FluidType;
import tnt.blockychef.common.food.recipe.MixerRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.Helper;
import tnt.blockychef.util.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class MixerBlockEntity extends RecipeRemberingBlockEntity<MixerRecipe> implements SynchronizableBlockEntity, IndexedColorHolder, FluidHolder {

    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5};

    private final FluidContainer container;
    private MixerRecipe.RpmValue selectedRpm = MixerRecipe.RpmValue.MEDIUM;
    private MixerRecipe activeRecipe;
    private int[] colors;

    public MixerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.MIXER, pos, state);
        this.container = new FluidContainer(750, false);
        this.colors = new int[1];
        Arrays.fill(this.colors, Integer.MIN_VALUE);
    }

    @Override
    public boolean hasFluid(Fluid fluid) {
        return container.hasFluid(fluid);
    }

    @Override
    public boolean extract(Fluid fluid) {
        return container.extract(fluid);
    }

    public boolean canBlend() {
        if (activeRecipe == null)
            return false;
        Map<FluidType, Integer> fluidMap = container.getFluids();
        Fluid result = activeRecipe.getOutput();
        if (fluidMap.size() > 0 && !fluidMap.containsKey(result.getFluidType()))
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
            activeRecipe.getInputs().forEach(ingredient -> ingredient.consume(this, INPUTS));
            activeRecipe.returnItemsToContainer(this, level, worldPosition);
            if (rpmDiff == 0) {
                Fluid fluid = activeRecipe.getOutput().copy();
                container.insert(fluid);
                storeRecipe(activeRecipe);
                if (!level.isClientSide) {
                    awardUsedRecipesAndPopExperience((ServerPlayer) player);
                }
            }
        }
        refreshRecipe();
        Helper.sendBlockEntityClientData(this);
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

    public void refreshRecipe() {
        if (level == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        Optional<MixerRecipe> optional = manager.getRecipeFor(BlockyChefRecipeTypes.MIXER_RECIPE, this, level);
        setRecipe(optional.orElse(null));
    }

    private void saveSharedData(CompoundTag tag) {
        tag.putIntArray("colors", colors);
        tag.putInt("rpm", selectedRpm.ordinal());
        tag.put("fluids", container.serialize());
    }

    private void loadSharedData(CompoundTag tag) {
        colors = tag.getIntArray("colors");
        selectedRpm = MixerRecipe.RpmValue.values()[tag.getInt("rpm") % MixerRecipe.RpmValue.values().length];
        container.deserialize(tag.getCompound("fluids"));
        refreshRecipe();
    }

    private void setRecipe(@Nullable MixerRecipe recipe) {
        if (recipe != activeRecipe) {
            activeRecipe = recipe;
            Helper.sendBlockEntityClientData(this);
        }
        setChanged();
    }
}
