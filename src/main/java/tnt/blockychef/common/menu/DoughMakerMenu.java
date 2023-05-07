package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import tnt.blockychef.common.block.entity.DoughMakerBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.blockychef.util.MenuQuickMoveHelper;

public class DoughMakerMenu extends AbstractBlockEntityMenu<DoughMakerBlockEntity> {

    private final MenuQuickMoveHelper quickMoveHelper;

    public DoughMakerMenu(int menuId, Inventory inventory, DoughMakerBlockEntity doughMaker) {
        super(BlockyChefMenuTypes.DOUGH_MAKER, menuId, doughMaker);
        this.quickMoveHelper = MenuQuickMoveHelper.inputOutputInventory(getQuickMoveContext(), DoughMakerBlockEntity.INPUTS, DoughMakerBlockEntity.OUTPUTS);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new SlotItemHandler(doughMaker.getItemHandler(), x + y * 3, 8 + x * 18, 32 + y * 18));
            }
        }

        for (int x = 0; x < 3; x++) {
            addSlot(new ItemHandlerOutputSlotWithCallback(doughMaker.getItemHandler(), 6 + x, 116 + x * 18, 41, this::onResultTaken));
        }

        addPlayerSlots(inventory, 8, 93);
        addSlotListener(new SimpleSlotListener(this::slotChanged));
    }

    public DoughMakerMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, BlockyChefBlocks.DOUGH_MAKER);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return quickMoveHelper.quickMove(player, slot);
    }

    private void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index < DoughMakerBlockEntity.INPUTS.length) {
            blockEntity.refreshRecipe();
        }
    }

    private void onResultTaken(Player player, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            blockEntity.awardUsedRecipesAndPopExperience(serverPlayer);
            blockEntity.setChanged();
        }
    }
}
