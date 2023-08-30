package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import tnt.blockychef.common.block.entity.StoveBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;
import tnt.tntlib.api.menu.MenuQuickMoveHelper;

import java.util.function.Supplier;

public class StoveMenu extends AbstractBlockEntityMenu<StoveBlockEntity> {

    private final MenuQuickMoveHelper moveHelper;

    public StoveMenu(int menuId, Inventory inventory, StoveBlockEntity stove) {
        super(BlockyChefMenuTypes.STOVE, menuId, stove);
        MenuQuickMoveHelper.QuickMoveContext context = getQuickMoveContext();
        this.moveHelper = null; // TODO
        // Fuel slot
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                // Cooking slots
                int slotIndex = (y * 3) + x;
                addSlot(new CookingSlot(stove.getItemHandler(), slotIndex, 44 + x * 36, 17 + y * 28, () -> {
                    StoveBlockEntity.CookingSlot[] cookSlots = stove.getSlots();
                    return cookSlots[slotIndex];
                }));
            }
        }
        addSlot(new SlotItemHandler(stove.getItemHandler(), 6, 80, 69));
        // Player inventory
        addPlayerSlots(inventory, 8, 93);

        addSlotListener(new SimpleSlotListener(this::slotChanged));
    }

    public StoveMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY; // TODO
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, BlockyChefBlocks.STOVE);
    }

    private void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index >= 0 && index < StoveBlockEntity.INPUTS.length) {
            blockEntity.refreshSlot(index);
        }
    }

    private static final class CookingSlot extends SlotItemHandler {

        private final Supplier<StoveBlockEntity.CookingSlot> cookingSlotProvider;

        public CookingSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Supplier<StoveBlockEntity.CookingSlot> cookingSlotProvider) {
            super(itemHandler, index, xPosition, yPosition);
            this.cookingSlotProvider = cookingSlotProvider;
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            StoveBlockEntity.CookingSlot cookingSlot = cookingSlotProvider.get();
            return !cookingSlot.isLocked();
        }
    }
}
