package tnt.blockychef.util;

import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import tnt.blockychef.common.block.entity.InventoryBlockEntity;
import tnt.blockychef.common.block.entity.SynchronizableBlockEntity;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.packet.S2C_SendBlockEntityData;
import tnt.blockychef.util.function.TriConsumer;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;

public final class Helper {

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createBlockEntityTicker(BlockEntityType<A> typeToTest, BlockEntityType<E> wantedType, BlockEntityTicker<? super E> ticker) {
        return typeToTest == wantedType ? (BlockEntityTicker<A>) ticker : null;
    }

    public static void dropInventoryContents(Level level, BlockPos pos) {
        if (level.isClientSide)
            return;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof InventoryBlockEntity inventoryBlockEntity) {
            dropInventoryContents(level, pos, inventoryBlockEntity);
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

    public static void giveItem(Player player, ItemStack stack) {
        if (player.level.isClientSide)
            return;
        Inventory inventory = player.getInventory();
        if (!inventory.add(stack)) {
            Containers.dropItemStack(player.level, player.getX(), player.getY(), player.getZ(), stack);
        }
    }

    public static <B extends BlockEntity & SynchronizableBlockEntity> void sendBlockEntityClientData(B blockEntity) {
        Level level = Objects.requireNonNull(blockEntity, "blockEntity cannot be null").getLevel();
        if (level == null || level.isClientSide)
            return;
        NetworkManager.dispatchClientLevelPacket(level, S2C_SendBlockEntityData.createUpdatePacket(blockEntity));
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

    public static boolean canFitItems(ItemStack[] items, Container container, int[] validSlots) {
        NonNullList<ItemStack> inventory = NonNullList.withSize(validSlots.length, ItemStack.EMPTY);
        for (int i = 0; i < inventory.size(); i++) {
            int slotIndex = validSlots[i];
            ItemStack stack = container.getItem(slotIndex);
            inventory.set(i, stack);
        }
        return insertItems(items, inventory, inventory::size, NonNullList::get, NonNullList::set, validSlots);
    }

    public static void insertItems(ItemStack[] items, Container container, int[] outputSlots) {
        insertItems(items, container, container::getContainerSize, Container::getItem, Container::setItem, outputSlots);
    }

    // TODO prioritize merge of same items first
    private static <T> boolean insertItems(ItemStack[] items, T t, IntSupplier maxSize, BiFunction<T, Integer, ItemStack> itemGetter, TriConsumer<T, Integer, ItemStack> itemSetter, int[] outputSlots) {
        for (ItemStack itemStack : items) {
            int limit = Math.min(maxSize.getAsInt(), itemStack.getMaxStackSize());
            int toPlace = itemStack.getCount();
            for (int i : outputSlots) {
                ItemStack stack = itemGetter.apply(t, i);
                if (stack.isEmpty()) {
                    int placed = Math.min(limit, toPlace);
                    ItemStack item = itemStack.copy();
                    item.setCount(placed);
                    itemSetter.accept(t, i, item);
                    toPlace -= placed;
                } else if (ItemStack.isSame(stack, itemStack)) {
                    int placed = Math.min(toPlace, limit - stack.getCount());
                    stack.setCount(stack.getCount() + placed);
                    toPlace -= placed;
                }
                if (toPlace == 0) {
                    break;
                }
            }
            if (toPlace != 0) {
                return false;
            }
        }
        return true;
    }
}
