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
import tnt.blockychef.common.block.entity.PotBlockEntity;
import tnt.blockychef.common.food.mastery.CookingMastery;
import tnt.blockychef.common.food.recipe.PotRecipe;
import tnt.blockychef.common.init.BlockyChefBlocks;
import tnt.blockychef.common.init.BlockyChefMenuTypes;
import tnt.blockychef.common.init.BlockyChefRecipeTypes;
import tnt.blockychef.common.init.BlockyChefTags;
import tnt.blockychef.util.Helper;
import tnt.tntlib.api.menu.AbstractBlockEntityMenu;

import java.util.Optional;
import java.util.function.Supplier;

public class PotMenu extends AbstractBlockEntityMenu<PotBlockEntity> {

    public PotMenu(int menuId, Inventory playerInventory, PotBlockEntity pot) {
        super(BlockyChefMenuTypes.POT, menuId, pot);
        addSlot(new PotSlot(0, 80, 8, pot));
        addSlot(new PotSlot(1, 107, 34, pot));
        addSlot(new PotSlot(2, 97, 65, pot));
        addSlot(new PotSlot(3, 63, 65, pot));
        addSlot(new PotSlot(4, 53, 34, pot));
        addSlot(new SlotItemHandler(pot.getItemHandler(), 5, 8, 81) {
            @Override
            public int getMaxStackSize(@NotNull ItemStack stack) {
                return 1;
            }

            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(BlockyChefTags.Items.WATER);
            }
        });
        addPlayerSlots(playerInventory, 8, 106);
        addSlotListener(new SimpleSlotListener(this::slotChanged));
    }

    public PotMenu(int menuId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(menuId, playerInventory, resolveBlockEntityUnsafe(playerInventory, buffer));
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, BlockyChefBlocks.POT);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    private void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index >= 0 && index < PotBlockEntity.INPUTS.length) {
            blockEntity.refreshSlot(index);
        }
        if (index == PotBlockEntity.WATER[0]) {
            blockEntity.waterInputItemChanged(stack);
        }
    }

    private static final class PotSlot extends SlotItemHandler {

        private final PotBlockEntity potBlockEntity;
        private final Supplier<PotBlockEntity.PotCookingSlot> slotProvider;

        public PotSlot(int index, int xPosition, int yPosition, PotBlockEntity potBlockEntity) {
            super(potBlockEntity.getItemHandler(), index, xPosition, yPosition);
            this.potBlockEntity = potBlockEntity;
            this.slotProvider = () -> {
                PotBlockEntity.PotCookingSlot[] slots = potBlockEntity.getSlots();
                return slots[index];
            };
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            PotBlockEntity.PotCookingSlot slot = slotProvider.get();
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
                potBlockEntity.awardUsedRecipesAndPopExperience(serverPlayer);
                potBlockEntity.setChanged();
            }
            super.onTake(pPlayer, pStack);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            Optional<RecipeHolder<PotRecipe>> opt = Helper.findRecipeFor(potBlockEntity.getLevel().getRecipeManager(), BlockyChefRecipeTypes.POT_RECIPE, recipe -> recipe.value().matches(stack) && !recipe.value().isOvercooked());
            return super.mayPlace(stack) && opt.isPresent();
        }
    }
}
