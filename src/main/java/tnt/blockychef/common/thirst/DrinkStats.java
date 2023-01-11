package tnt.blockychef.common.thirst;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import tnt.blockychef.common.item.Drinkable;

import java.util.Optional;
import java.util.function.Consumer;

public record DrinkStats(int hydrationLevel, float saturation, Consumer<Player> onConsume) {

    public static final DrinkStats NONE = new DrinkStats(0);

    public DrinkStats(int hydrationLevel, float saturation) {
        this(hydrationLevel, saturation, player -> {});
    }

    public DrinkStats(int hydrationLevel, int saturation) {
        this(hydrationLevel, calculateSaturationForHydrationLevel(hydrationLevel, saturation));
    }

    public DrinkStats(int hydrationLevel) {
        this(hydrationLevel, calculateSaturationForHydrationLevel(hydrationLevel, hydrationLevel));
    }

    public boolean isEmpty() {
        return this == NONE;
    }

    public static float calculateSaturationForHydrationLevel(int waterLevel, int wantedSaturationLevel) {
        return wantedSaturationLevel / (waterLevel * 2.0F);
    }

    public static DrinkStats getDrinkStatistics(ItemStack stack) {
        Item item = stack.getItem();
        Optional<DrinkStats> optional = ConfigDrinkLoader.getStats(item);
        if (optional.isPresent()) {
            return adjustStats(optional.get(), stack);
        }
        if (item instanceof Drinkable drinkable) {
            return adjustStats(drinkable.getStats(), stack);
        }
        return NONE;
    }

    public static DrinkStats adjustStats(DrinkStats stats, ItemStack stack) {
        return stats; // TODO implement
    }
}
