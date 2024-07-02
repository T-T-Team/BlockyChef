package tnt.blockychef.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import tnt.blockychef.common.block.entity.IndexedColorHolder;
import tnt.blockychef.util.ColorHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class DyeableBlock extends FullHorizontalAxisBlock {

    public static final Component TEXT_DYEABLE = Component.translatable("label.blockychef.dyeable").withStyle(ChatFormatting.BLUE);

    private final List<Component> extraLabels = new ArrayList<>();

    public DyeableBlock(Properties properties) {
        super(properties);
    }

    public void addExtraDescription(List<Component> extraLabels) {
        this.extraLabels.addAll(extraLabels);
    }

    public void addExtraDescription(Component extraLabel) {
        this.extraLabels.add(extraLabel);
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
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof IndexedColorHolder colorHolder) {
            int layer = this.getLayerIndexFromInteraction(state, level, pos, player, hand, hitResult);
            if (stack.getItem() instanceof DyeItem dyeItem) {
                DyeColor color = dyeItem.getDyeColor();
                ColorHelper.setColor(colorHolder, layer, color, this.allowColorMixing());
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            } else if (stack.getItem() == Items.BRUSH) {
                colorHolder.setColor(layer, null);
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                if (!player.isCreative() && !level.isClientSide()) {
                    stack.hurt(1, level.getRandom(), (ServerPlayer) player);
                }
                level.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS);
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
            Integer color = indexedColorHolder.getColor(layerIndex);
            return color == null ? 0xFFFFFF : color;
        }
        return 0xFFFFFF;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.addAll(this.extraLabels);
        pTooltip.add(TEXT_DYEABLE);
    }
}
