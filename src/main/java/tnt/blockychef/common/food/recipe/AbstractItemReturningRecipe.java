package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tnt.blockychef.util.MenuInventoryHelper;
import tnt.blockychef.util.SerializationHelper;

import java.util.Collections;
import java.util.List;

public abstract class AbstractItemReturningRecipe<C extends Container> extends AbstractFoodRecipe<C> {

    private final List<ItemStack> containerItems;

    public AbstractItemReturningRecipe(ResourceLocation recipeId, float experience, List<ItemStack> containerItems) {
        super(recipeId, experience);
        this.containerItems = containerItems;
    }

    public static <R extends AbstractItemReturningRecipe<?>> RecordCodecBuilder<R, List<ItemStack>> resolveContainerItems() {
        return SerializationHelper.SIMPLE_ITEMSTACK_CODEC.listOf().optionalFieldOf("containerItems", Collections.emptyList())
                .forGetter(AbstractItemReturningRecipe::getContainerItems);
    }

    public abstract int[] getContainerSlots(C container);

    public List<ItemStack> getContainerItems() {
        return containerItems;
    }

    public void returnItemsToContainer(C container, Level level, BlockPos pos) {
        int[] slots = getContainerSlots(container);
        for (ItemStack stack : containerItems) {
            if (MenuInventoryHelper.canFitItems(new ItemStack[] {stack}, container, slots)) {
                MenuInventoryHelper.insertItems(new ItemStack[] {stack.copy()}, container, slots);
            } else {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy());
            }
        }
    }
}
