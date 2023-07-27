package tnt.blockychef.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class CraftingRemainderItem extends Item {

    private final Supplier<ItemStack> craftRemainderProvider;

    public CraftingRemainderItem(Properties properties, Supplier<ItemStack> craftRemainderProvider) {
        super(properties);
        this.craftRemainderProvider = craftRemainderProvider;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return craftRemainderProvider.get();
    }
}
