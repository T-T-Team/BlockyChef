package tnt.blockychef.common.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public final class EdibleFluidType extends FluidType {

    private static final ResourceLocation WATER_STILL = new ResourceLocation("block/water");
    private static final ResourceLocation WATER_FLOWING = new ResourceLocation("block/water_flow");

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final int fluidColor;

    public EdibleFluidType(int fluidColor) {
        this(fluidColor, WATER_STILL, WATER_FLOWING);
    }

    public EdibleFluidType(int fluidColor, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        super(Properties.create());
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.fluidColor = fluidColor;
    }

    public ResourceLocation getStillTexture() {
        return stillTexture;
    }

    public ResourceLocation getFlowingTexture() {
        return flowingTexture;
    }

    public int getFluidColor() {
        return fluidColor;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public int getTintColor() {
                return fluidColor;
            }

            @Override
            public ResourceLocation getStillTexture() {
                return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }
        });
    }
}
