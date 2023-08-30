package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.food.recipe.StoveRecipe;
import tnt.blockychef.common.heat.HeatSource;
import tnt.blockychef.common.heat.HeatSourceProvider;
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

public class StoveBlockEntity extends RecipeRememberingBlockEntity<StoveRecipe> implements Synchronizable, IndexedColorHolder, HeatSourceProvider {

    public static final int ENERGY_BUFFER_SIZE = 1000;
    public static final int TEMPERATURE_LIMIT = 10;
    public static final int[] FUEL = {6};
    public static final int[] INPUTS = {0, 1, 2, 3, 4, 5};

    private final RegulatedRangeHeatSource stoveHeatSource;
    private final RegulatedRangeHeatSource externalHeatSource;
    private final CookingSlot[] slots;

    private int energyBuffer;
    private boolean stoveActive;
    private boolean externalActive;
    private float temperature;
    private int[] colors;

    public StoveBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.STOVE, pos, state);
        RegulationHandler stoveHandler = new StoveRegulationHandler(this::handleStoveHeatEvent);
        RegulationHandler externalHandler = new StoveRegulationHandler(this::handleExternalHeatEvent);
        this.stoveHeatSource = new RegulatedRangeHeatSource(stoveHandler, 0, TEMPERATURE_LIMIT);
        this.externalHeatSource = new RegulatedRangeHeatSource(externalHandler, 0, TEMPERATURE_LIMIT);
        this.slots = ArrayUtils.indexedFill(new CookingSlot[INPUTS.length], CookingSlot::new);
        this.colors = new int[1];
        Arrays.fill(this.colors, Integer.MIN_VALUE);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, StoveBlockEntity stove) {
        // Fuel slot tick
        ItemStack fuelStack = stove.getItem(FUEL[0]);
        if (!fuelStack.isEmpty() && stove.shouldReplenishEnergyBuffer()) {
            int burnTime = ForgeHooks.getBurnTime(fuelStack, null);
            stove.energyBuffer += burnTime;
            fuelStack.shrink(1);
            BlockEntityHelper.sendBlockEntityClientData(stove);
        }

        // Cooking tick
        if (stove.hasEnergy()) {
            boolean cooking = false;
            for (CookingSlot slot : stove.slots) {
                if (slot.updateSlot()) {
                    cooking = true;
                }
            }
            if (cooking) {
                stove.consumeEnergy();
            }
        }
    }

    public void refreshSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < slots.length) {
            slots[slotIndex].loadRecipe(level.getRecipeManager());
        }
    }

    @Override
    public HeatSource getHeatSourceAt(Level level, BlockPos pos, @Nullable Direction direction) {
        return direction == null ? stoveHeatSource : externalHeatSource;
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
        int consumption = Helper.sum(1, stoveActive, externalActive);
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
        this.energyBuffer = Mth.clamp(value, 0, ENERGY_BUFFER_SIZE);
    }

    public float getEnergyBufferValue() {
        return 1.0F - (energyBuffer / (float) ENERGY_BUFFER_SIZE);
    }

    public float getHeatAmount() {
        return 1.0F - (temperature / TEMPERATURE_LIMIT);
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

    public CookingSlot[] getSlots() {
        return slots;
    }

    private void saveSharedData(CompoundTag tag) {
        tag.putIntArray("colors", colors);
        tag.put("stoveHeat", stoveHeatSource.encodeData());
        tag.put("externalHeat", externalHeatSource.encodeData());
        tag.putInt("energyBuffer", energyBuffer);
        tag.putBoolean("stoveOn", stoveActive);
        tag.putBoolean("externalOn", externalActive);
        tag.putFloat("temperature", temperature);
        ListTag slots = new ListTag();
        Arrays.stream(this.slots)
                .map(CookingSlot::serialize)
                .forEach(slots::add);
        tag.put("cookingSlots", slots);
    }

    private void loadSharedData(CompoundTag tag) {
        colors = tag.getIntArray("colors");
        stoveHeatSource.decodeData(tag.getCompound("stoveHeat"));
        externalHeatSource.decodeData(tag.getCompound("externalHeat"));
        energyBuffer = tag.getInt("energyBuffer");
        stoveActive = tag.getBoolean("stoveOn");
        externalActive = tag.getBoolean("externalOn");
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
        float f = source.getRaw() + stepSize;
        source.set(f, decreased);
    }

    public final class CookingSlot {

        private final int slotIndex;
        private int progressionTimer;
        private int totalTimer;
        private float burnAmount;
        private StoveRecipe recipe;

        public CookingSlot(int slotIndex) {
            this.slotIndex = slotIndex;
        }

        public void loadRecipe(RecipeManager manager) {
            ItemStack input = getItem();
            StoveRecipe stoveRecipe = Helper.findRecipeFor(manager, BlockyChefRecipeTypes.STOVE_RECIPE, rec -> rec.matches(input))
                    .orElse(null);
            if (stoveRecipe == null || recipe != stoveRecipe) {
                progressionTimer = 0;
                totalTimer = 0;
                burnAmount = 0;
                recipe = stoveRecipe;
            }
            if (recipe != null) {
                totalTimer = recipe.getConfiguration().time();
            }
            BlockEntityHelper.sendBlockEntityClientData(StoveBlockEntity.this);
        }

        public boolean isLocked() {
            return recipe != null && !recipe.isOvercooking() && BlockyChef.config.cooking.lockCookingSlots;
        }

        public boolean updateSlot() {
            ItemStack stack = this.getItem();
            if (stack.isEmpty() || recipe == null)
                return false;
            StoveRecipe.CookingConfiguration configuration = recipe.getConfiguration();
            float temp = StoveBlockEntity.this.temperature;
            if (configuration.isCooking(temp)) {
                if (configuration.isBurning(temp)) {
                    float temperatureDifference = temp - configuration.maxTemperature();
                    float burnScale = temperatureDifference * (0.015F * configuration.burnSpeed());
                    if ((burnAmount += burnScale) >= 1.0F) {
                        ItemStack burntResult = recipe.getBurntResult().copy();
                        StoveBlockEntity.this.setItem(getSlotIndex(), burntResult);
                        loadRecipe(StoveBlockEntity.this.level.getRecipeManager());
                        return true;
                    }
                }
                if (++progressionTimer >= totalTimer) {
                    ItemStack result = recipe.getResult().copy();
                    StoveBlockEntity stove = StoveBlockEntity.this;
                    stove.setItem(getSlotIndex(), result);
                    stove.storeRecipe(recipe);
                    loadRecipe(StoveBlockEntity.this.level.getRecipeManager());
                }
            }
            return true;
        }

        public float getProgress() {
            return progressionTimer / (float) totalTimer;
        }

        public float getBurnProgress() {
            return burnAmount;
        }

        public ItemStack getItem() {
            return StoveBlockEntity.this.getItem(getSlotIndex());
        }

        public int getSlotIndex() {
            return this.slotIndex;
        }

        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("progression", progressionTimer);
            tag.putInt("total", totalTimer);
            tag.putFloat("burn", burnAmount);
            if (recipe != null) {
                tag.putString("recipeId", recipe.getId().toString());
            }
            return tag;
        }

        public void deserialize(CompoundTag tag) {
            progressionTimer = tag.getInt("progression");
            totalTimer = tag.getInt("total");
            burnAmount = tag.getFloat("burn");

            StoveBlockEntity be = StoveBlockEntity.this;
            if (be.level != null && tag.contains("recipeId")) {
                recipe = Helper.findRecipeByIdFor(be.level.getRecipeManager(), BlockyChefRecipeTypes.STOVE_RECIPE, new ResourceLocation(tag.getString("recipeId")))
                        .orElse(null);
            }
        }
    }

    private record StoveRegulationHandler(RegulationEvent event) implements RegulationHandler {

        @Override
        public boolean isToggleable() {
            return true;
        }

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
