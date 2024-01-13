package tnt.blockychef.common.food.recipe;

import net.minecraft.network.chat.Component;

import java.util.Locale;

public interface BaseCookConfiguration {

    int time();

    float minTemperature();

    float maxTemperature();

    float burnSpeed();

    default boolean isCooking(float temperature) {
        return temperature >= minTemperature();
    }

    default boolean isBurning(float temperature) {
        return temperature > maxTemperature();
    }

    default Component getTemperatureRange() {
        return Component.literal(String.format(Locale.ROOT, "%.1f-%.1f", minTemperature(), maxTemperature()));
    }
}
