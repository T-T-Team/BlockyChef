package tnt.blockychef.common.heat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface HeatSourceProvider {

    HeatSource getHeatSourceAt(Level level, BlockPos pos);
}
