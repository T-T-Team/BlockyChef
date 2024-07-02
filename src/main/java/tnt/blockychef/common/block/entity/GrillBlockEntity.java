package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
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
import tnt.blockychef.common.food.CookingStatus;
import tnt.blockychef.common.food.recipe.GrillRecipe;
import tnt.blockychef.common.heat.HeatHelper;
import tnt.blockychef.common.heat.HeatValues;
import tnt.blockychef.common.heat.RegulatedRangeHeatSource;
import tnt.blockychef.common.heat.RegulationHandler;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefSounds;
import tnt.blockychef.common.init.BlockyChefTags;
import tnt.blockychef.util.Helper;
import tnt.tntlib.api.ArrayUtils;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;
import tnt.tntlib.api.menu.MenuInventoryHelper;
import tnt.tntlib.api.serialization.NbtUtil;

import javax.annotation.Nullable;
import java.util.Optional;

public class GrillBlockEntity extends RecipeRememberingBlockEntity<GrillRecipe> implements Synchronizable, IndexedColorHolder, ApplianceEventConsumer {

    private final GrillSlot[] slots;
    private final Integer[] colors;
    private final RegulatedRangeHeatSource heatSource;
    private int fuelAmount;
    private float temperature;

    public GrillBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.GRILL, pos, state);
        this.colors = new Integer[1];
        this.slots = ArrayUtils.indexedFill(new GrillSlot[6], i -> new GrillSlot(i + 1, this));
        this.heatSource = new RegulatedRangeHeatSource(new GrillHeatRegulator(), 0.0F, HeatValues.MAX_TEMPERATURE);
        this.heatSource.addHeatingCondition(this::hasFuel);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GrillBlockEntity grill) {
        float heat = grill.heatSource.getHeat(null);
        grill.temperature = HeatHelper.regulateHeat(grill.temperature, heat, 0.02F);

        // fuel tick
        boolean isActive = grill.heatSource.getConfiguredHeat(null) > 0.0F;
        if (grill.fuelAmount > 0) {
            --grill.fuelAmount;
        }
        if (isActive) {
            // fuel restore
            if (grill.fuelAmount <= 0 && !level.isClientSide()) {
                int amount = grill.getFuelAmountFromInput();
                if (amount > 0) {
                    grill.setFuelAmount(grill.fuelAmount + amount);
                    grill.consumeFuel();
                }
            }
        }

        // Grill progress tick
        if (!grill.canGrill())
            return;
        for (GrillSlot slot : grill.slots) {
            slot.updateSlot();
        }
        CookingStatus status = grill.getCookingStatus();
        if (status != CookingStatus.NONE && level.getGameTime() % 50L == 0) {
            level.playSound(null, pos, BlockyChefSounds.GRILL, SoundSource.BLOCKS, 0.4F, 1.0F);
        }
    }

    public CookingStatus getCookingStatus() {
        return getCookingStatus(slots, slot -> slot.status);
    }

    @Override
    public void onEvent(Player eventOrigin, int eventId) {
        if (eventId >= 0 && eventId < slots.length) {
            GrillSlot slot = slots[eventId];
            slot.flip();
            setChanged();
            BlockEntityHelper.sendBlockEntityClientData(this);
        }
    }

    public RegulatedRangeHeatSource getHeatSource() {
        return heatSource;
    }

    public int getFuel() {
        return fuelAmount;
    }

    public float getTemperature() {
        return temperature;
    }

    public boolean canFlip(int index) {
        if (index >= 0 && index < slots.length) {
            return slots[index].canFlip();
        }
        return false;
    }

    public boolean canGrill() {
        return temperature > 0F;
    }

    public void consumeFuel() {
        ItemStack itemStack = this.getItem(0);
        if (!itemStack.isEmpty()) {
            itemStack.shrink(1);
            setChanged();
        }
    }

    public int getFuelAmountFromInput() {
        ItemStack itemStack = this.getItem(0);
        return !itemStack.isEmpty() && itemStack.is(BlockyChefTags.Items.GRILL_FUEL) ? ForgeHooks.getBurnTime(itemStack, null) : 0;
    }

    public void setFuelAmount(int amount) {
        this.fuelAmount = Math.max(0, amount);
        setChanged();
        BlockEntityHelper.sendBlockEntityClientData(this);
    }

    private boolean shouldInitiateCooking() {
        for (GrillSlot slot : slots) {
            if (slot.shouldCook())
                return true;
        }
        return false;
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(7);
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

    public float getProgression(int slot, boolean flipSide) {
        if (slot >= 0 && slot < slots.length) {
            return slots[slot].getProgress(flipSide);
        }
        return 0.0F;
    }

    public float getBurnProgression(int slot, boolean flipSide) {
        if (slot >= 0 && slot < slots.length) {
            return slots[slot].getBurnProgress(flipSide);
        }
        return 0.0F;
    }

    public boolean hasFuel() {
        return fuelAmount > 0;
    }

    public GrillSlot[] getSlots() {
        return slots;
    }

    public void refreshSlot(int index) {
        if (index >= 1 && index < 7) {
            slots[index - 1].loadRecipe(level.getRecipeManager());
        }
        setChanged();
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

    private void saveSharedData(CompoundTag tag) {
        ColorableBlockEntity.saveColorData(colors, tag);
        tag.put("slots", NbtUtil.arrayToNbt(slots, GrillSlot::serialize));
        tag.put("heatSource", heatSource.encodeData());
        tag.putInt("fuelAmount", fuelAmount);
        tag.putFloat("temperature", temperature);
    }

    private void loadSharedData(CompoundTag tag) {
        ColorableBlockEntity.loadColorData(colors, tag);
        GrillSlot[] grillSlots = NbtUtil.arrayFromNbt(slots, tag.getList("slots", Tag.TAG_COMPOUND), (slot, nbt) -> {
            slot.deserialize(nbt);
            return slot;
        }, CompoundTag.class);
        System.arraycopy(grillSlots, 0, slots, 0, slots.length);
        heatSource.decodeData(tag.getCompound("heatSource"));
        fuelAmount = tag.getInt("fuelAmount");
        temperature = tag.getFloat("temperature");
    }

    private void handleHeatEvent(RegulatedRangeHeatSource source, boolean decreased, float amount) {
        if (decreased) {
            amount = -amount;
        }
        float f = source.getConfiguredHeat(null) + amount;
        source.set(f, decreased);
        BlockEntityHelper.sendBlockEntityClientData(this);
        setChanged();
    }

    private final class GrillHeatRegulator implements RegulationHandler {

        @Override
        public void increase(float amount) {
            handleHeatEvent(GrillBlockEntity.this.heatSource, false, amount);
        }

        @Override
        public void decrease(float amount) {
            handleHeatEvent(GrillBlockEntity.this.heatSource, true, amount);
        }
    }

    public class GrillSlot extends CookingSlot<GrillRecipe, GrillBlockEntity> {

        protected boolean flipped;
        protected int flippedProgressionTimer;
        protected float flippedBurnAmount;
        private CookingStatus status = CookingStatus.NONE;

        public GrillSlot(int slotIndex, GrillBlockEntity blockEntity) {
            super(slotIndex, blockEntity);
        }

        public void updateSlot() {
            status = CookingStatus.NONE;
            ItemStack itemStack = this.getItem();
            if (itemStack.isEmpty() || recipe == null) {
                resetState();
                recipe = null;
                return;
            }
            GrillRecipe.GrillingConfiguration conf = recipe.value().getConfiguration();
            float temperature = GrillBlockEntity.this.temperature;
            if (conf.isCooking(temperature)) {
                status = CookingStatus.COOKING;
                int progress = this.getProgressAmount();
                if (conf.isBurning(temperature) || progress >= totalTimer) {
                    status = CookingStatus.BURNING;
                    float f = 0.0F;
                    if (conf.withinMinMaxTemperature(temperature)) {
                        f = 0.01F * conf.burnSpeed();
                    } else if (conf.overMaxTemperature(temperature)) {
                        f = 0.01F + HeatHelper.burn(temperature, conf.maxTemperature(), conf.burnSpeed());
                    }
                    float newBurnAmount = this.getBurnAmount(flipped) + f;
                    this.setBurnAmount(newBurnAmount, flipped);
                    if (newBurnAmount >= 1.0F) {
                        ItemStack burntResult = recipe.value().getBurnResult().copy();
                        GrillBlockEntity.this.setItem(getSlotIndex(), burntResult);
                        this.resetState();
                        loadRecipe(GrillBlockEntity.this.level.getRecipeManager());
                        return;
                    }
                } else if (recipe.value().isOvercooked()) {
                    status = CookingStatus.BURNING;
                }
                float oppositeBurn = this.getBurnAmount(!flipped);
                if (oppositeBurn > 0) {
                    oppositeBurn = Math.max(0.0F, oppositeBurn - 0.01F * conf.burnSpeed());
                    this.setBurnAmount(oppositeBurn, !flipped);
                }
                int newProgress = progress + 1;
                this.setProgressAmount(newProgress);
                if (this.areBothSidesDone() || (recipe.value().isOvercooked() && this.isEitherSideDone())) {
                    ItemStack result = recipe.value().getResult().copy();
                    GrillBlockEntity grill = GrillBlockEntity.this;
                    grill.setItem(getSlotIndex(), result);
                    grill.storeRecipe(recipe);
                    this.resetState();
                    loadRecipe(grill.level.getRecipeManager());
                }
            }
        }

        public boolean isSlotLocked() {
            return GrillBlockEntity.this.canGrill() && recipe != null && !recipe.value().isOvercooked() && BlockyChef.config.cooking.lockCookingSlots;
        }

        public boolean areBothSidesDone() {
            return progressionTimer >= totalTimer && flippedProgressionTimer >= totalTimer;
        }

        public boolean isEitherSideDone() {
            return progressionTimer >= totalTimer || flippedProgressionTimer >= totalTimer;
        }

        public float getProgress(boolean flipSide) {
            if (recipe != null && recipe.value().isOvercooked()) {
                return 0.0F;
            }
            return (flipSide ? flippedProgressionTimer : progressionTimer) / (float) totalTimer;
        }

        public float getBurnProgress(boolean flipSide) {
            if (recipe != null && recipe.value().isOvercooked()) {
                return (flipSide ? flippedProgressionTimer : progressionTimer) / (float) totalTimer;
            }
            return (flipSide ? flippedBurnAmount : burnAmount);
        }

        public void flip() {
            this.flipped = !flipped;
        }

        public boolean canFlip() {
            return !this.getItem().isEmpty() && recipe != null;
        }

        public boolean shouldCook() {
            return this.recipe != null;
        }

        public int getProgressAmount() {
            return flipped ? flippedProgressionTimer : progressionTimer;
        }

        public void setProgressAmount(int amount) {
            if (flipped)
                flippedProgressionTimer = amount;
            else
                progressionTimer = amount;
            GrillBlockEntity.this.setChanged();
        }

        public float getBurnAmount(boolean flippedSide) {
            return flippedSide ? flippedBurnAmount : burnAmount;
        }

        public void setBurnAmount(float amount, boolean flippedSide) {
            if (flippedSide)
                flippedBurnAmount = amount;
            else
                burnAmount = amount;
            GrillBlockEntity.this.setChanged();
        }

        private void resetState() {
            this.flipped = false;
            this.progressionTimer = 0;
            this.flippedProgressionTimer = 0;
            this.flippedBurnAmount = 0.0F;
            this.burnAmount = 0.0F;
            GrillBlockEntity.this.setChanged();
        }

        @Override
        public RecipeType<GrillRecipe> getRecipeType() {
            return BlockyChefRecipeTypes.GRILL_RECIPE;
        }

        @Override
        public Optional<RecipeHolder<GrillRecipe>> getRecipe(RecipeManager manager, ItemStack input) {
            return Helper.findRecipeFor(manager, this.getRecipeType(), recipe -> recipe.value().matches(input));
        }

        @Override
        protected void recipeLoaded(RecipeHolder<GrillRecipe> recipe, boolean updated) {
            totalTimer = recipe.value().getConfiguration().time();
            if (updated) {
                resetState();
            }
        }

        @Override
        public CompoundTag serialize() {
            CompoundTag tag = super.serialize();
            tag.putInt("flippedProgress", flippedProgressionTimer);
            tag.putFloat("flippedBurnAmount", flippedBurnAmount);
            tag.putBoolean("flipped", flipped);
            return tag;
        }

        @Override
        public void deserialize(CompoundTag tag) {
            super.deserialize(tag);
            flippedProgressionTimer = tag.getInt("flippedProgress");
            flippedBurnAmount = tag.getFloat("flippedBurnAmount");
            flipped = tag.getBoolean("flipped");
        }
    }
}
