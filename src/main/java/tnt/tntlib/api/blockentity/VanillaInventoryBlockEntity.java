package tnt.tntlib.api.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class VanillaInventoryBlockEntity extends InventoryBlockEntity implements Container {

    public VanillaInventoryBlockEntity(BlockEntityType<? extends VanillaInventoryBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public int getContainerSize() {
        return this.inventoryHandler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack stack = this.inventoryHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < this.getContainerSize() ? this.inventoryHandler.getStackInSlot(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = ItemStack.EMPTY;
        if (index >= 0 && index < this.getContainerSize()) {
            stack = this.inventoryHandler.getStackInSlot(index).split(count);
        }
        if (!stack.isEmpty()) {
            this.setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = this.inventoryHandler.getStackInSlot(index);
        if (stack.isEmpty()) {
            return stack;
        }
        this.inventoryHandler.setStackInSlot(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.inventoryHandler.setStackInSlot(index, stack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < this.getContainerSize(); i++) {
            this.inventoryHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
        this.setChanged();
    }
}
