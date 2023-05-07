package tnt.blockychef.common.food.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FluidValue(Fluid fluid, int amount) {

    public static final Codec<FluidValue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Fluid.CODEC.fieldOf("fluid").forGetter(FluidValue::fluid),
            Codec.INT.fieldOf("amount").forGetter(FluidValue::amount)
    ).apply(instance, FluidValue::new));
}
