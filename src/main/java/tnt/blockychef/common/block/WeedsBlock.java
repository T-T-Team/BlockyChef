package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;

public class WeedsBlock extends BushBlock {

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[] {
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };
    public static final float WEEDS_GROWTH = 0.025F;
    public static final IntegerProperty WEEDS_AGE = IntegerProperty.create("weeds", 0, 4);

    public WeedsBlock() {
        super(Properties.of(Material.REPLACEABLE_PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP));
        this.registerDefaultState(this.stateDefinition.any().setValue(WeedsBlock.WEEDS_AGE, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(WEEDS_AGE)];
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter getter, BlockPos pos) {
        return state.is(Blocks.FARMLAND);
    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.CROP;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WeedsBlock.WEEDS_AGE);
    }

    @Override
    public boolean canBeReplaced(BlockState p_60470_, BlockPlaceContext p_60471_) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (!areWeedsMaxAge(state)) {
            if (random.nextFloat() < WEEDS_GROWTH) { // Weeds growth chance
                int age = state.getValue(WeedsBlock.WEEDS_AGE);
                level.setBlock(pos, state.setValue(WeedsBlock.WEEDS_AGE, age + 1), 2);
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        return (reader.getRawBrightness(pos, 0) >= 8 || reader.canSeeSky(pos)) && super.canSurvive(state, reader, pos);
    }

    public static boolean areWeedsMaxAge(BlockState state) {
        return state.getValue(WeedsBlock.WEEDS_AGE) == 4;
    }
}
