package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import tnt.blockychef.common.block.entity.ToasterBlockEntity;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.food.recipe.ToasterRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;

import java.util.Optional;

public class ToasterMenu extends AbstractBlockEntityMenu<ToasterBlockEntity> {

    public ToasterMenu(int menuId, Inventory inventory, ToasterBlockEntity toaster) {
        super(BlockyChefMenuTypes.TOASTER, menuId, toaster);

        IItemHandler handler = toaster.getItemHandler();
        addSlot(new ToasterSlot(handler, 0, 44, 18, toaster));
        addSlot(new ToasterSlot(handler, 1, 116, 18, toaster));

        addPlayerSlots(inventory, 8, 93);
    }

    public ToasterMenu(int menuId, Inventory inventory, FriendlyByteBuf buffer) {
        this(menuId, inventory, resolveBlockEntityUnsafe(inventory, buffer));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, BlockyChefBlocks.TOASTER);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }

    private static final class ToasterSlot extends SlotItemHandler {

        private final ToasterBlockEntity toaster;

        public ToasterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ToasterBlockEntity blockEntity) {
            super(itemHandler, index, xPosition, yPosition);
            this.toaster = blockEntity;
        }

        @Override
        public void onTake(Player pPlayer, ItemStack pStack) {
            CookingMastery.applyMastery(pPlayer, pStack);
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                toaster.awardUsedRecipesAndPopExperience(serverPlayer);
                toaster.setChanged();
            }
            super.onTake(pPlayer, pStack);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            Optional<ToasterRecipe> recipe = toaster.getRecipe(stack);
            return !toaster.isToasting() && recipe.isPresent() && !recipe.get().isOvercooked();
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            return !toaster.isToasting();
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int getMaxStackSize(@NotNull ItemStack stack) {
            return 1;
        }
    }
}
