package tnt.blockychef.common.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tnt.blockychef.BlockyChef;
import tnt.blockychef.common.fluid.EdibleFluidType;

public final class BlockyChefFluidTypes {

    private static final DeferredRegister<FluidType> REGISTER = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, BlockyChef.MODID);

    public static final RegistryObject<EdibleFluidType> ORANGE_FLUID = registerEdible("orange_fluid", 0xAAFF7328);
    public static final RegistryObject<EdibleFluidType> CHERRY_FLUID = registerEdible("cherry_fluid", 0xAABA000C);
    public static final RegistryObject<EdibleFluidType> OLIVE_OIL_FLUID = registerEdible("olive_oil_fluid", 0xAAC1B330);
    public static final RegistryObject<EdibleFluidType> OIL_FLUID = registerEdible("oil_fluid", 0xAAC6A933);
    public static final RegistryObject<EdibleFluidType> COCONUT_MILK_FLUID = registerEdible("coconut_milk_fluid", 0xAADBD8CE);
    public static final RegistryObject<EdibleFluidType> SOY_MILK_FLUID = registerEdible("soy_milk_fluid", 0xAAE5E0CC);
    public static final RegistryObject<EdibleFluidType> APPLE_FLUID = registerEdible("apple_fluid", 0xAAE2DC9E);
    public static final RegistryObject<EdibleFluidType> BLUEBERRY_FLUID = registerEdible("blueberry_fluid", 0xAA3C05BC);
    public static final RegistryObject<EdibleFluidType> CARROT_FLUID = registerEdible("carrot_fluid", 0xAACC6E1C);
    public static final RegistryObject<EdibleFluidType> GRAPE_FLUID = registerEdible("grape_fluid", 0xAA9500BF);
    public static final RegistryObject<EdibleFluidType> LEMON_FLUID = registerEdible("lemon_fluid", 0xAAEFE143);
    public static final RegistryObject<EdibleFluidType> LIME_FLUID = registerEdible("lime_fluid", 0xAAACE24D);
    public static final RegistryObject<EdibleFluidType> MELON_FLUID = registerEdible("melon_fluid", 0xAADD616B);
    public static final RegistryObject<EdibleFluidType> PEACH_FLUID = registerEdible("peach_fluid", 0xAADD703E);
    public static final RegistryObject<EdibleFluidType> PEAR_FLUID = registerEdible("pear_fluid", 0xAADDDD6E);
    public static final RegistryObject<EdibleFluidType> PINEAPPLE_FLUID = registerEdible("pineapple_fluid", 0xAADDAF2A);
    public static final RegistryObject<EdibleFluidType> PLUM_FLUID = registerEdible("plum_fluid", 0xAA8E1C9F);
    public static final RegistryObject<EdibleFluidType> RASPBERRY_FLUID = registerEdible("raspberry_fluid", 0xAAC60F40);
    public static final RegistryObject<EdibleFluidType> STRAWBERRY_FLUID = registerEdible("strawberry_fluid", 0xAAD83C3C);
    public static final RegistryObject<EdibleFluidType> SWEET_BERRY_FLUID = registerEdible("sweet_berry_fluid", 0xAA8C1D00);
    public static final RegistryObject<EdibleFluidType> TOMATO_FLUID = registerEdible("tomato_fluid", 0xAAAC2F0C);



    public static final RegistryObject<EdibleFluidType> ORANGE_JUICE_FLUID = registerEdible("orange_juice_fluid", 0xAAFF9966);
    public static final RegistryObject<EdibleFluidType> APPLE_JUICE_FLUID = registerEdible("apple_juice_fluid", 0xAAE0DBB1);
    public static final RegistryObject<EdibleFluidType> BLUEBERRY_JUICE_FLUID = registerEdible("blueberry_juice_fluid", 0xAA5C34BA);
    public static final RegistryObject<EdibleFluidType> CARROT_JUICE_FLUID = registerEdible("carrot_juice_fluid", 0xAACC8851);
    public static final RegistryObject<EdibleFluidType> CHERRY_JUICE_FLUID = registerEdible("cherry_juice_fluid", 0xAAB73A43);
    public static final RegistryObject<EdibleFluidType> GRAPE_JUICE_FLUID = registerEdible("grape_juice_fluid", 0xAA9E3CBC);
    public static final RegistryObject<EdibleFluidType> LEMONADE_FLUID = registerEdible("lemonade_fluid", 0xAAEDE374);
    public static final RegistryObject<EdibleFluidType> LIME_JUICE_FLUID = registerEdible("lime_juice_fluid", 0xAABDE07B);
    public static final RegistryObject<EdibleFluidType> MELON_JUICE_FLUID = registerEdible("melon_juice_fluid", 0xAADB858C);
    public static final RegistryObject<EdibleFluidType> PEACH_JUICE_FLUID = registerEdible("peach_juice_fluid", 0xAADB8864);
    public static final RegistryObject<EdibleFluidType> PEAR_JUICE_FLUID = registerEdible("pear_juice_fluid", 0xAADBDB9B);
    public static final RegistryObject<EdibleFluidType> PINEAPPLE_JUICE_FLUID = registerEdible("pineapple_juice_fluid", 0xAADBB95E);
    public static final RegistryObject<EdibleFluidType> PLUM_JUICE_FLUID = registerEdible("plum_juice_fluid", 0xAA93529E);
    public static final RegistryObject<EdibleFluidType> RASPBERRY_JUICE_FLUID = registerEdible("raspberry_juice_fluid", 0xAAD66471);
    public static final RegistryObject<EdibleFluidType> STRAWBERRY_JUICE_FLUID = registerEdible("strawberry_juice_fluid", 0xAAD65E5E);
    public static final RegistryObject<EdibleFluidType> TOMATO_JUICE_FLUID = registerEdible("tomato_juice_fluid", 0xAAAA472C);
    public static final RegistryObject<EdibleFluidType> VEGGIE_JUICE_FLUID = registerEdible("veggie_juice_fluid", 0xAA357029);
    public static final RegistryObject<EdibleFluidType> ICE_TEA_FLUID = registerEdible("ice_tea_fluid", 0xAAA89B39);
    public static final RegistryObject<EdibleFluidType> SWEET_BERRY_JUICE_FLUID = registerEdible("sweet_berry_juice_fluid", 0xAA893E2C);



    public static final RegistryObject<EdibleFluidType> BANANA_MILKSHAKE_FLUID = registerEdible("banana_milkshake_fluid", 0xAAFFF4CC);
    public static final RegistryObject<EdibleFluidType> BLUEBERRY_MILKSHAKE_FLUID = registerEdible("blueberry_milkshake_fluid", 0xAAE6DBFF);
    public static final RegistryObject<EdibleFluidType> CHOCOLATE_MILKSHAKE_FLUID = registerEdible("chocolate_milkshake_fluid", 0xAADDBC99);
    public static final RegistryObject<EdibleFluidType> COCONUT_MILKSHAKE_FLUID = registerEdible("coconut_milkshake_fluid", 0xAAFFF0E8);
    public static final RegistryObject<EdibleFluidType> NUTS_MILKSHAKE_FLUID = registerEdible("nuts_milkshake_fluid", 0xAAEAD7C5);
    public static final RegistryObject<EdibleFluidType> PINEAPPLE_MILKSHAKE_FLUID = registerEdible("pineapple_milkshake_fluid", 0xAAFFF2AD);
    public static final RegistryObject<EdibleFluidType> RASPBERRY_MILKSHAKE_FLUID = registerEdible("raspberry_milkshake_fluid", 0xAAFFCCCF);
    public static final RegistryObject<EdibleFluidType> STRAWBERRY_MILKSHAKE_FLUID = registerEdible("strawberry_milkshake_fluid", 0xAAFFCFC4);
    public static final RegistryObject<EdibleFluidType> VANILLA_MILKSHAKE_FLUID = registerEdible("vanilla_milkshake_fluid", 0xAAFFF2E0);
    public static final RegistryObject<EdibleFluidType> FRAPPE_COFFEE_FLUID = registerEdible("frappe_coffee_fluid", 0xAAEACEAF);
    public static final RegistryObject<EdibleFluidType> ICE_COFFEE_FLUID = registerEdible("ice_coffee_fluid", 0xAAE8C6A2);
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    private static RegistryObject<EdibleFluidType> registerEdible(String name, int color) {
        return REGISTER.register(name, () -> new EdibleFluidType(color));
    }
}
