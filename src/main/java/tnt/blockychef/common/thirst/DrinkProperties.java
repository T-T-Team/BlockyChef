package tnt.blockychef.common.thirst;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import tnt.blockychef.common.item.Drinkable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class DrinkProperties {

    public static final DrinkProperties NONE = Builder.create().onDrink(p -> {
        throw new UnsupportedOperationException("Operation on empty drink properties");
    }).build();
    public static final DrinkPropertiesHolder NONE_HOLDER = new DrinkPropertiesHolder(NONE, ItemStack.EMPTY);
    private final int hydrationLevel;
    private final float saturation;
    private final boolean alwaysDrinkable;
    private final Consumer<Player> onConsumed;

    private DrinkProperties(Builder builder) {
        this.hydrationLevel = builder.hydration;
        this.saturation = Math.max(builder.saturation, 0.0F);
        this.alwaysDrinkable = builder.alwaysDrinkable;
        this.onConsumed = builder.onDrink;
    }

    public int getHydration() {
        return this.hydrationLevel;
    }

    public float getSaturation() {
        return this.saturation;
    }

    public boolean isAlwaysDrinkable() {
        return this.alwaysDrinkable;
    }

    public void onConsumed(Player player) {
        this.onConsumed.accept(player);
    }

    public boolean isEmpty() {
        return this == NONE;
    }

    public static float calculateSaturationForHydrationLevel(int waterLevel, int wantedSaturationLevel) {
        return wantedSaturationLevel / (waterLevel * 2.0F);
    }

    public static DrinkPropertiesHolder getDrinkStatistics(ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.AIR)
            return NONE_HOLDER;
        Optional<DrinkPropertiesHolder> optional = ConfigDrinkLoader.getStatsHolder(item);
        if (optional.isPresent()) {
            DrinkPropertiesHolder holder = optional.get();
            DrinkProperties properties = adjustStats(holder.properties(), stack);
            return new DrinkPropertiesHolder(properties, holder.returningItem());
        }
        if (item instanceof Drinkable drinkable) {
            DrinkProperties properties = drinkable.getStats();
            ItemStack returning = drinkable.getReturningItem();
            return new DrinkPropertiesHolder(adjustStats(properties, stack), returning);
        }
        return NONE_HOLDER;
    }

    public static DrinkProperties adjustStats(DrinkProperties stats, ItemStack stack) {
        return stats; // TODO implement
    }

    public static final class Builder {

        private int hydration;
        private float saturation;
        private boolean alwaysDrinkable;
        private Consumer<Player> onDrink = player -> {};

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder stats(int hydration, float saturation) {
            this.hydration = hydration;
            this.saturation = saturation;
            return this;
        }

        public Builder stats(int hydration, int saturation) {
            return this.stats(hydration, calculateSaturationForHydrationLevel(hydration, saturation));
        }

        public Builder stats(int hydration) {
            return this.stats(hydration, 0.5F);
        }

        public Builder setAlwaysDrinkable() {
            this.alwaysDrinkable = true;
            return this;
        }

        public Builder onDrink(Consumer<Player> onDrink) {
            this.onDrink = onDrink;
            return this;
        }

        public Builder giveSingleEffect(float chance, Supplier<MobEffectInstance> effectProvider) {
            return this.onDrink(player -> {
                RandomSource source = player.getRandom();
                if (!player.level().isClientSide && source.nextFloat() < chance) {
                    player.addEffect(effectProvider.get());
                }
            });
        }

        public DrinkProperties build() {
            Objects.requireNonNull(onDrink);
            return new DrinkProperties(this);
        }
    }

    public record DrinkPropertiesHolder(DrinkProperties properties, ItemStack returningItem) {}
}
