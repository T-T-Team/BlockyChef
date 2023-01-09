package tnt.blockychef.common.thirst;

import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record DrinkStats(int hydrationLevel, float saturation, Consumer<Player> onConsume) {

    public DrinkStats(int hydrationLevel, float saturation) {
        this(hydrationLevel, saturation, player -> {});
    }

    public DrinkStats(int hydrationLevel, int saturation) {
        this(hydrationLevel, calculateSaturationForHydrationLevel(hydrationLevel, saturation));
    }

    public DrinkStats(int hydrationLevel) {
        this(hydrationLevel, calculateSaturationForHydrationLevel(hydrationLevel, hydrationLevel));
    }

    public static float calculateSaturationForHydrationLevel(int waterLevel, int wantedSaturationLevel) {
        return wantedSaturationLevel / (waterLevel * 2.0F);
    }
}
