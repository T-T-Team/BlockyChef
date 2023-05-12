package tnt.blockychef.common.data.fluids;

import tnt.blockychef.common.food.fluid.Fluid;

public interface FluidHolder {

    boolean hasFluid(Fluid fluid);

    boolean extract(Fluid fluid);
}
