package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.data.fluids.FluidHolder;
import tnt.blockychef.common.food.fluid.FluidContainer;
import tnt.blockychef.common.food.recipe.TeapotRecipe;
import tnt.blockychef.common.heat.HeatHelper;
import tnt.blockychef.common.heat.HeatSource;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefTags;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import javax.annotation.Nullable;
import java.util.Optional;

public class TeapotBlockEntity extends RecipeRememberingBlockEntity<TeapotRecipe> implements Synchronizable, FluidHolder {

    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5};
    public static final int WATER = 6;
    public static final int CAPACITY = 1000;
    public static final int WATER_BOILING_TIME = 400;
    private final FluidContainer fluidContainer;

    private RecipeHolder<TeapotRecipe> recipeHolder;
    private int cookingTime;
    private int waterBoilTime;
    private float temperature;

    public TeapotBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.TEAPOT, pos, state);
        this.fluidContainer = new FluidContainer(CAPACITY, false);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TeapotBlockEntity teapot) {
        HeatSource heatSource = HeatHelper.getHeatSource(level, pos, Direction.DOWN);
        teapot.temperature = HeatHelper.regulateHeat(teapot.temperature, heatSource.getHeat(Direction.UP), 0.025F);

        if (teapot.recipeHolder != null) {
            TeapotRecipe recipe = teapot.recipeHolder.value();
            if (teapot.waterBoilTime > 0 && !recipe.matches(teapot, level)) {
                // recipe is not valid and boiling has started
                teapot.setRecipe(null);
                return;
            }
            if (recipe.isBurning() && teapot.temperature >= recipe.getMinTemperature()) {
                // Vaporization
                if (++teapot.cookingTime >= recipe.getCookingTime() && !level.isClientSide) {
                    teapot.fluidContainer.clear();
                    teapot.waterBoilTime = 0;
                    teapot.cookingTime = 0;
                    teapot.reloadRecipe();
                    BlockEntityHelper.sendBlockEntityClientData(teapot);
                }
                return;
            }

            float requiredTemperature = recipe.getMinTemperature();
            if (teapot.temperature >= requiredTemperature) {
                // boil water / cook
                if (teapot.waterBoilTime < WATER_BOILING_TIME) {
                    teapot.waterBoilTime++;
                } else if (++teapot.cookingTime >= recipe.getCookingTime() && !level.isClientSide) {
                    teapot.cookingTime = 0;
                    teapot.consumeIngredientsAndApplyCraftRemainder(recipe, INPUTS, new int[0], in -> recipe.getInputs().forEach(multiIngredient -> multiIngredient.consume(teapot, in)));
                    teapot.storeRecipe(teapot.recipeHolder);
                    FluidStack baseFluid = recipe.getBaseFluid().copy();
                    teapot.fluidContainer.extract(baseFluid);
                    FluidStack output = recipe.getResult().copy();
                    teapot.fluidContainer.insert(output);
                    teapot.reloadRecipe();
                    BlockEntityHelper.sendBlockEntityClientData(teapot);
                }
            } else {
                teapot.cookingTime = 0;
                teapot.waterBoilTime = Math.max(0, --teapot.waterBoilTime);
            }
        }
    }

    public FluidContainer getFluidContainer() {
        return fluidContainer;
    }

    public float getTemperature() {
        return temperature;
    }

    public void reloadRecipe() {
        if (level == null)
            return;
        RecipeManager manager = level.getRecipeManager();
        Optional<RecipeHolder<TeapotRecipe>> optional = manager.getRecipeFor(BlockyChefRecipeTypes.TEAPOT_RECIPE, this, level);
        optional.ifPresentOrElse(this::setRecipe, () -> setRecipe(null));
    }

    public float getCookProgress() {
        return recipeHolder != null ? cookingTime / (float) recipeHolder.value().getCookingTime() : 0.0F;
    }

    public float getWaterBoilProgress() {
        return recipeHolder != null ? recipeHolder.value().isBurning() ? 1.0F : waterBoilTime / (float) WATER_BOILING_TIME : 0.0F;
    }

    public void setRecipe(@Nullable RecipeHolder<TeapotRecipe> recipe) {
        if (this.recipeHolder != recipe) {
            this.recipeHolder = recipe;
            this.waterBoilTime = 0;
            this.cookingTime = 0;
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
        setChanged();
    }

    public void waterInputItemChanged(ItemStack stack) {
        int waterAmount = fluidContainer.getStoredAmount(Fluids.WATER.getFluidType());
        if (stack.is(BlockyChefTags.Items.WATER) && waterAmount < CAPACITY) {
            fluidContainer.insert(new FluidStack(Fluids.WATER, 500));
            ItemStack returnItem = stack.getCraftingRemainingItem();
            if (!returnItem.isEmpty()) {
                setItem(WATER, returnItem);
            }
            setChanged();
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    public boolean hasFluidForRecipe(TeapotRecipe recipe) {
        FluidStack baseFluid = recipe.getBaseFluid();
        return fluidContainer.hasFluid(baseFluid);
    }

    @Override
    public boolean hasFluid(FluidStack fluid) {
        return fluidContainer.hasFluid(fluid);
    }

    @Override
    public boolean extract(FluidStack fluid) {
        return fluidContainer.extract(fluid);
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(7);
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
    public void encodeData(CompoundTag compoundTag) {
        MenuInventoryHelper.encodeInventory(getItemHandler(), compoundTag);
        saveSharedData(compoundTag);
    }

    @Override
    public void decodeData(CompoundTag compoundTag) {
        MenuInventoryHelper.decodeInventory(getItemHandler(), compoundTag);
        loadSharedData(compoundTag);
    }

    private void saveSharedData(CompoundTag tag) {
        tag.put("fluids", fluidContainer.serialize());
        tag.putInt("cookingTime", cookingTime);
        tag.putInt("boilTime", waterBoilTime);
        tag.putFloat("temperature", temperature);
    }

    private void loadSharedData(CompoundTag tag) {
        fluidContainer.deserialize(tag.getCompound("fluids"));
        cookingTime = tag.getInt("cookingTime");
        waterBoilTime = tag.getInt("boilTime");
        temperature = tag.getFloat("temperature");
        reloadRecipe();
    }
}
