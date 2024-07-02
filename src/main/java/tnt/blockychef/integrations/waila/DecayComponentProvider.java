package tnt.blockychef.integrations.waila;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import tnt.blockychef.common.block.Decaying;

public enum DecayComponentProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public ResourceLocation getUid() {
        return WailaIntegrationPlugin.DECAYABLES;
    }


    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockState state = blockAccessor.getBlockState();
        Block block = blockAccessor.getBlock();
        if (block instanceof Decaying decaying) {
            float decay = decaying.getCurrentDecay(state);
            float max = decaying.getMaxDecay(state);
            append(iTooltip, decay / max, decaying);
        }
    }

    @Override
    public int getDefaultPriority() {
        return 1;
    }

    private static void append(ITooltip tooltip, float decayValue, Decaying decaying) {
        String progressTooltip = decaying.getTooltipBase();
        decayValue *= 100.0F;
        if (decayValue < 100.0F) {
            IThemeHelper theme = IThemeHelper.get();
            MutableComponent component = decayValue == 0.0F ? theme.info(String.format("%.0f%%", decayValue)) : theme.danger(String.format("%.0f%%", decayValue));
            tooltip.add(Component.translatable(progressTooltip, component));
        } else {
            tooltip.add(Component.translatable(progressTooltip, IThemeHelper.get().failure(Component.translatable("tooltip.blockychef.decay_max"))));
        }
    }
}
