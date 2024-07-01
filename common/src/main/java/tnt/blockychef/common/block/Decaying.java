package tnt.blockychef.common.block;

import net.minecraft.world.level.block.state.BlockState;

public interface Decaying {

    int getCurrentDecay(BlockState state);

    int getMaxDecay(BlockState state);

    default String getTooltipBase() {
        return "tooltip.blockychef.decay_progress.default";
    }
}
