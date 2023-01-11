package tnt.blockychef.common.food;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.common.thirst.DrinkProperties;

import java.util.function.BiFunction;

public enum FoodQuality {

    AWFUL(null, null),
    BAD(null, null),
    AVERAGE(Transformer.identity(), Transformer.identity()),
    GOOD(null, null),
    GREAT(null, null),
    EXCELLENT(null, null);

    private final Transformer<FoodProperties> foodTransformer;
    private final Transformer<DrinkProperties> drinkTransformer;

    FoodQuality(Transformer<FoodProperties> foodTransformer, Transformer<DrinkProperties> drinkTransformer) {
        this.foodTransformer = foodTransformer;
        this.drinkTransformer = drinkTransformer;
    }

    public static FoodQuality getItemQualityOrDefault(ItemStack stack, FoodQuality quality) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return quality;
        }
        CompoundTag food = tag.getCompound("blockychef.quality");
        if (food.contains("qualityIndex")) {
            int index = food.getInt("qualityIndex");
            FoodQuality[] values = FoodQuality.values();
            return values[index % values.length];
        }
        return quality;
    }

    public FoodProperties apply(FoodProperties food, ItemStack stack) {
        return this.foodTransformer.apply(food, stack);
    }

    public DrinkProperties apply(DrinkProperties drink, ItemStack stack) {
        return this.drinkTransformer.apply(drink, stack);
    }

    @FunctionalInterface
    private interface Transformer<T> extends BiFunction<T, ItemStack, T> {

        static <T> Transformer<T> identity() {
            return (t, stack) -> t;
        }
    }
}
