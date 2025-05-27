package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.RecipeRememberingBlockEntity;
import tnt.blockychef.common.block.entity.SaucepanBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.menu.SaucepanMenu;
import tnt.tntlib.api.blockentity.BlockEntityHelper;

public class SaucepanBlock extends FullHorizontalAxisBlock implements EntityBlock {

    private static final VoxelShape HITBOX = Block.box(4.0, 0.0, 4.0, 12.0, 3.0, 12.0);
    private static final Component TITLE = Component.translatable("screen.blockychef.saucepan");

    public SaucepanBlock() {
        super(Properties.of().sound(SoundType.STONE).strength(1.5F).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return HITBOX;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof SaucepanBlockEntity saucepan) {
            if (!pLevel.isClientSide()) {
                NetworkHooks.openScreen(
                        (ServerPlayer) pPlayer,
                        new SimpleMenuProvider(
                                (menuId, inv, owner) -> new SaucepanMenu(menuId, inv, saucepan),
                                TITLE
                        ),
                        pPos
                );
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockEntityHelper.createBlockEntityTicker(pBlockEntityType, BlockyChefBlockEntities.SAUCEPAN, SaucepanBlockEntity::tick);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacementState, boolean flag) {
        RecipeRememberingBlockEntity.dropRecipeBlockInventoryContentsAndAwardExp(state, level, pos, replacementState);
        super.onRemove(state, level, pos, replacementState, flag);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SaucepanBlockEntity(pPos, pState);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof SaucepanBlockEntity saucepan) {
            if (saucepan.getCookingStatus().isBurning()) {
                Vec3 vec = Vec3.atBottomCenterOf(pPos);
                pLevel.addParticle(ParticleTypes.SMOKE, vec.x, vec.y + 0.15, vec.z, 0.0, 0.1, 0.0);
            }
        }
    }
}
