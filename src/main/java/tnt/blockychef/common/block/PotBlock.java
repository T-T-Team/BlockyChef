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
import tnt.blockychef.common.block.entity.PotBlockEntity;
import tnt.blockychef.common.block.entity.RecipeRememberingBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.menu.PotMenu;
import tnt.tntlib.api.blockentity.BlockEntityHelper;

public class PotBlock extends FullHorizontalAxisBlock implements EntityBlock {

    private static final VoxelShape HITBOX = Block.box(4.0, 0.0, 4.0, 12.0, 7.0, 12.0);
    private static final Component TITLE = Component.translatable("screen.blockychef.pot");

    public PotBlock() {
        super(Properties.of().sound(SoundType.STONE).strength(1.5F).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return HITBOX;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacementState, boolean flag) {
        RecipeRememberingBlockEntity.dropRecipeBlockInventoryContentsAndAwardExp(state, level, pos, replacementState);
        super.onRemove(state, level, pos, replacementState, flag);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof PotBlockEntity pot && !pLevel.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) pPlayer, new SimpleMenuProvider((menuId, inv, owner) -> new PotMenu(menuId, inv, pot), TITLE), pPos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockEntityHelper.createBlockEntityTicker(pBlockEntityType, BlockyChefBlockEntities.POT, PotBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return BlockyChefBlockEntities.POT.create(pPos, pState);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof PotBlockEntity pot) {
            if (pot.getCookingStatus().isBurning()) {
                Vec3 vec3 = Vec3.atCenterOf(pPos);
                pLevel.addParticle(ParticleTypes.SMOKE, vec3.x, vec3.y, vec3.z, 0.0, 0.1, 0.0);
            }
        }
    }
}
