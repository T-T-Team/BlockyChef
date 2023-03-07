package tnt.blockychef.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import tnt.blockychef.common.block.entity.IndexedColorHolder;
import tnt.blockychef.util.ColorHelper;

import javax.annotation.Nullable;

public abstract class DyeableBlock extends FullHorizontalAxisBlock {

    public DyeableBlock(Properties properties) {
        super(properties);
    }

    public abstract int getLayerIndexFromInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult);

    protected boolean allowColorMixing() {
        return true;
    }

    protected InteractionResult handleDefaultInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, ItemStack stack) {
        return InteractionResult.PASS;
    }

    @Override
    public final InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof DyeItem dyeItem) {
            DyeColor color = dyeItem.getDyeColor();
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof IndexedColorHolder colorHolder) {
                int layer = this.getLayerIndexFromInteraction(state, level, pos, player, hand, hitResult);
                ColorHelper.setColor(colorHolder, layer, color, this.allowColorMixing());
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return this.handleDefaultInteraction(state, level, pos, player, hand, hitResult, stack);
    }

    public static int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int layerIndex) {
        if (level == null || pos == null) {
            return 0xFFFFFF;
        }
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof IndexedColorHolder indexedColorHolder) {
            int color = indexedColorHolder.getColor(layerIndex);
            return color == Integer.MIN_VALUE ? 0xFFFFFF : color;
        }
        return 0xFFFFFF;
    }
}
