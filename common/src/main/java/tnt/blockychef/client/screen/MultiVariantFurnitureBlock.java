package tnt.blockychef.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tnt.blockychef.common.block.DyeableBlock;
import tnt.blockychef.common.block.entity.IndexedColorHolder;

import javax.annotation.Nullable;
import java.util.Locale;

public abstract class MultiVariantFurnitureBlock extends DyeableBlock {

    private final Variant variant;

    public MultiVariantFurnitureBlock(Properties properties, Variant variant) {
        super(properties);
        this.variant = variant;

        this.addExtraDescription(Component.translatable("label.blockychef.variant", variant.getLabel()).withStyle(ChatFormatting.GRAY));
    }

    public Variant getVariant() {
        return variant;
    }

    public static int getVariantColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int layerIndex) {
        int[] defaultColors = {};
        if (state.getBlock() instanceof MultiVariantFurnitureBlock variantBlock) {
            defaultColors = variantBlock.getVariant().defaultColors;
        }
        if (level == null || pos == null) {
            return layerIndex < defaultColors.length ? defaultColors[layerIndex] : 0xFFFFFF;
        }
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof IndexedColorHolder indexedColorHolder) {
            Integer color = indexedColorHolder.getColor(layerIndex);
            return color == null ? 0xFFFFFF : color;
        }
        return 0xFFFFFF;
    }

    public enum Variant {

        FULL_WOOD(new int[] { 0xC39A61, 0xC39A61 }),
        FULL_CONCRETE(new int[] { 0xC6C6C6, 0xC6C6C6 }),
        MIXED_WOOD_TOP(new int[] { 0xC39A61, 0xC6C6C6 }),
        MIXED_CONCRETE_TOP(new int[] { 0xC6C6C6, 0xC39A61 });

        private final int[] defaultColors;
        private final Component label;

        Variant(int[] defaultColors) {
            this.defaultColors = defaultColors;
            this.label = Component.translatable("label.blockychef.variant.furniture." + name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.YELLOW);
        }

        public int[] getDefaultColors() {
            return defaultColors;
        }

        public Component getLabel() {
            return label;
        }
    }
}
