package tnt.blockychef.integrations.waila;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import tnt.blockychef.common.block.RegrowingLogBlock;
import tnt.blockychef.common.block.TreeHangingFruitBlock;

public enum CustomGrowthProgressProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public ResourceLocation getUid() {
        return WailaIntegrationPlugin.GROWABLES;
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        BlockState state = accessor.getBlockState();
        Block block = state.getBlock();
        if (block instanceof TreeHangingFruitBlock fruit) {
            if (state.hasProperty(BlockStateProperties.AGE_4)) {
                int age = fruit.getAge(state);
                append(iTooltip, age / 4.0F);
            }
        } else if (block instanceof RegrowingLogBlock logBlock) {
            if (state.hasProperty(BlockStateProperties.AGE_3) && logBlock.isRegrowable(state)) {
                int age = logBlock.getRegrowthAge(state);
                append(iTooltip, age / 3.0F);
            }
        }
    }

    private static void append(ITooltip tooltip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F) {
            tooltip.add(Component.translatable("tooltip.jade.crop_growth", IThemeHelper.get().info(String.format("%.0f%%", growthValue))));
        } else {
            tooltip.add(Component.translatable("tooltip.jade.crop_growth", IThemeHelper.get().success(Component.translatable("tooltip.jade.crop_mature"))));
        }
    }
}
