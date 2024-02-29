package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;
import tnt.blockychef.BlockyChef;

public class DecayingGrowingBlock extends BushBlock implements Decaying {

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[] {
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };
    public static final IntegerProperty WEEDS_AGE = IntegerProperty.create("weeds", 0, 4);

    public DecayingGrowingBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(createDefaultState(stateDefinition.any()));
    }

    @Override
    public int getCurrentDecay(BlockState state) {
        return state.getValue(WEEDS_AGE);
    }

    @Override
    public int getMaxDecay(BlockState state) {
        return this.getMaxDecayValue();
    }

    @Override
    public String getTooltipBase() {
        return "tooltip.blockychef.decay_progress.crops";
    }

    public IntegerProperty getDecayProperty() {
        return WEEDS_AGE;
    }

    public int getMaxDecayValue() {
        return 4;
    }

    protected void decay(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (this.canDecay(level, pos, state, random)) {
            this.tryDecay(level, pos, state, random);
        }
    }

    protected boolean canDecay(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        return !this.isFullyDecayed(state);
    }

    protected void tryDecay(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (random.nextFloat() < this.getDecayGrowthChance(level, pos, state, random)) {
            int decay = state.getValue(this.getDecayProperty());
            level.setBlock(pos, state.setValue(this.getDecayProperty(), decay + 1), 2);
        }
    }

    protected float getDecayGrowthChance(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        return BlockyChef.config.decay.plantDecayProgressChance;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(this.getDecayProperty())];
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
        builder.add(this.getDecayProperty());
    }

    @Override
    public boolean canBeReplaced(BlockState p_60470_, BlockPlaceContext p_60471_) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        this.decay(level, pos, state, random);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return ItemStack.EMPTY;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        int weedsAge = state.getValue(this.getDecayProperty());
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
        return base.setValue(this.getDecayProperty(), 0);
    }

    public static void trimWeeds(BlockPos pos, BlockState state, Level level) {
        if (!(state.getBlock() instanceof DecayingGrowingBlock block)) {
            return;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(block.getDecayProperty(), 0), 2);
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else {
            level.addDestroyBlockEffect(pos, Blocks.GRASS.defaultBlockState());
        }
    }

    public static boolean canTick(Level level, BlockPos pos) {
        return level.isAreaLoaded(pos, 1);
    }

    public boolean isFullyDecayed(BlockState state) {
        return state.getValue(this.getDecayProperty()) == this.getMaxDecayValue();
    }
}
