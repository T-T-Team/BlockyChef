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
    private final QuickMoveContext context;
    private final Rule[] rules;

    private MenuQuickMoveHelper(Builder builder) {
        context = builder.context;
        rules = builder.rules.toArray(Rule[]::new);
    }

    public static MenuQuickMoveHelper simpleInventory(QuickMoveContext context, int externalInventorySize) {
        return Builder.withContext(context)
                .addRule2(0, externalInventorySize, externalInventorySize, externalInventorySize + PLAYER_INVENTORY_SIZE)
                .build();
    }

    public static MenuQuickMoveHelper inputOutputInventory(QuickMoveContext context, int[] inputSlots, Predicate<ItemStack> inputFilter, int[] outputSlots) {
        int inputSize = inputSlots.length;
        int outputSize = outputSlots.length;
        return Builder.withContext(context)
                .addRule(0, inputSize, inputSize + outputSize, inputSize + outputSize + PLAYER_INVENTORY_SIZE) // in to inv
                .addRule(inputSize, inputSize + outputSize, inputSize + outputSize, inputSize + outputSize + PLAYER_INVENTORY_SIZE) // out to inv
                .addRule(inputSize + outputSize, inputSize + outputSize + PLAYER_INVENTORY_SIZE, 0, inputSize, inputFilter) // inv to in
                .build();
    }

    public static MenuQuickMoveHelper inputOutputInventory(QuickMoveContext context, int[] inputSlots, int[] outputSlots) {
        return inputOutputInventory(context, inputSlots, stack -> true, outputSlots);
    }

    public ItemStack quickMove(Player player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        List<Slot> slots = context.getSlots();
        Slot slot = slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            stack = slotItem.copy();
            Rule rule = getRuleForSlot(slotIndex, slotItem);
            if (rule != null && !rule.move(slotItem, context.getItemStackMoveHelper())) {
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

    public interface QuickMoveContext {

        List<Slot> getSlots();

        SlotItemMoveHelper getItemStackMoveHelper();

        static QuickMoveContext of(Supplier<List<Slot>> slotProvider, SlotItemMoveHelper moveHelper) {
            return new QuickMoveContext() {
                @Override
                public List<Slot> getSlots() {
                    return slotProvider.get();
                }

                @Override
                public SlotItemMoveHelper getItemStackMoveHelper() {
                    return moveHelper;
                }
            };
        }
    }

    @FunctionalInterface
    public interface SlotItemMoveHelper {

        boolean moveItem(ItemStack stack, int fromIndex, int toIndex, boolean reverseOrder);
    }

    public static final class Builder {

        private final QuickMoveContext context;
        private final List<Rule> rules = new ArrayList<>();

        private Builder(QuickMoveContext context) {
            this.context = context;
        }

        public static Builder withContext(QuickMoveContext context) {
            return new Builder(context);
        }

        public Builder addRule(int fromMin, int fromMax, int toMin, int toMax, boolean reverseOrder, Predicate<ItemStack> filter) {
            this.rules.add(new Rule(fromMin, fromMax, toMin, toMax, reverseOrder, filter));
            return this;
        }

        public Builder addRule2(int fromMin, int fromMax, int toMin, int toMax, boolean reverseOrder, Predicate<ItemStack> filter) {
            this.rules.add(new Rule(fromMin, fromMax, toMin, toMax, reverseOrder, filter));
            this.rules.add(new Rule(toMin, toMax, fromMin, fromMax, reverseOrder, filter));
            return this;
        }

        public Builder addRule(int fromMin, int fromMax, int toMin, int toMax, Predicate<ItemStack> filter) {
            return addRule(fromMin, fromMax, toMin, toMax, false, filter);
        }

        public Builder addRule2(int fromMin, int fromMax, int toMin, int toMax, Predicate<ItemStack> filter) {
            return addRule2(fromMin, fromMax, toMin, toMax, false, filter);
        }

        public Builder addRule(int fromMin, int fromMax, int toMin, int toMax, boolean reverseOrder) {
            return addRule(fromMin, fromMax, toMin, toMax, reverseOrder, stack -> true);
        }

        public Builder addRule2(int fromMin, int fromMax, int toMin, int toMax, boolean reverseOrder) {
            return addRule2(fromMin, fromMax, toMin, toMax, reverseOrder, stack -> true);
        }

        public Builder addRule(int fromMin, int fromMax, int toMin, int toMax) {
            return addRule(fromMin, fromMax, toMin, toMax, false);
        }

        public Builder addRule2(int fromMin, int fromMax, int toMin, int toMax) {
            return addRule2(fromMin, fromMax, toMin, toMax, false);
        }

        public MenuQuickMoveHelper build() {
            return new MenuQuickMoveHelper(this);
        }
    }
}
