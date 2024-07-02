package tnt.blockychef.common.food.mastery;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import tnt.blockychef.common.thirst.DrinkProperties;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public enum FoodQuality {

    BAD(-1, -1, ChatFormatting.RED),
    AVERAGE(0, 0, ChatFormatting.WHITE),
    GOOD(1, 0, ChatFormatting.GREEN),
    GREAT(1, 1, ChatFormatting.BLUE),
    PERFECT(2, 2, ChatFormatting.GOLD);

    private final int levelAdjustment;
    private final int saturationAdjustment;
    private final Component label;

    FoodQuality(int levelAdjustment, int saturationAdjustment, ChatFormatting formatting) {
        this.levelAdjustment = levelAdjustment;
        this.saturationAdjustment = saturationAdjustment;
        this.label = Component.translatable("blockychef.food.quality." + name().toLowerCase(Locale.ROOT)).withStyle(formatting);
    }

    public int getLevelAdjustment() {
        return levelAdjustment;
    }

    public int getSaturationAdjustment() {
        return saturationAdjustment;
    }

    public Component getLabel() {
        return label;
    }

    public static FoodQuality resolve(int id) {
        return values()[Mth.clamp(id, 0, values().length - 1)];
    }

    public FoodProperties applyFood(FoodProperties base) {
        FoodProperties.Builder props = new FoodProperties.Builder();
        int foodLevel = base.getNutrition() + this.levelAdjustment;
        int saturationLevel = DrinkProperties.calculateIntegerSaturation(base.getNutrition(), base.getSaturationModifier());
        float adjustedSaturation = DrinkProperties.calculateSaturation(foodLevel, saturationLevel + this.saturationAdjustment);
        boolean isMeat = base.isMeat();
        boolean isFast = base.isFastFood();
        boolean alwaysEdible = base.canAlwaysEat();
        List<Pair<MobEffectInstance, Float>> effects = base.getEffects();
        props.nutrition(foodLevel).saturationMod(adjustedSaturation);
        if (isMeat) {
            props.meat();
        }
        if (isFast) {
            props.fast();
        }
        if (alwaysEdible) {
            props.alwaysEat();
        }
        effects.forEach(p -> props.effect(p::getFirst, p.getSecond()));
        return props.build();
    }

    public DrinkProperties applyDrink(DrinkProperties base) {
        return base.adjustForQuality(this);
    }
}
