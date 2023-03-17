package tnt.blockychef.common.food;

import com.google.common.base.Supplier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import tnt.blockychef.common.init.BlockyChefMobEffects;

import java.util.IdentityHashMap;
import java.util.Map;

public final class FoodList {

    public static final Map<FoodProperties, FoodProperties> FOOD_OVERRIDES = new IdentityHashMap<>();

    private static final Supplier<MobEffectInstance> HUNGER = () -> new MobEffectInstance(MobEffects.HUNGER, 300, 7);
    private static final float PROBABILITY = 0.15F;


    // Vanilla overrides
    public static final FoodProperties COOKED_BEEF = food(2, 2).build();
    public static final FoodProperties COOKED_PORKCHOP = food(2, 2).build();
    public static final FoodProperties COOKED_MUTTON = food(2, 2).build();
    public static final FoodProperties COOKED_CHICKEN = food(2, 2).build();
    public static final FoodProperties COOKED_RABBIT = food(2, 2).build();
    public static final FoodProperties COOKED_COD = food(2, 2).build();
    public static final FoodProperties COOKED_SALMON = food(2, 2).build();
    public static final FoodProperties PUMPKIN_PIE = food(2, 2).build();
    public static final FoodProperties MUSHROOM_STEW = food(2, 1).build();
    public static final FoodProperties BEETROOT_SOUP = food(2, 2).build();
    public static final FoodProperties RABBIT_STEW = food(3, 2).build();
    public static final FoodProperties SUSPICIOUS_STEW = food(2, 2).build();
    public static final FoodProperties BREAD = food(2, 2).build();
    public static final FoodProperties HONEY_BOTTLE = food(2, 2).build();
    public static final FoodProperties APPLE = food(1, 1).build();
    public static final FoodProperties BEEF = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties PORKCHOP = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties CHICKEN = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties MUTTON = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties RABBIT = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties SALMON = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties COD = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties TROPICAL_FISH = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties SWEET_BERRIES = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties GLOW_BERRIES = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties CHORUS_FRUIT = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties POTATO = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties BEETROOT = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties POISONOUS_POTATO = food(1, 1).effect(HUNGER, 0.5F).build();
    public static final FoodProperties ROTTEN_FLESH = food(2, 1).effect(HUNGER, 0.75F).build();
    public static final FoodProperties SPIDER_EYE = food(1, 1).effect(HUNGER, 0.95F).build();
    public static final FoodProperties GOLDEN_APPLE = food(2, 2).build();
    public static final FoodProperties DRIED_KELP = food(1, 1).build();
    public static final FoodProperties BAKED_POTATO = food(2, 2).build();
    public static final FoodProperties ENCHANTED_GOLDEN_APPLE = food(2, 2).build();
    public static final FoodProperties GOLDEN_CARROT = food(2, 2).build();
    public static final FoodProperties COOKIE = food(1, 1).build();
    public static final FoodProperties MELON_SLICE = food(1, 1).build();
    public static final FoodProperties CARROT = food(1, 1).effect(HUNGER, PROBABILITY).build();
    // Blockychef foods
    public static final FoodProperties TOMATO = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties CUCUMBER = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties ALMOND = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties BELL_PEPPER = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties BLACK_PEPPER = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties BLUEBERRY = food(1, 1).build();
    public static final FoodProperties CHILLI_PEPPER = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties BROCCOLI = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties CABBAGE = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties CORN = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties GARLIC = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties LEEK = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties LETTUCE = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties ONION = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties PARSLEY = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties RASPBERRY = food(1, 1).build();
    public static final FoodProperties SPRING_ONION = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties STRAWBERRY = food(1, 1).build();
    public static final FoodProperties TURMERIC = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties PINEAPPLE = food(1, 1).build();
    public static final FoodProperties PORTOBELLO = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties BEANS = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties PEANUTS = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties PEAS = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties SOYBEANS = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties BASIL = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties GINGER = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties TEA = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties GRAPES = food(1, 1).build();
    public static final FoodProperties RICE = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties OAT = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties MUSTARD = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties COFFEE = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties EGGPLANT = food(2, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties SPINACH_LEAF = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties AVOCADO = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties BANANA = food(1, 1).build();
    public static final FoodProperties BLACK_OLIVES = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties CHERRY = food(1, 1).build();
    public static final FoodProperties GREEN_OLIVES = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties HAZELNUT = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties LEMON = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties LIME = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties ORANGE = food(1, 1).build();
    public static final FoodProperties PEACH = food(1, 1).build();
    public static final FoodProperties PEAR = food(1, 1).build();
    public static final FoodProperties PLUM = food(1, 1).build();
    public static final FoodProperties VANILLA_PODS = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties WALNUT = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties SHRIMP = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties RAW_SHRIMP = food(1, 1).effect(HUNGER, PROBABILITY).build();
    public static final FoodProperties FRIED_SHRIMP = food(2, 1).build();

    private static FoodProperties.Builder food(int nutrition, int saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation / (nutrition * 2.0F));
    }

