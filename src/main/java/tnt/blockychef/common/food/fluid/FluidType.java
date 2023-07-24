package tnt.blockychef.common.food.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tnt.blockychef.common.registry.Registry;

public final class FluidType {

    public static final Codec<FluidType> CODEC = ResourceLocation.CODEC.comapFlatMap(location -> {
        FluidType fluidType = Registry.FLUID.get().getValue(location);
        return fluidType != null ? DataResult.success(fluidType) : DataResult.error(() -> "Unknown fluid type: " + location);
    }, fluidType -> Registry.FLUID.get().getKey(fluidType));

    private final ResourceLocation identifier;
    private final int fluidColor;
    private final int fluidDensity;
    private final Component text;

    public FluidType(ResourceLocation identifier, int fluidColor) {
        this(identifier, fluidColor, 0);
    }

    public FluidType(ResourceLocation identifier, int fluidColor, int fluidDensity) {
        this.identifier = identifier;
        this.fluidColor = fluidColor;
        this.fluidDensity = fluidDensity;
        this.text = Component.translatable("crafting.fluid." + identifier.toString().replaceAll(":", "."));
    }

    public Component getComponent() {
        return text;
    }

    public ResourceLocation getFluidIdentifier() {
        return identifier;
    }

    public int fluidColor() {
        return fluidColor;
    }

    public int fluidDensity() {
        return fluidDensity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FluidType type = (FluidType) o;
        return getFluidIdentifier().equals(type.getFluidIdentifier());
    }

    @Override
    public int hashCode() {
        return getFluidIdentifier().hashCode();
    }

    @Override
    public String toString() {
        return String.format("FluidType[%s]", getFluidIdentifier());
    }
}
