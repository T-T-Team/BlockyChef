package tnt.blockychef.client;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tnt.blockychef.client.render.thirst.ThirstOverlay;
import tnt.blockychef.client.render.thirst.ThirstTooltipHandler;
import tnt.blockychef.integrations.Integrations;

public final class BlockyChefClient {

    public static final BlockyChefClient CLIENT = new BlockyChefClient();

    public BlockyChefClientConfig config;

    public void constructClient() {
        config = Configuration.registerConfig(BlockyChefClientConfig.class, ConfigFormats.yaml()).getConfigInstance();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modBus.addListener(this::setup);
        modBus.addListener(this::registerGuiOverlays);
        if (Integrations.shouldExpandFoodTooltips()) {
            modBus.addListener(ThirstTooltipHandler::registerTooltipFactory);
            forgeEventBus.addListener(ThirstTooltipHandler::gatherTooltipComponents);
        }
    }

    private void setup(FMLClientSetupEvent event) {
        Integrations.accept(layer -> layer.setup(event));
    }

    private void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(new ResourceLocation("minecraft:food_level"), "thirst", new ThirstOverlay());
    }
}
