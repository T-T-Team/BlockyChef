package tnt.blockychef.common.item;

import net.minecraft.world.item.ItemStack;
import tnt.blockychef.common.thirst.DrinkProperties;

public interface Drinkable {

    DrinkProperties getStats();

    default ItemStack getReturningItem() {
        return ItemStack.EMPTY;
    }
}
