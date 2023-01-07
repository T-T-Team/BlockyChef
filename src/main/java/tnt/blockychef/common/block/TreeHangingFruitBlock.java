package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import tnt.blockychef.BlockyChef;

public class TreeHangingFruitBlock extends BushBlock implements BonemealableBlock {

    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;

    public TreeHangingFruitBlock() {
        super(Properties.of(Material.PLANT).destroyTime(0.6F).sound(SoundType.CROP).randomTicks().noCollission());
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    public boolean isRipe(BlockState state) {
        return state.getValue(AGE) == 2;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter getter, BlockPos pos) {
        return state.is(BlockTags.LEAVES);
    }

    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        BlockPos blockpos = pos.above();
        if (state.getBlock() == this)
            return reader.getBlockState(blockpos).canSustainPlant(reader, blockpos, Direction.DOWN, this);
        return this.mayPlaceOn(reader.getBlockState(blockpos), reader, blockpos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader reader, BlockPos pos, BlockState state, boolean clientside) {
        return !BlockyChef.config.crops.restrictBonemealUsage && !this.isRipe(state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        int nextAge = age + 1;
        if (nextAge > 2) {
            nextAge = 2;
        }
        level.setBlock(pos, state.setValue(AGE, nextAge), 2);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (this.isRipe(state)) {
                dropResources(state, level, pos);
                level.setBlock(pos, state.setValue(AGE, 0), 2);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!DecayingGrowingBlock.canTick(level, pos)) return;
        if (this.isRipe(state)) {
            if (random.nextFloat() < 0.1F) {
                level.setBlock(pos, state.setValue(AGE, 0), 2);
                level.destroyBlock(pos, true);
            }
        } else {
            float growthChance = 0.05F;
            if (random.nextFloat() < growthChance) {
                int age = state.getValue(AGE);
                level.setBlock(pos, state.setValue(AGE, age + 1), 2);
            }
        }
    }
}
