package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import tnt.blockychef.common.init.BlockyChefBlockEntities;
import tnt.blockychef.util.Helper;

public class DryingRackBlockEntity extends InventoryBlockEntity {

    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.DRYING_RACK, pos, state);
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(1);
    }

    public void setItem(ItemStack stack) {
        this.inventoryHandler.setStackInSlot(0, stack);
    }

    public boolean hasItem() {
        return !this.inventoryHandler.getStackInSlot(0).isEmpty();
    }

    public void clearInventoryAndProcessRecipe(@Nullable Player player) {
        if (player == null) {
            // TODO drop exp on ground
            Helper.dropInventoryContents(level, worldPosition, inventoryHandler);
        } else {
            ItemStack stack = inventoryHandler.getStackInSlot(0);
            if (!stack.isEmpty()) {
                Helper.giveItem(player, stack);
            }
            // TODO implement exp
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DryingRackBlockEntity dryingRack) {
        // TODO tick drying recipes
    }
}
