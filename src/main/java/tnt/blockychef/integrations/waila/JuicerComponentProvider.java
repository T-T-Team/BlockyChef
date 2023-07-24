package tnt.blockychef.integrations.waila;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.ProgressArrowElement;
import tnt.blockychef.common.block.entity.JuicerBlockEntity;
import tnt.blockychef.common.food.fluid.FluidContainer;
import tnt.blockychef.integrations.waila.element.FluidStackElementExt;

import java.util.List;

public enum JuicerComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    INSTANCE;


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        CompoundTag tag = blockAccessor.getServerData();
        IElementHelper helper = IElementHelper.get();
        int amount = tag.getInt("filledAmount");
        tooltip.add(Component.translatable("label.blockychef.fluid_capacity", amount, JuicerBlockEntity.FLUID_CAPACITY));
        if (tag.contains("progress")) {
            float f = tag.getFloat("progress");
            ItemStack stack = ItemStack.of(tag.getCompound("item"));
            tooltip.add(helper.item(stack));
            tooltip.add(new ProgressArrowElement(f));
        }
        if (tag.contains("fluids")) {
            List<FluidStack> fluids = FluidContainer.deserializeAsList(tag.getList("fluids", Tag.TAG_COMPOUND));
            for (FluidStack stack : fluids) {
                tooltip.add(new FluidStackElementExt(stack));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        JuicerBlockEntity block = (JuicerBlockEntity) accessor.getBlockEntity();
        if (block.hasInputItem()) {
            compoundTag.putFloat("progress", block.getProgress());
            compoundTag.put("item", block.getInputItem().save(new CompoundTag()));
        }
        FluidContainer container = block.getFluids();
        if (!container.isEmpty()) {
            compoundTag.put("fluids", container.serializeAsList());
        }
        compoundTag.putInt("filledAmount", container.getAmount());
    }

    @Override
    public ResourceLocation getUid() {
        return WailaIntegrationPlugin.JUICER;
    }
}
