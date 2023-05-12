package tnt.blockychef;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tnt.blockychef.client.BlockyChefClient;
import tnt.blockychef.common.data.fluids.FluidExtractionManager;
import tnt.blockychef.common.thirst.DrinkConsumeHandler;
import tnt.blockychef.common.thirst.ConfigDrinkLoader;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;
import tnt.blockychef.config.BlockyChefConfig;
import tnt.blockychef.integrations.Integrations;
import tnt.blockychef.network.NetworkManager;

@Mod(BlockyChef.MODID)
public final class BlockyChef {

    public static final String MODID = "blockychef";
    public static final Logger LOGGER = LogManager.getLogger("Blockychef");
    public static final FluidExtractionManager EXTRACTION_MANAGER = new FluidExtractionManager();
    public static BlockyChefConfig config;

    public BlockyChef() {
        config = Configuration.registerConfig(BlockyChefConfig.class, ConfigFormats.yaml()).getConfigInstance();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BlockyChefClient.CLIENT::constructClient);

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::setup);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(DrinkConsumeHandler::onItemConsumed);
        forgeBus.addGenericListener(Entity.class, this::attachPlayerCapabilities);
        forgeBus.addListener(this::addDatapackLoaders);
    }

    private void setup(FMLCommonSetupEvent event) {
        ConfigDrinkLoader.loadData();
        NetworkManager.Registry.register();
        Integrations.accept(layer -> layer.setup(event));
    }

    private void attachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(resource("thirst"), new PlayerThirstStatsProvider(player));
        }
    }

    private void addDatapackLoaders(AddReloadListenerEvent event) {
        event.addListener(EXTRACTION_MANAGER);
    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
