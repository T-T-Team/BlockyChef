package tnt.blockychef.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tnt.blockychef.client.render.ThirstOverlay;

public final class BlockyChefClient {

    public static final BlockyChefClient CLIENT = new BlockyChefClient();

    public void constructClient() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::setup);
        modBus.addListener(this::registerGuiOverlays);
    }

    private void setup(FMLClientSetupEvent event) {

    }

    private void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(new ResourceLocation("minecraft:food_level"), "thirst", new ThirstOverlay());
    }
}
