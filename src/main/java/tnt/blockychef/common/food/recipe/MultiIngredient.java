package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import tnt.tntlib.api.serialization.Codecs;

import java.util.ArrayList;
import java.util.List;

public final class MultiIngredient {

    public static final Codec<MultiIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.INGREDIENT.fieldOf("ingredient").forGetter(t -> t.ingredient),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(t -> t.count)
    ).apply(instance, MultiIngredient::new));

    private final Ingredient ingredient;
    private final int count;

    private MultiIngredient(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public boolean acceptsItem(ItemStack itemStack) {
        List<ItemStack> itemStacks = toItemStackList();
        for (ItemStack stack : itemStacks) {
            if (!ItemStack.isSameItem(stack, itemStack)) {
                return false;
            }
        }
        return true;
    }

    public List<ItemStack> toItemStackList() {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack stack : ingredient.getItems()) {
            ItemStack itemStack = stack.copy();
            itemStack.setCount(count);
            list.add(itemStack);
        }
        return list;
    }

    public static boolean test(Container container, int[] slots, List<MultiIngredient> ingredients) {
        for (int slot : slots) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty())
                continue;
            boolean validItemStack = false;
            for (MultiIngredient ingredient : ingredients) {
                if (ingredient.ingredient.test(stack)) {
                    validItemStack = true;
                }
            }
            if (!validItemStack) {
                return false;
            }
        }
        for (MultiIngredient ingredient : ingredients) {
            if (!ingredient.test(container, slots)) {
                return false;
            }
        }
        return true;
    }

    public boolean test(Container container, int[] slots) {
        return test(container, slots, false);
    }

    private boolean test(Container container, int[] slots, boolean ignoreCount) {
        int remaining = count;
        for (int slot : slots) {
            ItemStack stack = container.getItem(slot);
            if (ingredient.test(stack)) {
                if (ignoreCount)
                    return true;
                remaining -= stack.getCount();
                if (remaining <= 0)
                    break;
            }
        }
        return remaining <= 0;
    }

    public List<ItemStack> consume(Container container, int[] slots) {
        List<ItemStack> consumed = new ArrayList<>();
        int remaining = count;
        for (int slot : slots) {
            ItemStack stack = container.getItem(slot);
            if (ingredient.test(stack)) {
                int toConsume = Math.min(remaining, stack.getCount());
                ItemStack consumedItem = stack.copy();
                consumedItem.setCount(toConsume);
                consumed.add(consumedItem);
                stack.shrink(toConsume);
                remaining -= toConsume;
                if (remaining <= 0) {
                    break;
                }
            }
        }
        return consumed;
    }

    public void consume(List<ItemStack> list) {
        int remaining = count;
        for (ItemStack stack : list) {
            if (ingredient.test(stack)) {
                int toConsume = Math.min(remaining, stack.getCount());
                stack.shrink(toConsume);
                remaining -= toConsume;
                if (remaining <= 0) {
                    break;
                }
            }
        }
    }
}
