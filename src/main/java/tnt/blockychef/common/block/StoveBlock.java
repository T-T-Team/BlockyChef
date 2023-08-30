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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.StoveBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.menu.StoveMenu;
import tnt.tntlib.api.blockentity.BlockEntityHelper;

public class StoveBlock extends DyeableBlock implements EntityBlock {

    public static final Component TITLE = Component.translatable("screen.blockychef.stove");

    public StoveBlock() {
        super(Properties.of().sound(SoundType.STONE).strength(3.0F).noOcclusion());
    }

    @Override
    protected InteractionResult handleDefaultInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, ItemStack stack) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof StoveBlockEntity stove) {
                NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider((id, inv, owner) -> new StoveMenu(id, inv, stove), TITLE), pos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public int getLayerIndexFromInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pose1, BlockState state) {
        return BlockyChefBlockEntities.STOVE.create(pose1, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockEntityHelper.createBlockEntityTicker(pBlockEntityType, BlockyChefBlockEntities.STOVE, StoveBlockEntity::tick);
    }
}
