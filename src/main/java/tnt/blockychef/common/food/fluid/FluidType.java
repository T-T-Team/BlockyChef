package tnt.blockychef.common.food.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;
import tnt.blockychef.common.registry.Registry;

public record FluidType(int fluidColor, int fluidDensity) {

    public static final Codec<FluidType> CODEC = ResourceLocation.CODEC.comapFlatMap(location -> {
        FluidType fluidType = Registry.FLUID.get().getValue(location);
        return fluidType != null ? DataResult.success(fluidType) : DataResult.error(() -> "Unknown fluid type: " + location);
    }, fluidType -> Registry.FLUID.get().getKey(fluidType));

    public FluidType(int fluidColor) {
        this(fluidColor, 1);
    }
}
