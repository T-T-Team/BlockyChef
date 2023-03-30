package tnt.blockychef.common.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerOutputSlotWithCallback extends ItemHandlerOutputSlot {

    private final ItemTakeCallback callback;

    public ItemHandlerOutputSlotWithCallback(IItemHandler itemHandler, int index, int xPosition, int yPosition, ItemTakeCallback callback) {
        super(itemHandler, index, xPosition, yPosition);
        this.callback = callback;
    }

    @Override
    public void onTake(Player player, ItemStack itemStack) {
        callback.onItemTaken(player, itemStack);
        super.onTake(player, itemStack);
    }

    @FunctionalInterface
    public interface ItemTakeCallback {
        void onItemTaken(Player player, ItemStack stack);
    }
}
