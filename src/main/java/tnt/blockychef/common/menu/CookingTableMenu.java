package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import tnt.blockychef.common.block.CookingTableBlock;
import tnt.blockychef.common.block.entity.CookingTableBlockEntity;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;
import tnt.tntlib.api.menu.MenuQuickMoveHelper;

public class CookingTableMenu extends AbstractBlockEntityMenu<CookingTableBlockEntity> {

    private final MenuQuickMoveHelper quickMoveHelper;

    public CookingTableMenu(int menuId, Inventory inventory, CookingTableBlockEntity blockEntity) {
        super(BlockyChefMenuTypes.COOKING_TABLE, menuId, blockEntity);

        MenuQuickMoveHelper.QuickMoveContext ctx = this.getQuickMoveContext();
        this.quickMoveHelper = MenuQuickMoveHelper.inputOutputInventory(ctx, CookingTableBlockEntity.INPUTS, CookingTableBlockEntity.OUTPUTS);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), x + y * 3, 8 + x * 18, 29 + y * 18));
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 2; x++) {
                addSlot(new OutputSlot(blockEntity.getItemHandler(), 9 + x + y * 2, 134 + x * 18, 29 + y * 18));
            }
        }

        addPlayerSlots(inventory, 8, 108);
        addSlotListener(new SimpleSlotListener(this::slotChanged));
    }

    public CookingTableMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, state -> state.getBlock() instanceof CookingTableBlock);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return this.quickMoveHelper.quickMove(pPlayer, pIndex);
    }

    private void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {

    }

    private static final class OutputSlot extends SlotItemHandler {

        public OutputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }
    }
}
