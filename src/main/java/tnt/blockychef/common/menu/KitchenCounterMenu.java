package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import tnt.blockychef.common.block.KitchenCounterBlock;
import tnt.blockychef.common.block.entity.KitchenCounterBlockEntity;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;
import tnt.tntlib.api.menu.MenuQuickMoveHelper;

public class KitchenCounterMenu extends AbstractBlockEntityMenu<KitchenCounterBlockEntity> {

    private final MenuQuickMoveHelper quickMoveHelper;

    public KitchenCounterMenu(int menuId, Inventory inventory, KitchenCounterBlockEntity blockEntity) {
        super(BlockyChefMenuTypes.KITCHEN_COUNTER_MENU, menuId, blockEntity);

        MenuQuickMoveHelper.QuickMoveContext ctx = this.getQuickMoveContext();
        this.quickMoveHelper = MenuQuickMoveHelper.simpleInventory(ctx, 54);

        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }
        addPlayerSlots(inventory, 8, 140);
    }

    public KitchenCounterMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, state -> state.getBlock() instanceof KitchenCounterBlock);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return quickMoveHelper.quickMove(pPlayer, pIndex);
    }
}
