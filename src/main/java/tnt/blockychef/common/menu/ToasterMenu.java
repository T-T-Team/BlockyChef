package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import tnt.blockychef.common.block.entity.ToasterBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockychefMenuTypes;
import tnt.blockychef.util.MenuQuickMoveHelper;

public class ToasterMenu extends AbstractBlockEntityMenu<ToasterBlockEntity> {

    private final MenuQuickMoveHelper moveHelper;

    public ToasterMenu(int menuId, Inventory inventory, ToasterBlockEntity toaster) {
        super(BlockychefMenuTypes.TOASTER, menuId, toaster);
        this.moveHelper = MenuQuickMoveHelper.simpleInventory(getQuickMoveContext(), 2);

        IItemHandler handler = toaster.getItemHandler();
        addSlot(new ToasterSlot(handler, 0, 44, 18, toaster));
        addSlot(new ToasterSlot(handler, 1, 116, 18, toaster));

        addPlayerSlots(inventory, 8, 93);
    }

    public ToasterMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, BlockyChefBlocks.TOASTER);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return moveHelper.quickMove(player, slotIndex);
    }

    private static final class ToasterSlot extends SlotItemHandler {

        private final ToasterBlockEntity toaster;

        public ToasterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ToasterBlockEntity blockEntity) {
            super(itemHandler, index, xPosition, yPosition);
            this.toaster = blockEntity;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return toaster.getRecipe(stack).isPresent();
        }
    }
}
