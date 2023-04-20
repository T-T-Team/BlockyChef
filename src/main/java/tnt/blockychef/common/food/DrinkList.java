package tnt.blockychef.common.food;

import com.google.common.base.Supplier;
import net.minecraft.world.effect.MobEffectInstance;
import tnt.blockychef.common.init.BlockyChefMobEffects;
import tnt.blockychef.common.thirst.DrinkProperties;

public final class DrinkList {

    private static final Supplier<MobEffectInstance> THIRST = () -> new MobEffectInstance(BlockyChefMobEffects.THIRST, 300, 0);
    private static final float PROBABILITY = 0.15F;


    public static final DrinkProperties TOMATO = DrinkProperties.Builder.create().stats(1, 1).build();
    public static final DrinkProperties ALMOND = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BELL_PEPPER = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BLACK_PEPPER = DrinkProperties.Builder.create().stats(-3, 0).build();
    public static final DrinkProperties CHILLI_PEPPER = DrinkProperties.Builder.create().stats(-4, 0).build();
    public static final DrinkProperties BROCCOLI = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties CABBAGE = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties CORN_COB = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties GARLIC = DrinkProperties.Builder.create().stats(-4, 0).build();
    public static final DrinkProperties LEEK = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties LETTUCE = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties ONION = DrinkProperties.Builder.create().stats(-3, 0).build();
    public static final DrinkProperties PARSLEY = DrinkProperties.Builder.create().stats(-2, 0).build();
    public static final DrinkProperties SPRING_ONION = DrinkProperties.Builder.create().stats(-2, 0).build();
    public static final DrinkProperties TURMERIC = DrinkProperties.Builder.create().stats(-3, 0).build();
    public static final DrinkProperties PINEAPPLE = DrinkProperties.Builder.create().stats(1, 2).build();
    public static final DrinkProperties PORTOBELLO = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BEANS_POD = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties PEANUTS = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties PEAS = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties SOYBEANS = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BASIL = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties GINGER = DrinkProperties.Builder.create().stats(-3, 0).build();
    public static final DrinkProperties TEA = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties GRAPES = DrinkProperties.Builder.create().stats(1, 2).build();
    public static final DrinkProperties RICE = DrinkProperties.Builder.create().stats(-2, 0).build();
    public static final DrinkProperties OAT = DrinkProperties.Builder.create().stats(-2, 0).build();
    public static final DrinkProperties MUSTARD = DrinkProperties.Builder.create().stats(-2, 0).build();
    public static final DrinkProperties COFFEE = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties EGGPLANT = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties SPINACH_LEAF = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties AVOCADO = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BANANA = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BLACK_OLIVES = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties GREEN_OLIVES = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties HAZELNUT = DrinkProperties.Builder.create().stats(-2, 0).build();
    public static final DrinkProperties ORANGE = DrinkProperties.Builder.create().stats(1, 1).build();
    public static final DrinkProperties VANILLA_PODS = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties WALNUT = DrinkProperties.Builder.create().stats(-2, 0).build();
    public static final DrinkProperties FRIED_SHRIMP = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties APPLE_EXTRACT = DrinkProperties.Builder.create().stats(3, 2).build();
    public static final DrinkProperties APPLE_MARMALADE = DrinkProperties.Builder.create().stats(-2, 0).build();
    public static final DrinkProperties AVOCADO_SLICE = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BANANA_SLICE = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BBQ_SAUCE = DrinkProperties.Builder.create().stats(-3, 0).build();
    public static final DrinkProperties BEANS = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BEEF_BURGER_PATTY = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BELL_PEPPER_SLICE = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BLACK_OLIVE_SLICE = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BLUEBERRY_EXTRACT = DrinkProperties.Builder.create().stats(3, 2).build();
    public static final DrinkProperties BLUEBERRY_MARMALADE = DrinkProperties.Builder.create().stats(-2, 0).build();
    public static final DrinkProperties BOILED_RICE = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties BREAD_SLICE = DrinkProperties.Builder.create().stats(-2, 0).build();
    public static final DrinkProperties BROCCOLI_CUTS = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties OIL = DrinkProperties.Builder.create().stats(2, 2).giveSingleEffect(PROBABILITY, THIRST).build();
    public static final DrinkProperties CABBAGE_LEAF = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties CARROT_EXTRACT = DrinkProperties.Builder.create().stats(3, 2).build();
    public static final DrinkProperties CHERRY_EXTRACT = DrinkProperties.Builder.create().stats(3, 2).build();
    public static final DrinkProperties CHERRY_MARMALADE = DrinkProperties.Builder.create().stats(-2, 0).build();
    public static final DrinkProperties CHICKEN_BURGER_PATTY = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties COCOA_SPREAD = DrinkProperties.Builder.create().stats(-2, 0).build();
    public static final DrinkProperties COCONUT_MILK = DrinkProperties.Builder.create().stats(3, 2).build();
    public static final DrinkProperties COFFEE_BEANS = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties CORN = DrinkProperties.Builder.create().stats(-1, 0).build();
    public static final DrinkProperties CORN_MEAL = DrinkProperties.Builder.create().stats(-3, 0).build();

    private DrinkList() {}
}
