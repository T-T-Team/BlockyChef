package tnt.tntlib.api.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public abstract class AbstractBlockEntityMenu<B extends BlockEntity> extends AbstractContainerMenu {

    protected final B blockEntity;
    protected final ContainerLevelAccess access;

    public AbstractBlockEntityMenu(MenuType<? extends AbstractBlockEntityMenu<B>> menuType, int menuId, B blockEntity) {
        super(menuType, menuId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    public static boolean stillValid(ContainerLevelAccess access, Player player, Predicate<BlockState> stateValidator) {
        return access.evaluate((level, pos) -> {
            BlockState state = level.getBlockState(pos);
            if (stateValidator.test(state)) {
                return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
            }
            return false;
        }, true);
    }

    public B getBlockEntity() {
        return blockEntity;
    }

    @SuppressWarnings("unchecked")
    protected static <B extends BlockEntity> B resolveBlockEntityUnsafe(Inventory inventory, FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        Level level = inventory.player.level();
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
