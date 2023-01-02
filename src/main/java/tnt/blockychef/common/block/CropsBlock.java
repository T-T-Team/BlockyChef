package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.Registry;

import java.util.function.UnaryOperator;

public class CropsBlock extends WeedsGrowingBlock implements BonemealableBlock {

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[] {
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    private final SeedProvider seedProvider;

    public CropsBlock(SeedProvider provider) {
        this(provider, UnaryOperator.identity());
    }

    public CropsBlock(SeedProvider provider, UnaryOperator<Properties> propertyExtender) {
        this(provider, propertyExtender.apply(Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
    }

    protected CropsBlock(SeedProvider provider, Properties properties) {
        super(properties);
        this.seedProvider = provider;
    }

    @Override
    protected BlockState createDefaultState(BlockState base) {
        return super.createDefaultState(base).setValue(AGE, 0);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        BlockState oldState = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (oldState.getBlock() instanceof WeedsGrowingBlock) {
            int age = oldState.getValue(WEEDS_AGE);
            state = state.setValue(WEEDS_AGE, age);
        }
        return state;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(AGE)];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
    public boolean canBeReplaced(BlockState p_60470_, BlockPlaceContext p_60471_) {
        return false;
    }

    public int getMaxAge() {
        return 7;
    }

    protected int getAge(BlockState state) {
        return state.getValue(AGE);
    }

    public boolean isMaxAge(BlockState p_52308_) {
        return p_52308_.getValue(AGE) >= this.getMaxAge();
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.playerDestroy(level, player, pos, state, blockEntity, stack);
        if (BlockyChef.config.weeds.placeOnRawFarmland) {
            if (level.getBlockState(pos.below()).is(Blocks.FARMLAND)) {
                level.setBlock(pos, Registry.WEEDS.defaultBlockState().setValue(WEEDS_AGE, state.getValue(WEEDS_AGE)), 2);
            }
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                float f = getGrowthSpeed(this, level, pos);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
                    level.setBlock(pos, state.setValue(AGE, i + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(level, pos, state);
                }
            }
        }
        if (areWeedsMaxAge(state)) {
            float destroyChance = BlockyChef.config.weeds.weedsCropKillChance;
            if (random.nextFloat() < destroyChance) {
                level.destroyBlock(pos, false);
            }
        } else if (random.nextFloat() < BlockyChef.config.weeds.weedsGrowthChance) { // Weeds growth chance
            int age = state.getValue(WEEDS_AGE);
            level.setBlock(pos, state.setValue(WEEDS_AGE, age + 1), 2);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter getter, BlockPos pos, BlockState state) {
        return new ItemStack(this.getBaseSeedId());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader reader, BlockPos pos, BlockState state, boolean flag) {
        return !BlockyChef.config.crops.restrictBonemealUsage && !this.isMaxAge(state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource source, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource source, BlockPos pos, BlockState state) {
        this.growCrops(level, pos, state);
    }

    public void growCrops(Level level, BlockPos pos, BlockState state) {
        int targetAge = this.getAge(state) + this.getBonemealAgeIncrease(level);
        int maxAge = this.getMaxAge();
        if (targetAge > maxAge) {
            targetAge = maxAge;
        }
        level.setBlock(pos, state.setValue(AGE, targetAge), 2);
    }

    protected int getBonemealAgeIncrease(Level p_52262_) {
        return Mth.nextInt(p_52262_.random, 1, 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        return (reader.getRawBrightness(pos, 0) >= 8 || reader.canSeeSky(pos)) && super.canSurvive(state, reader, pos);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof Ravager && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, entity)) {
            level.destroyBlock(pos, true, entity);
        }

        super.entityInside(state, level, pos, entity);
    }

    protected ItemLike getBaseSeedId() {
        return this.seedProvider.getSeedItem();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(this.getBaseSeedId());
    }

    protected static float getGrowthSpeed(Block block, BlockGetter blockGetter, BlockPos pos) {
        float f = 1.0F;
        BlockPos blockpos = pos.below();
        for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
                float f1 = 0.0F;
                BlockState blockstate = blockGetter.getBlockState(blockpos.offset(i, 0, j));
                if (blockstate.canSustainPlant(blockGetter, blockpos.offset(i, 0, j), net.minecraft.core.Direction.UP, (net.minecraftforge.common.IPlantable) block)) {
                    f1 = 1.0F;
                    if (blockstate.isFertile(blockGetter, pos.offset(i, 0, j))) {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }
        BlockPos blockpos1 = pos.north();
        BlockPos blockpos2 = pos.south();
        BlockPos blockpos3 = pos.west();
        BlockPos blockpos4 = pos.east();
        boolean flag = blockGetter.getBlockState(blockpos3).is(block) || blockGetter.getBlockState(blockpos4).is(block);
        boolean flag1 = blockGetter.getBlockState(blockpos1).is(block) || blockGetter.getBlockState(blockpos2).is(block);
        if (flag && flag1) {
            f /= 2.0F;
        } else {
            boolean flag2 = blockGetter.getBlockState(blockpos3.north()).is(block) || blockGetter.getBlockState(blockpos4.north()).is(block) || blockGetter.getBlockState(blockpos4.south()).is(block) || blockGetter.getBlockState(blockpos3.south()).is(block);
            if (flag2) {
                f /= 2.0F;
            }
        }
        return f;
    }

    @FunctionalInterface
    public interface SeedProvider {
        ItemLike getSeedItem();
    }
}
