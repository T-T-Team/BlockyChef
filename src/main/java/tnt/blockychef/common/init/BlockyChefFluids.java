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
    public static final RegistryObject<Fluid> ORANGE_FLOWING_FLUID = registerSource("orange_flowing", () -> BlockyChefFluids.ORANGE_FLUID_PROPERTIES);

    private static final ForgeFlowingFluid.Properties ORANGE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(BlockyChefFluidTypes.ORANGE_FLUID, ORANGE_FLUID, ORANGE_FLOWING_FLUID);

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

    private static RegistryObject<Fluid> registerSource(String name, Supplier<ForgeFlowingFluid.Properties> properties) {
        return REGISTER.register(name, () -> new ForgeFlowingFluid.Source(properties.get()));
    }
}
