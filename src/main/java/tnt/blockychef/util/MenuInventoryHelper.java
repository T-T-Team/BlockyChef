package tnt.blockychef.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import tnt.blockychef.common.block.entity.InventoryBlockEntity;
import tnt.blockychef.common.block.entity.RecipeRememberingBlockEntity;
import tnt.blockychef.util.function.TriConsumer;

import java.util.function.BiFunction;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

public final class MenuInventoryHelper {

    public static void dropRecipeBlockInventoryContentsAndAwardExp(BlockState state, Level level, BlockPos pos, BlockState replacementState) {
        if (!state.is(replacementState.getBlock())) {
            if (!level.isClientSide) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof RecipeRememberingBlockEntity<?> entity) {
                    entity.dropInventoryAndExp();
                }
            }
        }
    }

    public static void dropInventoryContents(Level level, BlockPos pos, InventoryBlockEntity blockEntity) {
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemhandler -> dropInventoryContents(level, pos, itemhandler));
    }

    public static void dropInventoryContents(Level level, BlockPos pos, IItemHandler itemHandler) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, x, y, z, stack);
            }
        }
    }

    public static void encodeInventory(IItemHandler handler, CompoundTag tag) {
        if (handler instanceof INBTSerializable<?> serializable) {
            Tag invTag = serializable.serializeNBT();
            if (invTag instanceof CompoundTag compoundTag) {
                tag.put("inventory", compoundTag);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void decodeInventory(IItemHandler handler, CompoundTag tag) {
        if (handler instanceof INBTSerializable<?> serializable) {
            INBTSerializable<CompoundTag> compoundTagINBTSerializable = (INBTSerializable<CompoundTag>) serializable;
            if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
                compoundTagINBTSerializable.deserializeNBT(tag.getCompound("inventory"));
            }
        }
    }

    public static boolean canFitItems(ItemStack[] items, Container container, int... validSlots) {
        int maxIndex = IntStream.of(validSlots).max().orElse(1);
        NonNullList<ItemStack> inventory = NonNullList.withSize(maxIndex + 1, ItemStack.EMPTY);
        for (int slotIndex : validSlots) {
            ItemStack stack = container.getItem(slotIndex);
            inventory.set(slotIndex, stack);
        }
        return insertItems(items, inventory, container::getMaxStackSize, NonNullList::get, NonNullList::set, validSlots);
    }

    public static void insertItems(ItemStack[] items, Container container, int... outputSlots) {
        insertItems(items, container, container::getMaxStackSize, Container::getItem, Container::setItem, outputSlots);
    }

    private static <T> boolean insertItems(ItemStack[] items, T t, IntSupplier maxSize, BiFunction<T, Integer, ItemStack> itemGetter, TriConsumer<T, Integer, ItemStack> itemSetter, int[] outputSlots) {
        for (ItemStack itemStack : items) {
            boolean result = insertItem(itemStack.copy(), t, maxSize, itemGetter, itemSetter, outputSlots);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    private static <T> boolean insertItem(ItemStack item, T target, IntSupplier maxSize, BiFunction<T, Integer, ItemStack> getter, TriConsumer<T, Integer, ItemStack> setter, int[] slots) {
        // Find best slot for item
        int max = maxSize.getAsInt();
        int toInsert = item.getCount();
        int slot = getSlotForItem(item, target, max, getter, setter, slots);
        if (slot == -1) {
            return false;
        }
        // Insert item
        ItemStack itemStack = getter.apply(target, slot);
        if (itemStack.isEmpty()) {
            ItemStack inserted = item.copy();
            inserted.setCount(Math.min(inserted.getCount(), Math.min(toInsert, max)));
            setter.accept(target, slot, inserted);
            toInsert -= inserted.getCount();
        } else if (ItemStack.isSameItem(itemStack, item)) {
            ItemStack inserted = item.copy();
            int emptySpace = Math.max(0, max - itemStack.getCount());
            int insertAmount = Math.min(emptySpace, toInsert);
            inserted.setCount(itemStack.getCount() + insertAmount);
            setter.accept(target, slot, inserted);
            toInsert -= insertAmount;
        }
        // If not everything was inserted, repeat
        if (toInsert > 0) {
            item.setCount(toInsert);
            return insertItem(item, target, maxSize, getter, setter, slots);
        }
        return true;
    }

    private static <T> int getSlotForItem(ItemStack stack, T target, int max, BiFunction<T, Integer, ItemStack> getter, TriConsumer<T, Integer, ItemStack> setter, int[] slots) {
        int slot = -1;
        for (int i : slots) {
            ItemStack itemStack = getter.apply(target, i);
            if (ItemStack.isSameItem(stack, itemStack)) {
                if (itemStack.getCount() < max) {
                    return i;
                }
            }
        }
        for (int i : slots) {
            ItemStack itemStack = getter.apply(target, i);
            if (itemStack.isEmpty()) {
                return i;
            }
        }
        return slot;
    }
}