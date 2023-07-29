package tnt.blockychef.integrations.waila;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.ProgressArrowElement;
import tnt.blockychef.common.block.entity.BarrelBlockEntity;
import tnt.tntlib.api.serialization.NbtUtil;

import java.util.ArrayList;
import java.util.List;

public enum BarrelComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        CompoundTag tag = blockAccessor.getServerData();
        if (!tag.contains("fermentTime"))
            return;
        int fermentTime = tag.getInt("fermentTime");
        int totalFermentTime = tag.getInt("totalFermentTime");
        List<ItemStack> inputs = NbtUtil.collectionFromNbt(new ArrayList<>(), tag.getList("inputs", Tag.TAG_COMPOUND), ItemStack::of, CompoundTag.class);
        List<ItemStack> outputs = NbtUtil.collectionFromNbt(new ArrayList<>(), tag.getList("output", Tag.TAG_COMPOUND), ItemStack::of, CompoundTag.class);
        iTooltip.add(Component.translatable("label.blockychef.fermenting", (totalFermentTime - fermentTime) / 20));
        IElementHelper helper = IElementHelper.get();
        iTooltip.add(CommonComponents.EMPTY);
        inputs.forEach(in -> iTooltip.append(helper.item(in)));
        iTooltip.append(new ProgressArrowElement(fermentTime / (float) totalFermentTime));
        outputs.forEach(out -> iTooltip.append(helper.item(out)));
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        BarrelBlockEntity block = (BarrelBlockEntity) accessor.getBlockEntity();
        int fermentTime = block.getFermentingTime();
        if (fermentTime > 0) {
            compoundTag.putInt("fermentTime", fermentTime);
            compoundTag.putInt("totalFermentTime", block.getTotalFermentTime());
            compoundTag.put("inputs", NbtUtil.collectionToNbt(block.getInputItems(), IForgeItemStack::serializeNBT));
            compoundTag.put("output", NbtUtil.collectionToNbt(block.getOutputs(), IForgeItemStack::serializeNBT));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return WailaIntegrationPlugin.BARREL;
    }
}
