package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.KitchenCabinetBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.menu.KitchenCabinetMenu;
import tnt.tntlib.api.menu.MenuInventoryHelper;

public class KitchenCabinetBlock extends DyeableBlock implements EntityBlock {

    private static final Component TITLE = Component.translatable("screen.blockychef.kitchen_cabinet");
    private static final VoxelShape[] HITBOX = {
            Block.box(0.0, 2.0, 9.0, 16.0, 16.0, 16.0), // NORTH
            Block.box(0.0, 2.0, 0.0, 16.0, 16.0, 7.0), // SOUTH
            Block.box(9.0, 2.0, 0.0, 16.0, 16.0, 16.0), // WEST
            Block.box(0.0, 2.0, 0.0, 7.0, 16.0, 16.0)  // EAST
    };

    public KitchenCabinetBlock() {
        super(Properties.of().sound(SoundType.STONE).strength(1.5F).noOcclusion());
    }

    @Override
    protected InteractionResult handleDefaultInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, ItemStack stack) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof KitchenCabinetBlockEntity cabinetBlockEntity && !level.isClientSide) {
            ((ServerPlayer) player).openMenu(new SimpleMenuProvider(
                    (menuId, inv, owner) -> new KitchenCabinetMenu(menuId, inv, cabinetBlockEntity),
                    TITLE
            ), pos);
            level.playSound(null, pos, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel.getBlockEntity(pPos) instanceof KitchenCabinetBlockEntity entity) {
            MenuInventoryHelper.dropInventoryContents(pLevel, pPos, entity.getItemHandler());
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).ordinal() - 2;
        return HITBOX[index];
    }

    @Override
    public int getLayerIndexFromInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockyChefBlockEntities.KITCHEN_CABINET.create(pos, state);
    }
}
