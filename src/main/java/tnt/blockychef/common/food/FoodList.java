package tnt.blockychef.common.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;

import java.util.IdentityHashMap;
import java.util.Map;

public final class FoodList {

    public static final Map<FoodProperties, FoodProperties> FOOD_OVERRIDES = new IdentityHashMap<>();
    public static final FoodProperties TOMATO = food(1, 1).build();

    private static FoodProperties.Builder food(int nutrition, int saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation / (nutrition * 2.0F));
    }

    private FoodList() {
    }

    static {
        FOOD_OVERRIDES.put(Foods.COOKED_BEEF, TOMATO);
    }
}
