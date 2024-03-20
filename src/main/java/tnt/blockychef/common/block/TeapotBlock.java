package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.block.entity.TeapotBlockEntity;
import tnt.blockychef.common.data.fluids.FluidExtraction;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.menu.TeapotMenu;
import tnt.tntlib.api.blockentity.BlockEntityHelper;
import tnt.tntlib.api.menu.MenuInventoryHelper;

public class TeapotBlock extends FullHorizontalAxisBlock implements EntityBlock {

    private static final VoxelShape HITBOX = Block.box(4.0, 0.0, 4.0, 12.0, 6.0, 12.0);
    private static final Component TITLE = Component.translatable("screen.blockychef.teapot");

    public TeapotBlock() {
        super(Properties.of().sound(SoundType.STONE).strength(1.5F));
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
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof TeapotBlockEntity teapot) {
            ItemStack itemStack = pPlayer.getItemInHand(pHand);
            FluidExtraction extraction = BlockyChef.EXTRACTION_MANAGER.getExtractor(itemStack, teapot);
            if (extraction != null) {
                ItemStack result = extraction.extractFluid(teapot);
                if (!result.isEmpty()) {
                    CookingMastery.applyMastery(pPlayer, result);
                    MenuInventoryHelper.giveItemOrDrop(pPlayer, result);
                    if (!pPlayer.isCreative())
                        itemStack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
            if (!pLevel.isClientSide) {
                ((ServerPlayer) pPlayer).openMenu(new SimpleMenuProvider((menuId, inv, owner) -> new TeapotMenu(menuId, inv, teapot), TITLE), pPos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return BlockyChefBlockEntities.TEAPOT.create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockEntityHelper.createBlockEntityTicker(pBlockEntityType, BlockyChefBlockEntities.TEAPOT, TeapotBlockEntity::tick);
    }

    @Override
    public void animateTick(BlockState pState, Level level, BlockPos pos, RandomSource rand) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof TeapotBlockEntity teapot && teapot.isBurning()) {
            Vec3 vec = Vec3.atCenterOf(pos);
            double dx = rand.nextDouble() - rand.nextDouble();
            double dz = rand.nextDouble() - rand.nextDouble();
            float scale = 0.0625F;
            level.addParticle(ParticleTypes.SMOKE, vec.x, vec.y, vec.z, dx * scale, 0.05, dz * scale);
        }
    }
}
