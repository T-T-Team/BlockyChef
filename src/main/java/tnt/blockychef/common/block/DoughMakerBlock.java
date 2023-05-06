package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.block.entity.DoughMakerBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.common.menu.DoughMakerMenu;
import tnt.blockychef.util.Helper;
import tnt.blockychef.util.MenuInventoryHelper;

public class DoughMakerBlock extends DyeableBlock implements EntityBlock {

    private static final VoxelShape HITBOX = Block.box(3.0, 0.0, 3.0, 13.0, 13.0, 13.0);
    private static final Component TITLE = Component.translatable("screen.blockychef.dough_maker");

    public DoughMakerBlock() {
        super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(3.0F).noOcclusion());
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return HITBOX;
    }

    @Override
    public int getLayerIndexFromInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return 0;
    }

    @Override
    protected InteractionResult handleDefaultInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, ItemStack stack) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DoughMakerBlockEntity doughMaker) {
            if (!level.isClientSide) {
                NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider(
                        (menuId, inv, owner) -> new DoughMakerMenu(menuId, inv, doughMaker),
                        TITLE
                ), pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacementState, boolean flag) {
        super.onRemove(state, level, pos, replacementState, flag);
        MenuInventoryHelper.dropRecipeBlockInventoryContentsAndAwardExp(state, level, pos, replacementState);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pose1, BlockState state) {
        return BlockyChefBlockEntities.DOUGH_MAKER.create(pose1, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return Helper.createBlockEntityTicker(type, BlockyChefBlockEntities.DOUGH_MAKER, DoughMakerBlockEntity::tick);
    }
}
