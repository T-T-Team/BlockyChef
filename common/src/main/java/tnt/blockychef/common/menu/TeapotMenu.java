package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import tnt.blockychef.common.block.entity.TeapotBlockEntity;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.blockychef.common.init.BlockyChefTags;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;
import tnt.tntlib.api.menu.MenuQuickMoveHelper;

public class TeapotMenu extends AbstractBlockEntityMenu<TeapotBlockEntity> {

    private final MenuQuickMoveHelper quickMoveHelper;

    public TeapotMenu(int menuId, Inventory inventory, TeapotBlockEntity teapot) {
        super(BlockyChefMenuTypes.TEAPOT, menuId, teapot);

        MenuQuickMoveHelper.QuickMoveContext ctx = getQuickMoveContext();
        quickMoveHelper = MenuQuickMoveHelper.Builder.withContext(ctx)
                .addRule(0, 6, 7, 43)
                .addRule(7, 43, 6, 6, itemStack -> itemStack.is(BlockyChefTags.Items.WATER))
                .addRule(7, 43, 0, 5)
                .build();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 2; x++) {
                addSlot(new SlotItemHandler(teapot.getItemHandler(), x + (y * 2), 8 + x * 18, 25 + y * 18));
            }
        }
        addSlot(new SlotItemHandler(teapot.getItemHandler(), 6, 90, 55) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(BlockyChefTags.Items.WATER);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public int getMaxStackSize(@NotNull ItemStack stack) {
                return 1;
            }
        });

        addPlayerSlots(inventory, 8, 106);
        addSlotListener(new SimpleSlotListener(this::slotChanged));
    }

    public TeapotMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, BlockyChefBlocks.TEAPOT);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return quickMoveHelper.quickMove(pPlayer, pIndex);
    }

    private void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index == 6) {
            blockEntity.waterInputItemChanged(stack);
        } else if (index < 6) {
            blockEntity.reloadRecipe();
        }
    }
}
