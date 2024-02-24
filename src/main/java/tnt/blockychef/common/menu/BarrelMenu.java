package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import tnt.blockychef.common.block.BarrelBlock;
import tnt.blockychef.common.block.entity.BarrelBlockEntity;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;
import tnt.tntlib.api.menu.MenuQuickMoveHelper;

public class BarrelMenu extends AbstractBlockEntityMenu<BarrelBlockEntity> {

    private final MenuQuickMoveHelper moveHelper;

    public BarrelMenu(int menuId, Inventory inventory, BarrelBlockEntity barrel) {
        super(BlockyChefMenuTypes.BARREL, menuId, barrel);
        this.moveHelper = MenuQuickMoveHelper.inputOutputInventory(getQuickMoveContext(), BarrelBlockEntity.INPUTS, BarrelBlockEntity.OUTPUTS);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new SlotItemHandler(barrel.getItemHandler(), x + y * 3, 8 + x * 18, 32 + y * 18));
            }
        }

        for (int x = 0; x < 3; x++) {
            addSlot(new ItemHandlerOutputSlotWithCallback(barrel.getItemHandler(), 6 + x, 116 + x * 18, 41, this::onResultTaken));
        }

        addPlayerSlots(inventory, 8, 93);
        addSlotListener(new SimpleSlotListener(this::slotChanged));

        barrel.startOpen(inventory.player);
    }

    public BarrelMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, state -> state.getBlock() instanceof BarrelBlock);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return moveHelper.quickMove(player, index);
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.blockEntity.stopOpen(pPlayer);
    }

    private void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index < BarrelBlockEntity.INPUTS.length) {
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
