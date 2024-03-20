package tnt.blockychef.integrations.waila;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import tnt.blockychef.common.block.entity.MixerBlockEntity;
import tnt.blockychef.common.block.entity.TeapotBlockEntity;
import tnt.blockychef.common.food.fluid.FluidContainer;
import tnt.blockychef.integrations.waila.element.FluidStackElementExt;

import java.util.List;

public enum TeapotComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        CompoundTag tag = blockAccessor.getServerData();
        int amount = tag.getInt("filledAmount");
        tooltip.add(Component.translatable("label.blockychef.fluid_capacity", amount, TeapotBlockEntity.CAPACITY));
        if (tag.contains("fluids")) {
            List<FluidStack> fluids = FluidContainer.deserializeAsList(tag.getList("fluids", Tag.TAG_COMPOUND));
            for (FluidStack stack : fluids) {
                tooltip.add(new FluidStackElementExt(stack));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        TeapotBlockEntity block = (TeapotBlockEntity) accessor.getBlockEntity();
        FluidContainer container = block.getFluidContainer();
        if (!container.isEmpty()) {
            compoundTag.put("fluids", container.serializeAsList());
        }
        compoundTag.putInt("filledAmount", container.getAmount());
    }

    @Override
    public ResourceLocation getUid() {
        return WailaIntegrationPlugin.TEAPOT;
    }
}
