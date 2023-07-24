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

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

    private static RegistryObject<Fluid> registerSource(String name, Supplier<ForgeFlowingFluid.Properties> properties) {
        return REGISTER.register(name, () -> new ForgeFlowingFluid.Source(properties.get()));
    }
}
