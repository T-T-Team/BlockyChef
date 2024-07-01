package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import tnt.blockychef.common.block.entity.GrillBlockEntity;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.blockychef.common.init.BlockyChefTags;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;
import tnt.tntlib.api.menu.MenuQuickMoveHelper;

import java.util.function.Supplier;

public class GrillMenu extends AbstractBlockEntityMenu<GrillBlockEntity> {

    private final MenuQuickMoveHelper moveHelper;

    public GrillMenu(int menuId, Inventory inventory, GrillBlockEntity grill) {
        super(BlockyChefMenuTypes.GRILL, menuId, grill);

        MenuQuickMoveHelper.QuickMoveContext ctx = this.getQuickMoveContext();
        this.moveHelper = MenuQuickMoveHelper.Builder.withContext(ctx)
                .addRule(0, 1, 8, 43)
                .addRule(8, 43, 0, 1, itemstack -> itemstack.is(BlockyChefTags.Items.GRILL_FUEL))
                .build();

        addSlot(new GrillFuelSlot(grill.getItemHandler(), 0, 80, 91));
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                int index = 1 + x + y * 3;
                int slotIndex = index - 1;
                addSlot(new GrillSlot(grill.getItemHandler(), index, 44 + x * 36, 17 + y * 37, grill, () -> {
                    GrillBlockEntity.GrillSlot[] grillSlots = grill.getSlots();
                    return grillSlots[slotIndex];
                }));
            }
        }
        addPlayerSlots(inventory, 8, 126);

        this.addSlotListener(new SimpleSlotListener(this::onSlotChanged));
    }

    public GrillMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, BlockyChefBlocks.GRILL);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return moveHelper.quickMove(pPlayer, pIndex);
    }

    private void onSlotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index >= 1 && index < 7) {
            blockEntity.refreshSlot(index);
        }
    }

    private static final class GrillFuelSlot extends SlotItemHandler {

        public GrillFuelSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.is(BlockyChefTags.Items.GRILL_FUEL);
        }
    }

    private static final class GrillSlot extends SlotItemHandler {

        private final GrillBlockEntity grill;
        private final Supplier<GrillBlockEntity.GrillSlot> slotProvider;

        public GrillSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, GrillBlockEntity grill, Supplier<GrillBlockEntity.GrillSlot> slotProvider) {
            super(itemHandler, index, xPosition, yPosition);
            this.grill = grill;
            this.slotProvider = slotProvider;
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            GrillBlockEntity.GrillSlot slot = slotProvider.get();
            return !slot.isSlotLocked();
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int getMaxStackSize(@NotNull ItemStack stack) {
            return getMaxStackSize();
        }

        @Override
        public void onTake(Player pPlayer, ItemStack pStack) {
            CookingMastery.applyMastery(pPlayer, pStack);
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                grill.awardUsedRecipesAndPopExperience(serverPlayer);
                grill.setChanged();
            }
            super.onTake(pPlayer, pStack);
        }
    }
}
