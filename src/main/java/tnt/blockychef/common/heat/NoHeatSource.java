package tnt.blockychef.common.heat;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public final class NoHeatSource implements HeatSource {

    public static final HeatSource INSTANCE = new NoHeatSource();

    @Override
    public float getHeat(@Nullable Direction direction) {
        return 0.0F;
    }
}
