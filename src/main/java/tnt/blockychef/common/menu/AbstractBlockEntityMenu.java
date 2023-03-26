package tnt.blockychef.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import tnt.blockychef.util.MenuQuickMoveHelper;

public abstract class AbstractBlockEntityMenu<B extends BlockEntity> extends AbstractContainerMenu {

    protected final B blockEntity;
    protected final ContainerLevelAccess access;

    public AbstractBlockEntityMenu(MenuType<? extends AbstractBlockEntityMenu<B>> menuType, int menuId, B blockEntity) {
        super(menuType, menuId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    public B getBlockEntity() {
        return blockEntity;
    }

    @SuppressWarnings("unchecked")
    protected static <B extends BlockEntity> B resolveBlockEntityUnsafe(Inventory inventory, FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        Level level = inventory.player.getLevel();
        BlockEntity entity = level.getBlockEntity(pos);
        return (B) entity;
    }

    protected void addPlayerSlots(Inventory inventory, int startX, int startY) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(inventory, x + (y * 9) + 9, startX + x * 18, startY + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(inventory, x, startX + x * 18, startY + 58));
        }
    }

    protected MenuQuickMoveHelper.QuickMoveContext getQuickMoveContext() {
        return MenuQuickMoveHelper.QuickMoveContext.of(() -> slots, this::moveItemStackTo);
    }
}
