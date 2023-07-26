package tnt.blockychef.common.data.fluids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import tnt.tntlib.api.serialization.Codecs;

public final class FluidExtraction {

    public static final Codec<FluidExtraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.comapFlatMap(location -> {
                if (!ForgeRegistries.ITEMS.containsKey(location)) {
                    return DataResult.error(() -> "Unknown item: " + location);
                }
                return DataResult.success(ForgeRegistries.ITEMS.getValue(location));
            }, ForgeRegistries.ITEMS::getKey).fieldOf("item").forGetter(t -> t.inputItem.getItem()),
            Codecs.SIMPLE_ITEMSTACK_CODEC.fieldOf("output").forGetter(t -> t.outputItem),
            FluidStack.CODEC.fieldOf("fluidFilter").forGetter(t -> t.fluid)
    ).apply(instance, FluidExtraction::new));

    private final ItemStack inputItem;
    private final ItemStack outputItem;
    private final FluidStack fluid;

    public FluidExtraction(Item inputItem, ItemStack outputItem, FluidStack fluid) {
        this.inputItem = new ItemStack(inputItem);
        this.outputItem = outputItem;
        this.fluid = fluid;
    }

    public ItemStack getInputItem() {
        return inputItem;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    public boolean matches(ItemStack stack, FluidHolder holder) {
        return stack.getItem() == inputItem.getItem() && holder.hasFluid(fluid);
    }

    public ItemStack extractFluid(FluidHolder holder) {
        return holder.extract(fluid.copy()) ? outputItem.copy() : ItemStack.EMPTY;
    }
}
