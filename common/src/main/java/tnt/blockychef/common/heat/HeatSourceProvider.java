package tnt.blockychef.common.heat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface HeatSourceProvider {

    HeatSource getHeatSourceAt(Level level, BlockPos pos, @Nullable Direction direction);
}
