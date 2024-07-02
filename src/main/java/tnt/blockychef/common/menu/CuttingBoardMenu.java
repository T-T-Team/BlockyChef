package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import tnt.blockychef.common.block.CuttingBoardBlock;
import tnt.blockychef.common.block.entity.CuttingBoardBlockEntity;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;
import tnt.tntlib.api.menu.MenuQuickMoveHelper;

public class CuttingBoardMenu extends AbstractBlockEntityMenu<CuttingBoardBlockEntity> {

    private final MenuQuickMoveHelper quickMoveHelper;

    public CuttingBoardMenu(int menuId, Inventory inventory, CuttingBoardBlockEntity blockEntity) {
        super(BlockyChefMenuTypes.CUTTING_BOARD, menuId, blockEntity);
        this.quickMoveHelper = MenuQuickMoveHelper.inputOutputInventory(getQuickMoveContext(), new int[] {CuttingBoardBlockEntity.SLOT_INPUT}, CuttingBoardBlockEntity.SLOT_OUTPUTS);

        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), CuttingBoardBlockEntity.SLOT_INPUT, 26, 36));
        for (int y = 0; y < CuttingBoardBlockEntity.SLOT_OUTPUTS.length; y++) {
            addSlot(new ItemHandlerOutputSlotWithCallback(blockEntity.getItemHandler(), CuttingBoardBlockEntity.SLOT_OUTPUTS[y], 134, 18 + y * 18, this::onResultItemTaken));
        }

        addPlayerSlots(inventory, 8, 113);

        addSlotListener(new SimpleSlotListener(this::slotChanged));
    }

    public CuttingBoardMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, state -> state.getBlock() instanceof CuttingBoardBlock);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return quickMoveHelper.quickMove(player, slotIndex);
    }

    private void onResultItemTaken(Player player, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            blockEntity.awardUsedRecipesAndPopExperience(serverPlayer);
            blockEntity.setChanged();
        }
    }

    private void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index == 0) {
            blockEntity.onInputChanged();
        }
    }
}
