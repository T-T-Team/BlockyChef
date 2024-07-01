package tnt.blockychef.common.heat;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public record SimpleHeatSource(float value, Direction... directions) implements HeatSource {

    @Override
    public float getHeat(@Nullable Direction direction) {
        if (direction != null) {
            for (Direction dir : directions) {
                if (dir == direction) {
                    return value;
                }
            }
        }
        return 0.0F;
    }
}
