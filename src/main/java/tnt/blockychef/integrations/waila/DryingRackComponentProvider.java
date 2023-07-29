package tnt.blockychef.integrations.waila;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.ProgressArrowElement;
import tnt.blockychef.common.block.entity.DryingRackBlockEntity;
import tnt.blockychef.common.food.recipe.DryingRecipe;

public enum DryingRackComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        CompoundTag tag = blockAccessor.getServerData();
        if (tag.contains("time")) {
            int time = tag.getInt("time");
            int total = tag.getInt("total");
            if (time <= 0) {
                return;
            }
            tooltip.add(Component.translatable("label.blockychef.drying", (total - time) / 20));
            if (tag.contains("input")) {
                IElementHelper helper = IElementHelper.get();
                ItemStack input = ItemStack.of(tag.getCompound("input"));
                ItemStack output = ItemStack.of(tag.getCompound("output"));
                tooltip.add(helper.item(input));
                tooltip.append(new ProgressArrowElement(time / (float) total));
                tooltip.append(helper.item(output));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        DryingRackBlockEntity blockEntity = (DryingRackBlockEntity) accessor.getBlockEntity();
        if (blockEntity.hasItem()) {
            compoundTag.putInt("time", blockEntity.getTicksDrying());
            compoundTag.putInt("total", blockEntity.getTotalTime());
            DryingRecipe recipe = blockEntity.getRecipe();
            if (recipe != null) {
                compoundTag.put("input", blockEntity.getItem(0).save(new CompoundTag()));
                compoundTag.put("output", recipe.getOutput().serializeNBT());
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return WailaIntegrationPlugin.DRYING_RACK;
    }
}
