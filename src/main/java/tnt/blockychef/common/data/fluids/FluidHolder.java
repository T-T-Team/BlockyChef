package tnt.blockychef.common.data.fluids;

import net.minecraftforge.fluids.FluidStack;

public interface FluidHolder {

    boolean hasFluid(FluidStack fluid);

    boolean extract(FluidStack fluid);
}
