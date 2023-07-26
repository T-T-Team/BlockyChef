package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import tnt.blockychef.common.block.MortarAndPestleBlock;
import tnt.blockychef.common.block.entity.MortarAndPestleBlockEntity;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;
import tnt.tntlib.api.menu.MenuQuickMoveHelper;

public class MortarAndPestleMenu extends AbstractBlockEntityMenu<MortarAndPestleBlockEntity> {

    private final MenuQuickMoveHelper moveHelper;

    public MortarAndPestleMenu(int menuId, Inventory inventory, MortarAndPestleBlockEntity blockEntity) {
        super(BlockyChefMenuTypes.MORTAR_AND_PESTLE, menuId, blockEntity);
        this.moveHelper = MenuQuickMoveHelper.inputOutputInventory(getQuickMoveContext(), MortarAndPestleBlockEntity.INPUTS, MortarAndPestleBlockEntity.OUTPUT);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), x + y * 3, 8 + x * 18, 32 + y * 18));
            }
        }
        for (int x = 0; x < 3; x++) {
            addSlot(new ItemHandlerOutputSlotWithCallback(blockEntity.getItemHandler(), 6 + x, 116 + x * 18, 41, this::onResultTakenOut));
        }
        addPlayerSlots(inventory, 8, 93);
        addSlotListener(new SimpleSlotListener(this::slotChanged));
    }

    public MortarAndPestleMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, state -> state.getBlock() instanceof MortarAndPestleBlock);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return moveHelper.quickMove(player, slotIndex);
    }

    private void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index < MortarAndPestleBlockEntity.INPUTS.length) {
            blockEntity.refreshRecipe();
        }
    }

    private void onResultTakenOut(Player player, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            blockEntity.awardUsedRecipesAndPopExperience(serverPlayer);
            blockEntity.setChanged();
        }
    }
}
