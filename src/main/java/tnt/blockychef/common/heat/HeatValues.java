package tnt.blockychef.common.heat;

import net.minecraft.core.Direction;

public final class HeatValues {

    public static final HeatSource HEATING_FLUID = new SimpleHeatSource(5.0F, Direction.UP);
    public static final HeatSource WEAK_HEAT = new SimpleHeatSource(1.0F, Direction.UP);
    public static final HeatSource STRONG_HEAT = new SimpleHeatSource(3.0F, Direction.UP);
}
