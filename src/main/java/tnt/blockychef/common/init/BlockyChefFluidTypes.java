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
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    private static RegistryObject<EdibleFluidType> registerEdible(String name, int color) {
        return REGISTER.register(name, () -> new EdibleFluidType(color));
    }
}
