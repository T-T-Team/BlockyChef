package tnt.blockychef.common.heat;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class RegulatedRangeHeatSource implements RegulatedHeatSource {

    private final RegulationHandler regulationHandler;
    private final float minValue;
    private final float maxValue;
    private float amount;

    public RegulatedRangeHeatSource(RegulationHandler regulationHandler, float minValue, float maxValue) {
        this.regulationHandler = regulationHandler;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public boolean isProducingHeat() {
        return amount > 0;
    }

    @Override
    public float getHeat(@Nullable Direction direction) {
        return amount;
    }

    public float getHeat() {
        return amount;
    }

    public void set(float amount, boolean decrease) {
        if (decrease && amount < minValue) {
            this.amount = 0;
            return;
        }
        this.amount = Mth.clamp(amount, minValue, maxValue);
    }

    @Override
    public RegulationHandler getRegulationHandler() {
        return regulationHandler;
    }

    public CompoundTag encodeData() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("amount", amount);
        return tag;
    }

    public void decodeData(CompoundTag data) {
        amount = data.getFloat("amount");
    }
}
