package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.init.BlockyChefBlockEntities;

public class KitchenCabinetBlockEntity extends ColorableBlockEntity {

    public KitchenCabinetBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.KITCHEN_CABINET, pos, state);
    }

    @Override
    public int getColorLayerCount() {
        return 1;
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(54);
    }
}
