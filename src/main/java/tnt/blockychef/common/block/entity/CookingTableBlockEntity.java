package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.init.BlockyChefBlockEntities;

public class CookingTableBlockEntity extends ColorableBlockEntity { // TODO propably won't have internal inventory

    public CookingTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.COOKING_TABLE, pos, state);
    }

    @Override
    public int getColorLayerCount() {
        return 2;
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(9);
    }
}
