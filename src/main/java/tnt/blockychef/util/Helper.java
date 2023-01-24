package tnt.blockychef.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import tnt.blockychef.common.block.entity.InventoryBlockEntity;

public final class Helper {

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createBlockEntityTicker(BlockEntityType<A> type1, BlockEntityType<E> type2, BlockEntityTicker<? super E> ticker) {
        return type1 == type2 ? (BlockEntityTicker<A>) ticker : null;
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
}
