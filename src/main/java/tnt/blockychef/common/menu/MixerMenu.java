package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import tnt.blockychef.common.block.entity.MixerBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;
import tnt.tntlib.api.menu.MenuQuickMoveHelper;

public class MixerMenu extends AbstractBlockEntityMenu<MixerBlockEntity> {

    private final MenuQuickMoveHelper moveHelper;

    public MixerMenu(int menuId, Inventory inventory, MixerBlockEntity mixer) {
        super(BlockyChefMenuTypes.MIXER, menuId, mixer);
        moveHelper = MenuQuickMoveHelper.simpleInventory(getQuickMoveContext(), MixerBlockEntity.INPUTS.length);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new SlotItemHandler(mixer.getItemHandler(), x + y * 3, 62 + x * 18, 17 + y * 18));
            }
        }

        addPlayerSlots(inventory, 8, 93);

        addSlotListener(new SimpleSlotListener(this::slotChanged));
    }

    public MixerMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, BlockyChefBlocks.MIXER);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return moveHelper.quickMove(player, slot);
    }

    private void slotChanged(AbstractContainerMenu menu, int slot, ItemStack itemStack) {
        if (slot < MixerBlockEntity.INPUTS.length) {
            blockEntity.refreshRecipe();
        }
    }
}
