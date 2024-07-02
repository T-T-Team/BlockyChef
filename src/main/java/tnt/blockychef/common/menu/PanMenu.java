package tnt.blockychef.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import tnt.blockychef.common.block.entity.PanBlockEntity;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.food.recipe.PanRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefTags;
import tnt.blockychef.util.Helper;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;

import java.util.Optional;
import java.util.function.Supplier;

public class PanMenu extends AbstractBlockEntityMenu<PanBlockEntity> {

    public PanMenu(int menuId, Inventory playerInventory, PanBlockEntity pan) {
        super(BlockyChefMenuTypes.PAN, menuId, pan);
        addSlot(new PanSlot(0, 80, 8, pan));
        addSlot(new PanSlot(1, 107, 34, pan));
        addSlot(new PanSlot(2, 97, 65, pan));
        addSlot(new PanSlot(3, 63, 65, pan));
        addSlot(new PanSlot(4, 53, 34, pan));
        addSlot(new SlotItemHandler(pan.getItemHandler(), 5, 8, 81) {
            @Override
            public int getMaxStackSize(@NotNull ItemStack stack) {
                return 1;
            }

            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(BlockyChefTags.Items.OIL);
            }
        });
        addPlayerSlots(playerInventory, 8, 106);
        addSlotListener(new SimpleSlotListener(this::slotChanged));
    }

    public PanMenu(int menuId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(menuId, playerInventory, resolveBlockEntityUnsafe(playerInventory, buffer));
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, BlockyChefBlocks.PAN);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    private void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index >= 0 && index < PanBlockEntity.INPUTS.length) {
            blockEntity.refreshSlot(index);
        }
        if (index == PanBlockEntity.OIL[0]) {
            blockEntity.oilItemChanged(stack);
        }
    }

    private static final class PanSlot extends SlotItemHandler {

        private final PanBlockEntity panBlockEntity;
        private final Supplier<PanBlockEntity.PanCookingSlot> slotProvider;

        public PanSlot(int index, int xPosition, int yPosition, PanBlockEntity panBlockEntity) {
            super(panBlockEntity.getItemHandler(), index, xPosition, yPosition);
            this.panBlockEntity = panBlockEntity;
            this.slotProvider = () -> {
                PanBlockEntity.PanCookingSlot[] slots = panBlockEntity.getSlots();
                return slots[index];
            };
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            PanBlockEntity.PanCookingSlot slot = slotProvider.get();
            return !slot.isLocked();
        }

        @Override
        public int getMaxStackSize(@NotNull ItemStack stack) {
            return getMaxStackSize();
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void onTake(Player pPlayer, ItemStack pStack) {
            CookingMastery.applyMastery(pPlayer, pStack);
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                panBlockEntity.awardUsedRecipesAndPopExperience(serverPlayer);
                panBlockEntity.setChanged();
            }
            super.onTake(pPlayer, pStack);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            Optional<RecipeHolder<PanRecipe>> opt = Helper.findRecipeFor(panBlockEntity.getLevel().getRecipeManager(), BlockyChefRecipeTypes.PAN_RECIPE, recipe -> recipe.value().matches(stack) && !recipe.value().isOvercooked());
            return super.mayPlace(stack) && opt.isPresent();
        }
    }
}