    private FoodList() {
    }

    static {
        FOOD_OVERRIDES.put(Foods.COOKED_BEEF, COOKED_BEEF);
        FOOD_OVERRIDES.put(Foods.CARROT, CARROT);
        FOOD_OVERRIDES.put(Foods.BREAD, BREAD);
        FOOD_OVERRIDES.put(Foods.APPLE, APPLE);
        FOOD_OVERRIDES.put(Foods.GOLDEN_APPLE, GOLDEN_APPLE);
        FOOD_OVERRIDES.put(Foods.ENCHANTED_GOLDEN_APPLE, ENCHANTED_GOLDEN_APPLE);
        FOOD_OVERRIDES.put(Foods.MELON_SLICE, MELON_SLICE);
        FOOD_OVERRIDES.put(Foods.SWEET_BERRIES, SWEET_BERRIES);
        FOOD_OVERRIDES.put(Foods.GLOW_BERRIES, GLOW_BERRIES);
        FOOD_OVERRIDES.put(Foods.CHORUS_FRUIT, CHORUS_FRUIT);
        FOOD_OVERRIDES.put(Foods.GOLDEN_CARROT, GOLDEN_CARROT);
        FOOD_OVERRIDES.put(Foods.POTATO, POTATO);
        FOOD_OVERRIDES.put(Foods.BAKED_POTATO, BAKED_POTATO);
        FOOD_OVERRIDES.put(Foods.POISONOUS_POTATO, POISONOUS_POTATO);
        FOOD_OVERRIDES.put(Foods.BEETROOT, BEETROOT);
        FOOD_OVERRIDES.put(Foods.DRIED_KELP, DRIED_KELP);
        FOOD_OVERRIDES.put(Foods.BEEF, BEEF);
        FOOD_OVERRIDES.put(Foods.PORKCHOP, PORKCHOP);
        FOOD_OVERRIDES.put(Foods.COOKED_PORKCHOP, COOKED_PORKCHOP);
        FOOD_OVERRIDES.put(Foods.MUTTON, MUTTON);
        FOOD_OVERRIDES.put(Foods.COOKED_MUTTON, COOKED_MUTTON);
        FOOD_OVERRIDES.put(Foods.CHICKEN, CHICKEN);
        FOOD_OVERRIDES.put(Foods.COOKED_CHICKEN, COOKED_CHICKEN);
        FOOD_OVERRIDES.put(Foods.RABBIT, RABBIT);
        FOOD_OVERRIDES.put(Foods.COOKED_RABBIT, COOKED_RABBIT);
        FOOD_OVERRIDES.put(Foods.COD, COD);
        FOOD_OVERRIDES.put(Foods.COOKED_COD, COOKED_COD);
        FOOD_OVERRIDES.put(Foods.SALMON, SALMON);
        FOOD_OVERRIDES.put(Foods.COOKED_SALMON, COOKED_SALMON);
        FOOD_OVERRIDES.put(Foods.TROPICAL_FISH, TROPICAL_FISH);
        FOOD_OVERRIDES.put(Foods.COOKIE, COOKIE);
        FOOD_OVERRIDES.put(Foods.PUMPKIN_PIE, PUMPKIN_PIE);
        FOOD_OVERRIDES.put(Foods.ROTTEN_FLESH, ROTTEN_FLESH);
        FOOD_OVERRIDES.put(Foods.SPIDER_EYE, SPIDER_EYE);
        FOOD_OVERRIDES.put(Foods.MUSHROOM_STEW, MUSHROOM_STEW);
        FOOD_OVERRIDES.put(Foods.BEETROOT_SOUP, BEETROOT_SOUP);
        FOOD_OVERRIDES.put(Foods.RABBIT_STEW, RABBIT_STEW);
        FOOD_OVERRIDES.put(Foods.SUSPICIOUS_STEW, SUSPICIOUS_STEW);
        FOOD_OVERRIDES.put(Foods.HONEY_BOTTLE, HONEY_BOTTLE);
    }
}
