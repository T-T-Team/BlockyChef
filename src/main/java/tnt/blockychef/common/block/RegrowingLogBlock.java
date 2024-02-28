package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import tnt.blockychef.BlockyChef;

public class RegrowingLogBlock extends RotatedPillarBlock implements BonemealableBlock {

    public static final BooleanProperty REGROWABLE = BooleanProperty.create("regrowable");
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    public RegrowingLogBlock(Properties pProperties) {
        super(pProperties.randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(REGROWABLE, this.isDefaultRegrowable()).setValue(AGE, this.getDefaultAge()));
    }

    public boolean hasRegrown(BlockState state) {
        return state.getValue(AGE) == 3;
    }

    public boolean canRegrow(BlockState state) {
        return state.getValue(REGROWABLE) && !hasRegrown(state);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
        return !this.hasRegrown(pState);
    }

    @Override
    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        int age = pState.getValue(AGE);
        int nextAge = age + 1;
        if (nextAge > 3) {
            nextAge = 3;
        }
        pLevel.setBlock(pPos, pState.setValue(AGE, nextAge), Block.UPDATE_CLIENTS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(REGROWABLE, AGE);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!DecayingGrowingBlock.canTick(level, pos)) return;
        if (this.canRegrow(state)) {
            float chance = this.getRegrowthChance(level, pos, state);
            if (!level.isClientSide() && random.nextFloat() < chance) {
                this.grow(state, level, pos, random);
            }
        }
    }

    protected void grow(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int currentAge = state.getValue(AGE);
        level.setBlock(pos, state.setValue(AGE, currentAge + 1), Block.UPDATE_CLIENTS);
    }

    protected float getRegrowthChance(ServerLevel level, BlockPos pos, BlockState state) {
        return BlockyChef.config.plants.logRegrowChance;
    }

    protected boolean isDefaultRegrowable() {
        return false;
    }

    protected int getDefaultAge() {
        return 0;
    }
}
