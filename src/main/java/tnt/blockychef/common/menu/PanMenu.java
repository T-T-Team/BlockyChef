package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import tnt.blockychef.common.block.entity.PanBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.blockychef.common.init.BlockyChefTags;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;

public class PanMenu extends AbstractBlockEntityMenu<PanBlockEntity> {

    public PanMenu(int menuId, Inventory playerInventory, PanBlockEntity pan) {
        super(BlockyChefMenuTypes.PAN, menuId, pan);
        addSlot(new SlotItemHandler(pan.getItemHandler(), 0, 80, 8));
        addSlot(new SlotItemHandler(pan.getItemHandler(), 1, 107, 34));
        addSlot(new SlotItemHandler(pan.getItemHandler(), 2, 97, 65));
        addSlot(new SlotItemHandler(pan.getItemHandler(), 3, 63, 65));
        addSlot(new SlotItemHandler(pan.getItemHandler(), 4, 53, 34));
        addSlot(new SlotItemHandler(pan.getItemHandler(), 5, 8, 81) {
            @Override
            public int getMaxStackSize(@NotNull ItemStack stack) {
                return 1;
            }

            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(BlockyChefTags.Items.OIL);
            }
        });
        addPlayerSlots(playerInventory, 8, 106);
        addSlotListener(new SimpleSlotListener(this::slotChanged));
    }

    public PanMenu(int menuId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(menuId, playerInventory, resolveBlockEntityUnsafe(playerInventory, buffer));
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, BlockyChefBlocks.PAN);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    private void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index >= 0 && index < PanBlockEntity.INPUTS.length) {
            //blockEntity.refreshSlot(index);
        }
        if (index == PanBlockEntity.OIL[0]) {
            blockEntity.oilItemChanged(stack);
        }
    }
}
