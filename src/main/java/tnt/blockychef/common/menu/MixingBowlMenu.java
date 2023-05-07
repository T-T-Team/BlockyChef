package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import tnt.blockychef.common.block.MixingBowlBlock;
import tnt.blockychef.common.block.entity.MixingBowlBlockEntity;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.blockychef.util.MenuQuickMoveHelper;

public class MixingBowlMenu extends AbstractBlockEntityMenu<MixingBowlBlockEntity> {

    private final MenuQuickMoveHelper quickMoveHelper;

    public MixingBowlMenu(int menuId, Inventory inventory, MixingBowlBlockEntity mixingBowl) {
        super(BlockyChefMenuTypes.MIXING_BOWL, menuId, mixingBowl);
        this.quickMoveHelper = MenuQuickMoveHelper.inputOutputInventory(getQuickMoveContext(), MixingBowlBlockEntity.INPUTS, MixingBowlBlockEntity.OUTPUTS);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new SlotItemHandler(mixingBowl.getItemHandler(), x + y * 3, 8 + x * 18, 32 + y * 18));
            }
        }

        for (int x = 0; x < 3; x++) {
            addSlot(new ItemHandlerOutputSlotWithCallback(mixingBowl.getItemHandler(), 6 + x, 116 + x * 18, 41, this::onResultTaken));
        }

        addPlayerSlots(inventory, 8, 93);
        addSlotListener(new SimpleSlotListener(this::slotChanged));
    }

    public MixingBowlMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, state -> state.getBlock() instanceof MixingBowlBlock);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMoveHelper.quickMove(player, index);
    }

    private void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index < MixingBowlBlockEntity.INPUTS.length) {
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
