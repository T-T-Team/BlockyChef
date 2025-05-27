package tnt.blockychef.integrations.waila;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.ProgressStyle;
import tnt.blockychef.common.block.entity.DryingRackBlockEntity;

public enum DryingRackComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        CompoundTag tag = blockAccessor.getServerData();
        if (!tag.contains("data", Tag.TAG_LIST))
            return;
        ListTag dataTag = tag.getList("data", Tag.TAG_COMPOUND);
        IElementHelper helper = IElementHelper.get();
        for (int i = 0; i < dataTag.size(); i++) {
            CompoundTag data = dataTag.getCompound(i);
            ItemStack input = ItemStack.of(data.getCompound("input"));
            if (data.contains("dryingTotal", Tag.TAG_INT)) {
                ItemStack result = ItemStack.of(data.getCompound("result"));
                int total = data.getInt("dryingTotal");
                int current = data.getInt("dryingCurrent");
                int seconds = (total - current) / 20;
                float progress = (float) current / total;
                tooltip.add(Component.translatable("label.blockychef.drying", i + 1, seconds));
                tooltip.add(helper.item(input));
                tooltip.append(helper.progress(progress, null, new ProgressStyle(), new BoxStyle(), false));
                tooltip.append(helper.item(result));
            } else {
                tooltip.add(Component.translatable("label.blockychef.drying.no_recipe", i + 1));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        ListTag data = new ListTag();
        DryingRackBlockEntity blockEntity = (DryingRackBlockEntity) accessor.getBlockEntity();
        for (int i = 0; i < DryingRackBlockEntity.DRYING_CAPACITY; i++) {
            DryingRackBlockEntity.DryingSlot slot = blockEntity.getSlot(i);
            CompoundTag tag = new CompoundTag();
            data.add(tag);
            ItemStack itemStack = blockEntity.getItem(i);
            if (itemStack.isEmpty())
                continue;
            tag.put("input", itemStack.save(new CompoundTag()));
            if (!slot.hasRecipe())
                continue;
            tag.putInt("dryingCurrent", slot.getCurrentDryingTime());
            tag.putInt("dryingTotal", slot.getTotalDryingTime());
            tag.put("result", slot.getResult().save(new CompoundTag()));
        }
        compoundTag.put("data", data);
    }

    @Override
    public ResourceLocation getUid() {
        return WailaIntegrationPlugin.DRYING_RACK;
    }
}