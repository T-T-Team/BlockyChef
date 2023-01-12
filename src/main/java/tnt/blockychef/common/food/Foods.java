package tnt.blockychef.common.food;

import net.minecraft.world.food.FoodProperties;

public final class Foods {

    public static FoodProperties TOMATO = food(1, 1).build();

    private static FoodProperties.Builder food(int nutrition, int saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation / (nutrition * 2.0F));
    }

    private Foods() {}
}
