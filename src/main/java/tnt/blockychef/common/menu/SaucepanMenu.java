package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import tnt.blockychef.common.block.entity.SaucepanBlockEntity;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;
import tnt.tntlib.api.menu.MenuQuickMoveHelper;

public class SaucepanMenu extends AbstractBlockEntityMenu<SaucepanBlockEntity> {

    private final MenuQuickMoveHelper quickmove;

    public SaucepanMenu(int menuId, Inventory inventory, SaucepanBlockEntity saucepan) {
        super(BlockyChefMenuTypes.SAUCEPAN, menuId, saucepan);

        MenuQuickMoveHelper.QuickMoveContext ctx = this.getQuickMoveContext();
        this.quickmove = MenuQuickMoveHelper.Builder.withContext(ctx)
                .addRule2(0, 5, 9, 45)
                .build();

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new SlotItemHandler(saucepan.getItemHandler(), x + y * 3, x * 18 + 62, y * 18 + 18));
            }
        }
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                addSlot(new ItemHandlerOutputSlotWithCallback(saucepan.getItemHandler(), 6 + x + y * 2, 71 + x * 18, 90 + y * 18, this::outputItemRemoved));
            }
        }
        addPlayerSlots(inventory, 8, 137);
        addSlotListener(new SimpleSlotListener(this::onInputChanged));
    }

    public SaucepanMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, BlockyChefBlocks.SAUCEPAN);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return this.quickmove.quickMove(pPlayer, pIndex);
    }

    private void outputItemRemoved(Player player, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            CookingMastery.applyMastery(player, stack);
            blockEntity.awardUsedRecipesAndPopExperience(serverPlayer);
            blockEntity.setChanged();
        }
    }

    private void onInputChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index < 6) {
            blockEntity.reloadRecipe();
        }
    }
}
