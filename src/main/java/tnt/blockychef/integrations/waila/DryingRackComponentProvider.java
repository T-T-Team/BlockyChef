package tnt.blockychef.integrations.waila;

import net.minecraft.nbt.CompoundTag;
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

public enum DryingRackComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        CompoundTag tag = blockAccessor.getServerData();
        if (tag.contains("time")) {
            int time = tag.getInt("time");
            int total = tag.getInt("total");
            ItemStack item = ItemStack.of(tag.getCompound("item"));

            IElementHelper helper = IElementHelper.get();
            tooltip.add(helper.item(item));
            tooltip.append(new ProgressArrowElement(time / (float)total));
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        DryingRackBlockEntity blockEntity = (DryingRackBlockEntity) accessor.getBlockEntity();
        if (blockEntity.hasItem()) {
            compoundTag.putInt("time", blockEntity.getTicksDrying());
            compoundTag.putInt("total", blockEntity.getTotalTime());
            compoundTag.put("item", blockEntity.getItem(0).save(new CompoundTag()));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return WailaIntegrationPlugin.DRYING_RACK;
    }
}
