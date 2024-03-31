package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.GrillBlockEntity;
import tnt.blockychef.common.food.CookingStatus;
import tnt.blockychef.common.heat.HeatSource;
import tnt.blockychef.common.heat.HeatSourceProvider;
import tnt.blockychef.common.heat.NoHeatSource;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.menu.GrillMenu;
import tnt.tntlib.api.TNTUtils;
import tnt.tntlib.api.blockentity.BlockEntityHelper;

public class GrillBlock extends DyeableBlock implements EntityBlock, HeatSourceProvider {

    private static final VoxelShape HITBOX = Block.box(2.0, 0.0, 2.0, 14.0, 15.0, 14.0);
    private static final Component TITLE = Component.translatable("screen.blockychef.grill");

    public GrillBlock() {
        super(Properties.of().sound(SoundType.STONE).strength(1.5F).noOcclusion());
    }

    @Override
    public HeatSource getHeatSourceAt(Level level, BlockPos pos, @Nullable Direction direction) {
        if (direction != null)
            return NoHeatSource.INSTANCE;
        BlockEntity entity = level.getBlockEntity(pos);
        return entity instanceof GrillBlockEntity grill ? grill.getHeatSource() : NoHeatSource.INSTANCE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return HITBOX;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return pDirection == Direction.DOWN && !pState.canSurvive(pLevel, pPos) ? Blocks.AIR.defaultBlockState() : pState;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos below = pPos.relative(Direction.DOWN);
        BlockState base = pLevel.getBlockState(below);
        return base.isFaceSturdy(pLevel, pPos, Direction.UP);
    }

    @Override
    public int getLayerIndexFromInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return 0;
    }

    @Override
    protected InteractionResult handleDefaultInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, ItemStack stack) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof GrillBlockEntity grill) {
            if (!level.isClientSide()) {
                ((ServerPlayer) player).openMenu(
                        new SimpleMenuProvider(
                                (menuId, inv, owner) -> new GrillMenu(menuId, inv, grill),
                                TITLE
                        ),
                        pos
                );
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos1, BlockState state) {
        return BlockyChefBlockEntities.GRILL.create(pos1, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockEntityHelper.createBlockEntityTicker(pBlockEntityType, BlockyChefBlockEntities.GRILL, GrillBlockEntity::tick);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof GrillBlockEntity grill) {
            CookingStatus status = grill.getCookingStatus();
            Vec3 pos = Vec3.atCenterOf(pPos);
            if (grill.hasFuel()) {
                pLevel.addParticle(ParticleTypes.FLAME, pos.x + TNTUtils.randomRange(pRandom, 0.35F), pos.y + 0.45, pos.z + TNTUtils.randomRange(pRandom, 0.35F), 0.0F, 0.01F, 0.0F);
                if (pRandom.nextDouble() < 0.1) {
                    pLevel.playLocalSound(pos.x, pos.y, pos.z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                }
            }
            if (status != CookingStatus.NONE) {
                pLevel.addParticle(ParticleTypes.SMOKE, pos.x, pos.y + 0.45, pos.z, 0.0F, 0.05F, 0.0F);
            }
            if (status.isBurning()) {
                pLevel.addParticle(ParticleTypes.LARGE_SMOKE, pos.x, pos.y + 0.45, pos.z, 0.0F, 0.05F, 0.0F);
            }
        }
    }
}
