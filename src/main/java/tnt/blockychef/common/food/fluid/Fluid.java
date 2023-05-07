package tnt.blockychef.common.food.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;
import tnt.blockychef.common.registry.Registry;

public record Fluid(int fluidColor) {

    public static final Codec<Fluid> CODEC = ResourceLocation.CODEC.comapFlatMap(location -> {
        Fluid fluid = Registry.FLUID.get().getValue(location);
        return fluid != null ? DataResult.success(fluid) : DataResult.error(() -> "Unknown fluid type: " + location);
    }, fluid -> Registry.FLUID.get().getKey(fluid));
}
