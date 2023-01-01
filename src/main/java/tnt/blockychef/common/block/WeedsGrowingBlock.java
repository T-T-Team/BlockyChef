package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.Registry;

public class WeedsGrowingBlock extends BushBlock {

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[] {
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };
    public static final IntegerProperty WEEDS_AGE = IntegerProperty.create("weeds", 0, 4);

    public WeedsGrowingBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.createDefaultState(this.stateDefinition.any()));
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
        builder.add(WeedsGrowingBlock.WEEDS_AGE);
    }

    @Override
    public boolean canBeReplaced(BlockState p_60470_, BlockPlaceContext p_60471_) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (!areWeedsMaxAge(state)) {
            if (random.nextFloat() < BlockyChef.config.weeds.weedsGrowthChance) { // Weeds growth chance
                int age = state.getValue(WeedsGrowingBlock.WEEDS_AGE);
                level.setBlock(pos, state.setValue(WeedsGrowingBlock.WEEDS_AGE, age + 1), 2);
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return ItemStack.EMPTY;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        int weedsAge = state.getValue(WEEDS_AGE);
        if (stack.getItem() instanceof HoeItem && weedsAge > 0) {
            if (!level.isClientSide) {
                stack.hurtAndBreak(weedsAge, player, p -> p.broadcastBreakEvent(hand));
            }
            trimWeeds(pos, state, level);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    protected BlockState createDefaultState(BlockState base) {
        return base.setValue(WEEDS_AGE, 0);
    }

    public static void trimWeeds(BlockPos pos, BlockState state, Level level) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(WEEDS_AGE, 0), 2);
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else {
            level.addDestroyBlockEffect(pos, Blocks.GRASS.defaultBlockState());
        }
    }

    public static boolean areWeedsMaxAge(BlockState state) {
        return state.getValue(WeedsGrowingBlock.WEEDS_AGE) == 4;
    }
}
