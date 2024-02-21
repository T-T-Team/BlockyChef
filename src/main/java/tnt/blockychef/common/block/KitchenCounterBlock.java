package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.KitchenCounterBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.menu.KitchenCounterMenu;
import tnt.tntlib.api.menu.MenuInventoryHelper;

public class KitchenCounterBlock extends DyeableBlock implements EntityBlock {

    private static final Component TITLE = Component.translatable("screen.blockychef.kitchen_counter");

    public KitchenCounterBlock() {
        super(Properties.of().sound(SoundType.STONE).strength(1.5F).noOcclusion());
    }

    @Override
    protected InteractionResult handleDefaultInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, ItemStack stack) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof KitchenCounterBlockEntity counterBlockEntity && !level.isClientSide) {
            ((ServerPlayer) player).openMenu(new SimpleMenuProvider(
                    (menuId, inv, owner) -> new KitchenCounterMenu(menuId, inv, counterBlockEntity),
                    TITLE
            ), pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel.getBlockEntity(pPos) instanceof KitchenCounterBlockEntity entity) {
            MenuInventoryHelper.dropInventoryContents(pLevel, pPos, entity.getItemHandler());
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public int getLayerIndexFromInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Vec3 vec = hitResult.getLocation();
        double y = vec.y - pos.getY();
        return y >= 0.85 ? 0 : 1;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockyChefBlockEntities.KITCHEN_COUNTER.create(pos, state);
    }
}
