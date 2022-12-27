package tnt.blockychef.client;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class BlockyChefClient {

    public static final BlockyChefClient CLIENT = new BlockyChefClient();

    public void constructClient() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::setup);
    }

    private void setup(FMLClientSetupEvent event) {

    }
}
