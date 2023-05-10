package tnt.blockychef.common.food.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class Fluid {

    private final FluidType fluidType;
    private int amount;

    public static final Codec<Fluid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidType.CODEC.fieldOf("fluid").forGetter(Fluid::getFluidType),
            Codec.INT.fieldOf("amount").forGetter(Fluid::getAmount)
    ).apply(instance, Fluid::new));

    public Fluid(FluidType fluidType, int amount) {
        this.fluidType = fluidType;
        this.amount = amount;
    }

    public FluidType getFluidType() {
        return fluidType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void insert(int amount) {
        this.amount += amount;
    }

    public void extract(int amount) {
        this.amount -= amount;
    }

    public boolean isEmpty() {
        return amount <= 0;
    }
}
