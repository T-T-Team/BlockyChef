package tnt.blockychef.common.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class BlockEntityStackHandler extends ItemStackHandler {

    private final Runnable onContentsChanged;

    public BlockEntityStackHandler(Runnable onContentsChanged) {
        this.onContentsChanged = onContentsChanged;
    }

    public BlockEntityStackHandler(int size, Runnable onContentsChanged) {
        super(size);
        this.onContentsChanged = onContentsChanged;
    }

    public BlockEntityStackHandler(NonNullList<ItemStack> stacks, Runnable onContentsChanged) {
        super(stacks);
        this.onContentsChanged = onContentsChanged;
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.onContentsChanged.run();
    }
}
