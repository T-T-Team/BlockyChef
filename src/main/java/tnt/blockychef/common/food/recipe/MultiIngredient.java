package tnt.blockychef.common.food.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import tnt.blockychef.util.SerializationHelper;

public final class MultiIngredient {

    public static final Codec<MultiIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SerializationHelper.INGREDIENT_CODEC.fieldOf("ingredient").forGetter(t -> t.ingredient),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(t -> t.count)
    ).apply(instance, MultiIngredient::new));

    private final Ingredient ingredient;
    private final int count;

    private MultiIngredient(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public boolean test(Container container, int[] slots) {
        int remaining = count;
        for (int slot : slots) {
            ItemStack stack = container.getItem(slot);
            if (ingredient.test(stack)) {
                remaining -= stack.getCount();
                if (remaining <= 0)
                    break;
            }
        }
        return remaining <= 0;
    }

    public void consume(Container container, int[] slots) {
        int remaining = count;
        for (int slot : slots) {
            ItemStack stack = container.getItem(slot);
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
