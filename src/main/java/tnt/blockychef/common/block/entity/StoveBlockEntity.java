package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.recipe.CookingConfiguration;
import tnt.blockychef.common.food.recipe.StoveRecipe;
import tnt.blockychef.common.heat.HeatHelper;
import tnt.blockychef.common.heat.HeatValues;
import tnt.blockychef.common.heat.RegulatedRangeHeatSource;
import tnt.blockychef.common.heat.RegulationHandler;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.util.Helper;
import tnt.tntlib.api.ArrayUtils;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;

import java.util.Arrays;
import java.util.Optional;

public class StoveBlockEntity extends RecipeRememberingBlockEntity<StoveRecipe> implements Synchronizable, IndexedColorHolder {

    public static final int ENERGY_BUFFER_SIZE = 1600;
    public static final int[] FUEL = {6};
    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5};

    private final RegulatedRangeHeatSource stoveHeatSource;
    private final RegulatedRangeHeatSource externalHeatSource;
    private final StoveCookingSlot[] slots;

    private int energyBuffer;
    private float temperature;
    private int[] colors;

    public StoveBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.STOVE, pos, state);
        RegulationHandler stoveHandler = new StoveRegulationHandler(this::handleStoveHeatEvent);
        RegulationHandler externalHandler = new StoveRegulationHandler(this::handleExternalHeatEvent);
        this.stoveHeatSource = new RegulatedRangeHeatSource(stoveHandler, 0, HeatValues.MAX_TEMPERATURE);
        this.externalHeatSource = new RegulatedRangeHeatSource(externalHandler, 0, HeatValues.MAX_TEMPERATURE);
        this.slots = ArrayUtils.indexedFill(new StoveCookingSlot[INPUTS.length], index -> new StoveCookingSlot(index, this));
        this.colors = new int[1];
        Arrays.fill(this.colors, Integer.MIN_VALUE);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, StoveBlockEntity stove) {
        // Temperature tick
        stove.temperature = HeatHelper.regulateHeat(stove.temperature, stove.stoveHeatSource.getHeat(), 0.01F);
        // Fuel slot tick
        ItemStack fuelStack = stove.getItem(FUEL[0]);
        if (!fuelStack.isEmpty() && stove.shouldReplenishEnergyBuffer()) {
            int burnTime = ForgeHooks.getBurnTime(fuelStack, null);
            stove.energyBuffer += burnTime;
            fuelStack.shrink(1);
            BlockEntityHelper.sendBlockEntityClientData(stove);
        }

        // Cooking tick
        if (stove.canCook()) {
            for (StoveCookingSlot slot : stove.slots) {
                slot.updateSlot();
            }
            if (level.getGameTime() % 2L == 0L) {
                stove.consumeEnergy();
            }
        }
    }

    public void refreshSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < slots.length) {
            slots[slotIndex].loadRecipe(level.getRecipeManager());
        }
        setChanged();
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(FUEL.length + INPUTS.length);
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

    public void consumeEnergy() {
        int consumption = Helper.sum(1, stoveHeatSource.isProducingHeat(), externalHeatSource.isProducingHeat());
        setEnergy(energyBuffer - consumption);
    }

    public boolean hasEnergy() {
        return energyBuffer > 0;
    }

    public int getStoredEnergyAmount() {
        return energyBuffer;
    }

    public float getActualTemperature() {
        return temperature;
    }

    public boolean shouldReplenishEnergyBuffer() {
        return !hasEnergy() || energyBuffer <= 2;
    }

    public void setEnergy(int value) {
        this.energyBuffer = value;
    }

    public float getEnergyBufferValue() {
        return 1.0F - (Math.min(1.0F, energyBuffer / (float) ENERGY_BUFFER_SIZE));
    }

    public float getHeatAmount() {
        return 1.0F - (temperature / HeatValues.MAX_TEMPERATURE);
    }

    public boolean canCook() {
        return hasEnergy() && temperature > 0.0F;
    }

    public RegulatedRangeHeatSource getStoveHeatSource() {
        return stoveHeatSource;
    }

    public RegulatedRangeHeatSource getExternalHeatSource() {
        return externalHeatSource;
    }

    @Override
    public void encodeData(CompoundTag tag) {
        MenuInventoryHelper.encodeInventory(getItemHandler(), tag);
        saveSharedData(tag);
    }

    @Override
    public void decodeData(CompoundTag tag) {
        MenuInventoryHelper.decodeInventory(getItemHandler(), tag);
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

    public StoveCookingSlot[] getSlots() {
        return slots;
    }

    private void saveSharedData(CompoundTag tag) {
        tag.putIntArray("colors", colors);
        tag.put("stoveHeat", stoveHeatSource.encodeData());
        tag.put("externalHeat", externalHeatSource.encodeData());
        tag.putInt("energyBuffer", energyBuffer);
        tag.putFloat("temperature", temperature);
        ListTag slots = new ListTag();
        Arrays.stream(this.slots)
                .map(StoveCookingSlot::serialize)
                .forEach(slots::add);
        tag.put("cookingSlots", slots);
    }

    private void loadSharedData(CompoundTag tag) {
        colors = tag.getIntArray("colors");
        stoveHeatSource.decodeData(tag.getCompound("stoveHeat"));
        externalHeatSource.decodeData(tag.getCompound("externalHeat"));
        energyBuffer = tag.getInt("energyBuffer");
        temperature = tag.getFloat("temperature");
        ListTag list = tag.getList("cookingSlots", Tag.TAG_COMPOUND);
        for (int i = 0; i < Math.min(this.slots.length, list.size()); i++) {
            slots[i].deserialize(list.getCompound(i));
        }
    }

    private void handleStoveHeatEvent(boolean decreased) {
        handleHeatEvent(stoveHeatSource, decreased);
    }

    private void handleExternalHeatEvent(boolean decreased) {
        handleHeatEvent(externalHeatSource, decreased);
    }

    private void handleHeatEvent(RegulatedRangeHeatSource source, boolean decreased) {
        float stepSize = 0.5F;
        if (decreased) {
            stepSize = -stepSize;
        }
        float f = source.getHeat() + stepSize;
        source.set(f, decreased);
        setChanged();
    }

    public final class StoveCookingSlot extends CookingSlot<StoveRecipe, StoveBlockEntity> {

        public StoveCookingSlot(int slotIndex, StoveBlockEntity blockEntity) {
            super(slotIndex, blockEntity);
        }

        @Override
        public RecipeType<StoveRecipe> getRecipeType() {
            return BlockyChefRecipeTypes.STOVE_RECIPE;
        }

        @Override
        public Optional<RecipeHolder<StoveRecipe>> getRecipe(RecipeManager manager, ItemStack input) {
            return Helper.findRecipeFor(manager, getRecipeType(), r -> r.value().matches(input));
        }

        @Override
        protected void recipeLoaded(RecipeHolder<StoveRecipe> recipe) {
            totalTimer = recipe.value().getConfiguration().time();
        }

        public boolean isLocked() {
            return StoveBlockEntity.this.canCook() && recipe != null && !recipe.value().isOvercooking() && BlockyChef.config.cooking.lockCookingSlots;
        }

        public void updateSlot() {
            ItemStack stack = this.getItem();
            if (stack.isEmpty() || recipe == null)
                return;
            CookingConfiguration configuration = recipe.value().getConfiguration();
            float temp = StoveBlockEntity.this.temperature;
            if (configuration.isCooking(temp)) {
                if (configuration.isBurning(temp)) {
                    float temperatureDifference = temp - configuration.maxTemperature();
                    float burnScale = temperatureDifference * (0.015F * configuration.burnSpeed());
                    if ((burnAmount += burnScale) >= 1.0F) {
                        ItemStack burntResult = recipe.value().getBurntResult().copy();
                        StoveBlockEntity.this.setItem(getSlotIndex(), burntResult);
                        loadRecipe(StoveBlockEntity.this.level.getRecipeManager());
                        return;
                    }
                }
                if (++progressionTimer >= totalTimer) {
                    ItemStack result = recipe.value().getResult().copy();
                    StoveBlockEntity stove = StoveBlockEntity.this;
                    stove.setItem(getSlotIndex(), result);
                    stove.storeRecipe(recipe);
                    loadRecipe(StoveBlockEntity.this.level.getRecipeManager());
                }
            }
        }
    }

    private record StoveRegulationHandler(RegulationEvent event) implements RegulationHandler {

        @Override
        public void decrease() {
            event.changed(true);
        }

        @Override
        public void increase() {
            event.changed(false);
        }
    }

    @FunctionalInterface
    private interface RegulationEvent {
        void changed(boolean decrease);
    }
}
