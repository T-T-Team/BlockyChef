package tnt.blockychef.common.heat;

import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public interface HeatSource {

    float getHeat(@Nullable Direction direction);
}
