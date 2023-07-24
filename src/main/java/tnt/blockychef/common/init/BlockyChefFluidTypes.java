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
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    private static RegistryObject<EdibleFluidType> registerEdible(String name, int color) {
        return REGISTER.register(name, () -> new EdibleFluidType(color));
    }
}
