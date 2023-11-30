package tnt.blockychef.common.heat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import tnt.blockychef.common.init.BlockyChefTags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class HeatHelper {

    private static final List<VanillaHeatSourceProvider> DEFAULT_HEAT_SOURCES = new ArrayList<>();

    static {
        DEFAULT_HEAT_SOURCES.add(new FluidHeatSourceProvider(BlockyChefTags.Fluids.HEATING_FLUIDS, HeatValues.HEATING_FLUID));
        DEFAULT_HEAT_SOURCES.add(new BlockHeatSourceProvider(BlockyChefTags.Blocks.WEAK_CONSTANT_HEAT, HeatValues.WEAK_HEAT));
        DEFAULT_HEAT_SOURCES.add(new BlockHeatSourceProvider(BlockyChefTags.Blocks.STRONG_CONSTANT_HEAT, HeatValues.STRONG_HEAT));
        DEFAULT_HEAT_SOURCES.add(new VariableBlockHeatSourceProvider(BlockyChefTags.Blocks.WEAK_VARIABLE_HEAT, HeatValues.WEAK_HEAT, BlockStateProperties.LIT));
        DEFAULT_HEAT_SOURCES.add(new VariableBlockHeatSourceProvider(BlockyChefTags.Blocks.STRONG_VARIABLE_HEAT, HeatValues.STRONG_HEAT, BlockStateProperties.LIT));
    }

    public static HeatSource getHeatSource(Level level, BlockPos pos, @Nullable Direction direction) {
        BlockPos position = direction != null ? pos.relative(direction) : pos;
        BlockState state = level.getBlockState(position);
        if (state.getBlock() instanceof HeatSourceProvider provider) {
            return provider.getHeatSourceAt(level, position, direction);
        }
        for (VanillaHeatSourceProvider vanillaHeatSourceProvider : DEFAULT_HEAT_SOURCES) {
            if (vanillaHeatSourceProvider.isValidSource(level, position, state)) {
                return vanillaHeatSourceProvider.getHeatSource();
            }
        }
        return NoHeatSource.INSTANCE;
    }

    public static float regulateHeat(float currentHeat, float setHeat, float speed) {
        if (currentHeat == setHeat) {
            return setHeat;
        }
        float diff = setHeat - currentHeat;
        if (Math.abs(diff) < speed) {
            return setHeat;
        }
        float adjust = setHeat < currentHeat ? (-speed * 1.2F) : speed;
        return currentHeat + adjust;
    }

    public static boolean isEmpty(HeatSource source) {
        return source == NoHeatSource.INSTANCE;
    }

    private interface VanillaHeatSourceProvider {

        boolean isValidSource(Level level, BlockPos pos, BlockState state);

        HeatSource getHeatSource();
    }

    private static class BlockHeatSourceProvider implements VanillaHeatSourceProvider {

        private final TagKey<Block> blockTagKey;
        private final HeatSource source;

        public BlockHeatSourceProvider(TagKey<Block> blockTagKey, HeatSource source) {
            this.blockTagKey = blockTagKey;
            this.source = source;
        }

        @Override
        public boolean isValidSource(Level level, BlockPos pos, BlockState state) {
            return state.is(this.blockTagKey);
        }

        @Override
        public HeatSource getHeatSource() {
            return source;
        }
    }

    private static class VariableBlockHeatSourceProvider extends BlockHeatSourceProvider {

        private final BooleanProperty property;

        public VariableBlockHeatSourceProvider(TagKey<Block> blockTagKey, HeatSource source, BooleanProperty property) {
            super(blockTagKey, source);
            this.property = property;
        }

        @Override
        public boolean isValidSource(Level level, BlockPos pos, BlockState state) {
            if (state.hasProperty(this.property) && state.getValue(this.property)) {
                return super.isValidSource(level, pos, state);
            }
            return false;
        }
    }

    private record FluidHeatSourceProvider(TagKey<Fluid> fluidTagKey, HeatSource source) implements VanillaHeatSourceProvider {

        @Override
        public boolean isValidSource(Level level, BlockPos pos, BlockState state) {
            FluidState fluidState = state.getFluidState();
            return !fluidState.isEmpty() && fluidState.isSource() && fluidState.is(this.fluidTagKey);
        }

        @Override
        public HeatSource getHeatSource() {
            return source;
        }
    }
}
