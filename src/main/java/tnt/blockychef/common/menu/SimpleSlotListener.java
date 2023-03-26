package tnt.blockychef.common.menu;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public class SimpleSlotListener implements ContainerListener {

    private final SlotChangeHandler slotHandler;

    public SimpleSlotListener(SlotChangeHandler slotHandler) {
        this.slotHandler = slotHandler;
    }

    @Override
    public void slotChanged(AbstractContainerMenu menu, int index, ItemStack itemStack) {
        this.slotHandler.onSlotChanged(menu, index, itemStack);
    }

    @Override
    public void dataChanged(AbstractContainerMenu menu, int index, int data) {
    }

    @FunctionalInterface
    public interface SlotChangeHandler {
        void onSlotChanged(AbstractContainerMenu menu, int index, ItemStack itemStack);
    }
}
