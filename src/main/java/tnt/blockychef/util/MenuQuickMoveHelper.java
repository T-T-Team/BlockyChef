package tnt.blockychef.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class MenuQuickMoveHelper {

    public static final int PLAYER_INVENTORY_SIZE = 36;
    private final Supplier<List<Slot>> slotProvider;
    private final SlotItemMoveHelper moveHelper;
    private final Rule[] rules;

    private MenuQuickMoveHelper(Builder builder) {
        slotProvider = builder.slotProvider;
        moveHelper = builder.moveHelper;
        rules = builder.rules.toArray(Rule[]::new);
    }

    public static MenuQuickMoveHelper simpleInventory(Supplier<List<Slot>> slotProvider, SlotItemMoveHelper moveHelper, int externalInventorySize) {
        return Builder.withContext(slotProvider, moveHelper)
                .addRule(0, externalInventorySize, externalInventorySize, externalInventorySize + PLAYER_INVENTORY_SIZE)
                .addRule(externalInventorySize, externalInventorySize + PLAYER_INVENTORY_SIZE, 0, externalInventorySize)
                .build();
    }

    public static MenuQuickMoveHelper inputOutputInventory(Supplier<List<Slot>> slotProvider, SlotItemMoveHelper moveHelper, int[] inputSlots, int[] outputSlots) {
        int inputSize = inputSlots.length;
        int outputSize = outputSlots.length;
        return Builder.withContext(slotProvider, moveHelper)
                .addRule(0, inputSize, inputSize + outputSize, inputSize + outputSize + PLAYER_INVENTORY_SIZE) // in to inv
                .addRule(inputSize, inputSize + outputSize, inputSize + outputSize, inputSize + outputSize + PLAYER_INVENTORY_SIZE) // out to inv
                .addRule(inputSize + outputSize, inputSize + outputSize + PLAYER_INVENTORY_SIZE, 0, inputSize) // inv to in
                .build();
    }

    public ItemStack quickMove(Player player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slotProvider.get().get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            stack = slotItem.copy();
            Rule rule = getRuleForSlot(slotIndex, slotItem);
            if (rule != null && rule.move(slotItem, moveHelper)) {
                return ItemStack.EMPTY;
            }

            if (slotItem.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }

    private Rule getRuleForSlot(int slotIndex, ItemStack stack) {
        for (Rule rule : rules) {
            if (rule.canUse(slotIndex, stack)) {
                return rule;
            }
        }
        return null;
    }

    private record Rule(int fromMin, int fromMax, int toMin, int toMax, boolean reverseOrder, Predicate<ItemStack> filter) {

        boolean canUse(int slotIndex, ItemStack itemStack) {
            return slotIndex >= fromMin && slotIndex < fromMax && filter.test(itemStack);
        }

        boolean move(ItemStack stack, SlotItemMoveHelper moveManager) {
            return moveManager.moveItem(stack, toMin, toMax, reverseOrder);
        }
    }

    @FunctionalInterface
    public interface SlotItemMoveHelper {

        boolean moveItem(ItemStack stack, int fromIndex, int toIndex, boolean reverseOrder);
    }

    public static final class Builder {

        private final Supplier<List<Slot>> slotProvider;
        private final SlotItemMoveHelper moveHelper;
        private final List<Rule> rules = new ArrayList<>();

        private Builder(Supplier<List<Slot>> slotProvider, SlotItemMoveHelper moveHelper) {
            this.slotProvider = slotProvider;
            this.moveHelper = moveHelper;
        }

        public static Builder withContext(Supplier<List<Slot>> slotProvider, SlotItemMoveHelper moveHelper) {
            return new Builder(slotProvider, moveHelper);
        }

        public Builder addRule(int fromMin, int fromMax, int toMin, int toMax, boolean reverseOrder, Predicate<ItemStack> filter) {
            this.rules.add(new Rule(fromMin, fromMax, toMin, toMax, reverseOrder, filter));
            return this;
        }

        public Builder addRule(int fromMin, int fromMax, int toMin, int toMax, Predicate<ItemStack> filter) {
            return addRule(fromMin, fromMax, toMin, toMax, false, filter);
        }

        public Builder addRule(int fromMin, int fromMax, int toMin, int toMax, boolean reverseOrder) {
            return addRule(fromMin, fromMax, toMin, toMax, reverseOrder, stack -> true);
        }

        public Builder addRule(int fromMin, int fromMax, int toMin, int toMax) {
            return addRule(fromMin, fromMax, toMin, toMax, false);
        }

        public MenuQuickMoveHelper build() {
            return new MenuQuickMoveHelper(this);
        }
    }
}
