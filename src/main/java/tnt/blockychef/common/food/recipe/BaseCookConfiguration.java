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
        String s1 = String.format(Locale.ROOT, "%.2f", minTemperature());
        String s2 = String.format(Locale.ROOT, "%.2f", maxTemperature());
        return s1.equals(s2) ? Component.literal(s1) : Component.literal(s1 + "-" + s2);
    }
}
