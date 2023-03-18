package tnt.blockychef.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tnt.blockychef.common.food.recipe.CuttingBoardRecipe;
import tnt.blockychef.common.init.BlockyChefBlockEntities;

public class CuttingBoardBlockEntity extends RecipeRemberingBlockEntity<CuttingBoardRecipe> implements SynchronizableBlockEntity {

    public static final int SLOT_INPUT = 0;
    public static final int[] SLOT_OUTPUTS = { 1, 2, 3 };

    public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(BlockyChefBlockEntities.CUTTING_BOARD, pos, state);
    }

    public ItemStack getInputItem() {
        return this.getItem(SLOT_INPUT);
    }

    public NonNullList<ItemStack> getOutputItems() {
        NonNullList<ItemStack> list = NonNullList.withSize(3, ItemStack.EMPTY);
        for (int i : SLOT_OUTPUTS) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty()) {
                list.set(i, stack);
            }
        }
        return list;
    }

    @Override
    public IItemHandlerModifiable setUpInventory() {
        return new ItemStackHandler(4);
    }

    @Override
    public void encodeBlockEntityData(CompoundTag tag) {

    }

    @Override
    public void decodeBlockEntityData(CompoundTag tag) {

    }
}
