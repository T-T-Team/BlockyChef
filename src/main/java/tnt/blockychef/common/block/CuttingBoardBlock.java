package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.CuttingBoardBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.menu.CuttingBoardMenu;
import tnt.blockychef.util.Helper;
import tnt.blockychef.util.MenuInventoryHelper;

public class CuttingBoardBlock extends FullHorizontalAxisBlock implements EntityBlock {

    private static final VoxelShape HITBOX = Block.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0);
    public static final Component SCREEN_TITLE = Component.translatable("screen.blockychef.cutting_board");

    public CuttingBoardBlock() {
        super(Properties.of().sound(SoundType.WOOD).strength(2.0F));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return HITBOX;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockyChefBlockEntities.CUTTING_BOARD.create(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CuttingBoardBlockEntity cuttingBoard) {
            if (!level.isClientSide) {
                NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider(
                        (menuId, inventory, interactionPlayer) -> new CuttingBoardMenu(menuId, inventory, cuttingBoard),
                        SCREEN_TITLE
                ), pos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ?
                Helper.createBlockEntityTicker(type, BlockyChefBlockEntities.CUTTING_BOARD, CuttingBoardBlockEntity::tickClient) :
                Helper.createBlockEntityTicker(type, BlockyChefBlockEntities.CUTTING_BOARD, CuttingBoardBlockEntity::tickServer);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacementState, boolean flag) {
        MenuInventoryHelper.dropRecipeBlockInventoryContentsAndAwardExp(state, level, pos, replacementState);
        super.onRemove(state, level, pos, replacementState, flag);
    }
}
