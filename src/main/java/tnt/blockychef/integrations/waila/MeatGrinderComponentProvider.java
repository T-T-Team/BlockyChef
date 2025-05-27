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
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.ProgressStyle;
import tnt.blockychef.common.block.entity.MeatGrinderBlockEntity;
import tnt.blockychef.common.food.recipe.MeatGrinderRecipe;

public enum MeatGrinderComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        CompoundTag tag = blockAccessor.getServerData();
        if (!tag.contains("input"))
            return;
        float progress = (float) tag.getInt("current") / tag.getInt("total");
        ItemStack inputStack = ItemStack.of(tag.getCompound("input"));
        ItemStack outputStack = ItemStack.of(tag.getCompound("output"));

        IElementHelper helper = IElementHelper.get();
        Component title = Component.translatable("label.blockychef.grinding", Math.round(progress * 100.0F));
        iTooltip.add(title);
        iTooltip.add(helper.item(inputStack));
        iTooltip.append(helper.progress(progress, null, new ProgressStyle(), new BoxStyle(), false));
        iTooltip.append(helper.item(outputStack));
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        MeatGrinderBlockEntity blockEntity = (MeatGrinderBlockEntity) accessor.getBlockEntity();
        MeatGrinderRecipe recipe = blockEntity.getRecipe();
        if (blockEntity.hasInput() && recipe != null) {
            compoundTag.put("input", blockEntity.getInputItem().serializeNBT());
            compoundTag.put("output", recipe.getOutput().serializeNBT());
            compoundTag.putInt("current", blockEntity.getGrindAmount());
            compoundTag.putInt("total", recipe.getProcessingAmount());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return WailaIntegrationPlugin.MEAT_GRINDER;
    }
}
