package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tnt.blockychef.common.heat.RegulatedRangeHeatSource;
import tnt.blockychef.common.heat.RegulationHandler;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.util.Helper;
import tnt.tntlib.api.ArrayUtils;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.blockentity.Synchronizable;

import java.util.Arrays;

public class StoveBlockEntity extends BlockEntity implements Synchronizable, IndexedColorHolder {

    public static final int ENERGY_BUFFER_SIZE = 300;
    public static final int[] FUEL = {0};
    public static final int[] INPUTS = {1, 2, 3, 4, 5, 6};

    private final RegulatedRangeHeatSource stoveHeatSource;
    private final RegulatedRangeHeatSource externalHeatSource;
    private final CookingSlot[] slots;

    private int energyBuffer;
    private boolean stoveActive;
    private boolean externalActive;
    private int[] colors;

    public StoveBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.STOVE, pos, state);
        RegulationHandler stoveHandler = new StoveRegulationHandler(this::handleStoveHeatEvent);
        RegulationHandler externalHandler = new StoveRegulationHandler(this::handleExternalHeatEvent);
        this.stoveHeatSource = new RegulatedRangeHeatSource(stoveHandler, 160.0F, 350.0F);
        this.externalHeatSource = new RegulatedRangeHeatSource(externalHandler, 80.0F, 200.0F);
        this.slots = ArrayUtils.indexedFill(new CookingSlot[INPUTS.length], CookingSlot::new);
        this.colors = new int[1];
        Arrays.fill(this.colors, Integer.MIN_VALUE);
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

    public void setEnergy(int value) {
        this.energyBuffer = Mth.clamp(value, 0, ENERGY_BUFFER_SIZE);
    }

    public float getEnergyBufferValue() {
        return energyBuffer / (float) ENERGY_BUFFER_SIZE;
    }

    @Override
    public void encodeData(CompoundTag tag) {
        saveSharedData(tag);
    }

    @Override
    public void decodeData(CompoundTag tag) {
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
        tag.putIntArray("colors", colors);
        tag.put("stoveHeat", stoveHeatSource.encodeData());
        tag.put("externalHeat", externalHeatSource.encodeData());
        tag.putInt("energyBuffer", energyBuffer);
        tag.putBoolean("stoveOn", stoveActive);
        tag.putBoolean("externalOn", externalActive);
    }

    private void loadSharedData(CompoundTag tag) {
        colors = tag.getIntArray("colors");
        stoveHeatSource.decodeData(tag.getCompound("stoveHeat"));
        externalHeatSource.decodeData(tag.getCompound("externalHeat"));
        energyBuffer = tag.getInt("energyBuffer");
        stoveActive = tag.getBoolean("stoveOn");
        externalActive = tag.getBoolean("externalOn");
    }

    private void handleStoveHeatEvent(boolean decreased) {
        handleHeatEvent(stoveHeatSource, decreased);
    }

    private void handleExternalHeatEvent(boolean decreased) {
        handleHeatEvent(externalHeatSource, decreased);
    }

    private void handleHeatEvent(RegulatedRangeHeatSource source, boolean decreased) {
        float stepSize = 10.0F;
        float f = source.getRaw() + stepSize;
        source.set(f, decreased);
    }

    private final class CookingSlot {

        private final int slotIndex;
        private int progressionTimer;
        private int totalTimer;
        private float burnAmount;

        public CookingSlot(int slotIndex) {
            this.slotIndex = slotIndex;
        }

        public void updateSlot() {

        }

        public float getProgress() {
            return progressionTimer / (float) totalTimer;
        }

        public float getBurnProgress() {
            return burnAmount;
        }

        public ItemStack getItem() {
            // TODO
            return ItemStack.EMPTY;
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
