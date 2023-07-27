package tnt.blockychef.common.init;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tnt.blockychef.BlockyChef;

import java.util.function.Supplier;

public final class BlockyChefFluids {

    private static final DeferredRegister<Fluid> REGISTER = DeferredRegister.create(ForgeRegistries.FLUIDS, BlockyChef.MODID);

    public static final RegistryObject<Fluid> ORANGE_FLUID = registerSource("orange", () -> BlockyChefFluids.ORANGE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> CHERRY_FLUID = registerSource("cherry", () -> BlockyChefFluids.CHERRY_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> OLIVE_OIL_FLUID = registerSource("olive_oil", () -> BlockyChefFluids.OLIVE_OIL_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> OIL_FLUID = registerSource("oil", () -> BlockyChefFluids.OIL_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> COCONUT_MILK_FLUID = registerSource("coconut_milk", () -> BlockyChefFluids.COCONUT_MILK_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> SOY_MILK_FLUID = registerSource("soy_milk", () -> BlockyChefFluids.SOY_MILK_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> APPLE_FLUID = registerSource("apple", () -> BlockyChefFluids.APPLE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> BLUEBERRY_FLUID = registerSource("blueberry", () -> BlockyChefFluids.BLUEBERRY_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> CARROT_FLUID = registerSource("carrot", () -> BlockyChefFluids.CARROT_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> GRAPE_FLUID = registerSource("grape", () -> BlockyChefFluids.GRAPE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> LEMON_FLUID = registerSource("lemon", () -> BlockyChefFluids.LEMON_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> LIME_FLUID = registerSource("lime", () -> BlockyChefFluids.LIME_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> MELON_FLUID = registerSource("melon", () -> BlockyChefFluids.MELON_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PEACH_FLUID = registerSource("peach", () -> BlockyChefFluids.PEACH_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PEAR_FLUID = registerSource("pear", () -> BlockyChefFluids.PEAR_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PINEAPPLE_FLUID = registerSource("pineapple", () -> BlockyChefFluids.PINEAPPLE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PLUM_FLUID = registerSource("plum", () -> BlockyChefFluids.PLUM_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> RASPBERRY_FLUID = registerSource("raspberry", () -> BlockyChefFluids.RASPBERRY_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> STRAWBERRY_FLUID = registerSource("strawberry", () -> BlockyChefFluids.STRAWBERRY_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> SWEET_BERRY_FLUID = registerSource("sweet_berry", () -> BlockyChefFluids.SWEET_BERRY_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> TOMATO_FLUID = registerSource("tomato", () -> BlockyChefFluids.TOMATO_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> ORANGE_JUICE_FLUID = registerSource("orange_juice", () -> BlockyChefFluids.ORANGE_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> APPLE_JUICE_FLUID = registerSource("apple_juice", () -> BlockyChefFluids.APPLE_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> BLUEBERRY_JUICE_FLUID = registerSource("blueberry_juice", () -> BlockyChefFluids.BLUEBERRY_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> CARROT_JUICE_FLUID = registerSource("carrot_juice", () -> BlockyChefFluids.CARROT_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> CHERRY_JUICE_FLUID = registerSource("cherry_juice", () -> BlockyChefFluids.CHERRY_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> GRAPE_JUICE_FLUID = registerSource("grape_juice", () -> BlockyChefFluids.GRAPE_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> LEMONADE_FLUID = registerSource("lemonade", () -> BlockyChefFluids.LEMONADE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> LIME_JUICE_FLUID = registerSource("lime_juice", () -> BlockyChefFluids.LIME_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> MELON_JUICE_FLUID = registerSource("melon_juice", () -> BlockyChefFluids.MELON_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PEACH_JUICE_FLUID = registerSource("peach_juice", () -> BlockyChefFluids.PEACH_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PEAR_JUICE_FLUID = registerSource("pear_juice", () -> BlockyChefFluids.PEAR_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PINEAPPLE_JUICE_FLUID = registerSource("pineapple_juice", () -> BlockyChefFluids.PINEAPPLE_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PLUM_JUICE_FLUID = registerSource("plum_juice", () -> BlockyChefFluids.PLUM_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> RASPBERRY_JUICE_FLUID = registerSource("raspberry_juice", () -> BlockyChefFluids.RASPBERRY_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> STRAWBERRY_JUICE_FLUID = registerSource("strawberry_juice", () -> BlockyChefFluids.STRAWBERRY_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> TOMATO_JUICE_FLUID = registerSource("tomato_juice", () -> BlockyChefFluids.TOMATO_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> VEGGIE_JUICE_FLUID = registerSource("veggie_juice", () -> BlockyChefFluids.VEGGIE_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> ICE_TEA_FLUID = registerSource("ice_tea_juice", () -> BlockyChefFluids.ICE_TEA_FLUID_PROPERTIES);



    public static final RegistryObject<Fluid> ORANGE_FLOWING_FLUID = registerSource("orange_flowing", () -> BlockyChefFluids.ORANGE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> CHERRY_FLOWING_FLUID = registerSource("cherry_flowing", () -> BlockyChefFluids.CHERRY_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> OLIVE_OIL_FLOWING_FLUID = registerSource("olive_oil_flowing", () -> BlockyChefFluids.OLIVE_OIL_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> OIL_FLOWING_FLUID = registerSource("oil_flowing", () -> BlockyChefFluids.OIL_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> COCONUT_MILK_FLOWING_FLUID = registerSource("coconut_milk_flowing", () -> BlockyChefFluids.COCONUT_MILK_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> SOY_MILK_FLOWING_FLUID = registerSource("soy_milk_flowing", () -> BlockyChefFluids.SOY_MILK_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> APPLE_FLOWING_FLUID = registerSource("apple_flowing", () -> BlockyChefFluids.APPLE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> BLUEBERRY_FLOWING_FLUID = registerSource("blueberry_flowing", () -> BlockyChefFluids.BLUEBERRY_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> CARROT_FLOWING_FLUID = registerSource("carrot_flowing", () -> BlockyChefFluids.CARROT_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> GRAPE_FLOWING_FLUID = registerSource("grape_flowing", () -> BlockyChefFluids.GRAPE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> LEMON_FLOWING_FLUID = registerSource("lemon_flowing", () -> BlockyChefFluids.LEMON_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> LIME_FLOWING_FLUID = registerSource("lime_flowing", () -> BlockyChefFluids.LIME_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> MELON_FLOWING_FLUID = registerSource("melon_flowing", () -> BlockyChefFluids.MELON_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PEACH_FLOWING_FLUID = registerSource("peach_flowing", () -> BlockyChefFluids.PEACH_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PEAR_FLOWING_FLUID = registerSource("pear_flowing", () -> BlockyChefFluids.PEAR_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PINEAPPLE_FLOWING_FLUID = registerSource("pineapple_flowing", () -> BlockyChefFluids.PINEAPPLE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PLUM_FLOWING_FLUID = registerSource("plum_flowing", () -> BlockyChefFluids.PLUM_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> RASPBERRY_FLOWING_FLUID = registerSource("raspberry_flowing", () -> BlockyChefFluids.RASPBERRY_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> STRAWBERRY_FLOWING_FLUID = registerSource("strawberry_flowing", () -> BlockyChefFluids.STRAWBERRY_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> SWEET_BERRY_FLOWING_FLUID = registerSource("sweet_berry_flowing", () -> BlockyChefFluids.SWEET_BERRY_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> TOMATO_FLOWING_FLUID = registerSource("tomato_flowing", () -> BlockyChefFluids.TOMATO_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> ORANGE_JUICE_FLOWING_FLUID = registerSource("orange_juice_flowing", () -> BlockyChefFluids.ORANGE_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> APPLE_JUICE_FLOWING_FLUID = registerSource("apple_juice_flowing", () -> BlockyChefFluids.APPLE_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> BLUEBERRY_JUICE_FLOWING_FLUID = registerSource("blueberry_juice_flowing", () -> BlockyChefFluids.BLUEBERRY_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> CARROT_JUICE_FLOWING_FLUID = registerSource("carrot_juice_flowing", () -> BlockyChefFluids.CARROT_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> CHERRY_JUICE_FLOWING_FLUID = registerSource("cherry_juice_flowing", () -> BlockyChefFluids.CHERRY_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> GRAPE_JUICE_FLOWING_FLUID = registerSource("grape_juice_flowing", () -> BlockyChefFluids.GRAPE_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> LEMONADE_FLOWING_FLUID = registerSource("lemonade_flowing", () -> BlockyChefFluids.LEMONADE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> LIME_JUICE_FLOWING_FLUID = registerSource("lime_juice_flowing", () -> BlockyChefFluids.LIME_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> MELON_JUICE_FLOWING_FLUID = registerSource("melon_juice_flowing", () -> BlockyChefFluids.MELON_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PEACH_JUICE_FLOWING_FLUID = registerSource("peach_juice_flowing", () -> BlockyChefFluids.PEACH_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PEAR_JUICE_FLOWING_FLUID = registerSource("pear_juice_flowing", () -> BlockyChefFluids.PEAR_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PINEAPPLE_JUICE_FLOWING_FLUID = registerSource("pineapple_juice_flowing", () -> BlockyChefFluids.PINEAPPLE_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> PLUM_JUICE_FLOWING_FLUID = registerSource("plum_juice_flowing", () -> BlockyChefFluids.PLUM_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> RASPBERRY_JUICE_FLOWING_FLUID = registerSource("raspberry_juice_flowing", () -> BlockyChefFluids.RASPBERRY_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> STRAWBERRY_JUICE_FLOWING_FLUID = registerSource("strawberry_juice_flowing", () -> BlockyChefFluids.RASPBERRY_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> TOMATO_JUICE_FLOWING_FLUID = registerSource("tomato_juice_flowing", () -> BlockyChefFluids.TOMATO_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> VEGGIE_JUICE_FLOWING_FLUID = registerSource("veggie_juice_flowing", () -> BlockyChefFluids.VEGGIE_JUICE_FLUID_PROPERTIES);
    public static final RegistryObject<Fluid> ICE_TEA_FLOWING_FLUID = registerSource("ice_tea_flowing", () -> BlockyChefFluids.ICE_TEA_FLUID_PROPERTIES);



    private static final ForgeFlowingFluid.Properties ORANGE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.ORANGE_FLUID, ORANGE_FLUID, ORANGE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties CHERRY_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.CHERRY_FLUID, CHERRY_FLUID, CHERRY_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties OLIVE_OIL_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.OLIVE_OIL_FLUID, OLIVE_OIL_FLUID, OLIVE_OIL_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties OIL_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.OIL_FLUID, OIL_FLUID, OIL_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties COCONUT_MILK_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.COCONUT_MILK_FLUID, COCONUT_MILK_FLUID, COCONUT_MILK_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties SOY_MILK_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.SOY_MILK_FLUID, SOY_MILK_FLUID, SOY_MILK_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties APPLE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.APPLE_FLUID, APPLE_FLUID, APPLE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties BLUEBERRY_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.BLUEBERRY_FLUID, BLUEBERRY_FLUID, BLUEBERRY_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties CARROT_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.CARROT_FLUID, CARROT_FLUID, CARROT_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties GRAPE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.GRAPE_FLUID, GRAPE_FLUID, GRAPE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties LEMON_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.LEMON_FLUID, LEMON_FLUID, LEMON_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties LIME_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.LIME_FLUID, LIME_FLUID, LIME_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties MELON_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.MELON_FLUID, MELON_FLUID, MELON_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties PEACH_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.PEACH_FLUID, PEACH_FLUID, PEACH_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties PEAR_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.PEAR_FLUID, PEAR_FLUID, PEAR_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties PINEAPPLE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.PINEAPPLE_FLUID, PINEAPPLE_FLUID, PINEAPPLE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties PLUM_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.PLUM_FLUID, PLUM_FLUID, PLUM_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties RASPBERRY_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.RASPBERRY_FLUID, RASPBERRY_FLUID, RASPBERRY_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties STRAWBERRY_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.STRAWBERRY_FLUID, STRAWBERRY_FLUID, STRAWBERRY_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties SWEET_BERRY_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.SWEET_BERRY_FLUID, SWEET_BERRY_FLUID, SWEET_BERRY_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties TOMATO_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.TOMATO_FLUID, TOMATO_FLUID, TOMATO_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties ORANGE_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.ORANGE_JUICE_FLUID, ORANGE_JUICE_FLUID, ORANGE_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties APPLE_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.APPLE_JUICE_FLUID, APPLE_JUICE_FLUID, APPLE_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties BLUEBERRY_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.BLUEBERRY_JUICE_FLUID, BLUEBERRY_JUICE_FLUID, BLUEBERRY_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties CARROT_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.CARROT_JUICE_FLUID, CARROT_JUICE_FLUID, CARROT_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties CHERRY_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.CHERRY_JUICE_FLUID, CHERRY_JUICE_FLUID, CHERRY_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties GRAPE_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.GRAPE_JUICE_FLUID, GRAPE_JUICE_FLUID, GRAPE_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties LEMONADE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.LEMONADE_FLUID, LEMONADE_FLUID, LEMONADE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties LIME_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.LIME_JUICE_FLUID, LIME_JUICE_FLUID, LIME_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties MELON_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.MELON_JUICE_FLUID, MELON_JUICE_FLUID, MELON_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties PEACH_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.PEACH_JUICE_FLUID, PEACH_JUICE_FLUID, PEACH_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties PEAR_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.PEAR_JUICE_FLUID, PEAR_JUICE_FLUID, PEAR_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties PINEAPPLE_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.PINEAPPLE_JUICE_FLUID, PINEAPPLE_JUICE_FLUID, PINEAPPLE_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties PLUM_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.PLUM_JUICE_FLUID, PLUM_JUICE_FLUID, PLUM_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties RASPBERRY_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.RASPBERRY_JUICE_FLUID, RASPBERRY_JUICE_FLUID, RASPBERRY_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties STRAWBERRY_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.STRAWBERRY_JUICE_FLUID, STRAWBERRY_JUICE_FLUID, STRAWBERRY_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties TOMATO_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.TOMATO_JUICE_FLUID, TOMATO_JUICE_FLUID, TOMATO_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties VEGGIE_JUICE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.VEGGIE_JUICE_FLUID, VEGGIE_JUICE_FLUID, VEGGIE_JUICE_FLOWING_FLUID);
    private static final ForgeFlowingFluid.Properties ICE_TEA_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.ICE_TEA_FLUID, ICE_TEA_FLUID, ICE_TEA_FLOWING_FLUID);

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

    private static RegistryObject<Fluid> registerSource(String name, Supplier<ForgeFlowingFluid.Properties> properties) {
        return REGISTER.register(name, () -> new ForgeFlowingFluid.Source(properties.get()));
    }
}
