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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.config.PlantDecay;

public class TreeHangingFruitBlock extends BushBlock implements BonemealableBlock {

    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    public static final VoxelShape[] SHAPES = {
            Block.box(6.5, 11.0, 6.5,  9.5, 16.0,  9.5),
            Block.box(6.5, 11.0, 6.5,  9.5, 16.0,  9.5),
            Block.box(5.0,  8.0, 5.0, 11.0, 16.0, 11.0),
            Block.box(5.0,  8.0, 5.0, 11.0, 16.0, 11.0),
            Block.box(4.0,  6.0, 4.0, 12.0, 16.0, 12.0)
    };

    public TreeHangingFruitBlock() {
        super(Properties.of().destroyTime(0.6F).sound(SoundType.CROP).randomTicks().noCollission());
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    public boolean isRipe(BlockState state) {
        return state.getValue(AGE) == 4;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(AGE)];
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
    public boolean isValidBonemealTarget(LevelReader reader, BlockPos pos, BlockState state) {
        return !this.isRipe(state);
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
        level.setBlock(pos, state.setValue(AGE, nextAge), Block.UPDATE_CLIENTS);
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
                level.setBlock(pos, state.setValue(AGE, 0), Block.UPDATE_CLIENTS);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!DecayingGrowingBlock.canTick(level, pos)) return;
        if (this.isRipe(state)) {
            PlantDecay decay = BlockyChef.config.decay;
            if (!level.isClientSide && random.nextFloat() < decay.treeFruitDecayChance) {
                if (decay.treeFruitDecayKillsPlant) {
                    level.destroyBlock(pos, decay.treeFruitDecayDropsFruit);
                } else {
                    if (decay.treeFruitDecayDropsFruit) {
                        dropResources(state, level, pos);
                    }
                    level.setBlock(pos, state.setValue(AGE, 0), Block.UPDATE_CLIENTS);
                }
            }
        } else {
            float growthChance = BlockyChef.config.plants.fruitGrowthChance;
            if (random.nextFloat() < growthChance) {
                int age = state.getValue(AGE);
                level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_CLIENTS);
            }
        }
    }
}
