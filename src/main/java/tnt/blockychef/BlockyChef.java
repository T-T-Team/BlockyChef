package tnt.blockychef;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tnt.blockychef.client.BlockyChefClient;
import tnt.blockychef.common.CreativeTabs;
import tnt.blockychef.common.MasteryCommand;
import tnt.blockychef.common.data.fluids.FluidExtractionManager;
import tnt.blockychef.common.food.mastery.CookingMasteryManager;
import tnt.blockychef.common.food.mastery.MasteryDataProvider;
import tnt.blockychef.common.food.mastery.PlayerMasteryDataProvider;
import tnt.blockychef.common.init.BlockyChefFluidTypes;
import tnt.blockychef.common.init.BlockyChefFluids;
import tnt.blockychef.common.init.BlockyChefLootModifiers;
import tnt.blockychef.common.thirst.ConfigDrinkLoader;
import tnt.blockychef.common.thirst.DrinkConsumeHandler;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;
import tnt.blockychef.config.BlockyChefConfig;
import tnt.blockychef.integrations.Integrations;
import tnt.blockychef.network.NetworkManager;
import tnt.blockychef.network.message.S2C_SendFluidExtractors;
import tnt.blockychef.network.message.S2C_SendMasteriesToClient;
import tnt.tntlib.core.network.manager.AutomaticNetworkManager;

@Mod(BlockyChef.MODID)
public final class BlockyChef {

    public static final String MODID = "blockychef";
    public static final Logger LOGGER = LogManager.getLogger("Blockychef");
    public static final FluidExtractionManager EXTRACTION_MANAGER = new FluidExtractionManager();
    public static final CookingMasteryManager MASTERY_MANAGER = new CookingMasteryManager();
    public static BlockyChefConfig config;

    public BlockyChef() {
        config = Configuration.registerConfig(BlockyChefConfig.class, ConfigFormats.YAML).getConfigInstance();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BlockyChefClient.CLIENT::constructClient);

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        CreativeTabs.TABS.register(modBus);
        BlockyChefFluidTypes.register(modBus);
        BlockyChefFluids.register(modBus);
        BlockyChefLootModifiers.register(modBus);
        modBus.addListener(this::setup);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(DrinkConsumeHandler::onItemConsumed);
        forgeBus.addGenericListener(Entity.class, this::attachPlayerCapabilities);
        forgeBus.addListener(this::addDatapackLoaders);
        forgeBus.addListener(this::persistPlayerData);
        forgeBus.addListener(this::sendLoginPayloads);
        forgeBus.addListener(this::registerCommands);
    }

    private void setup(FMLCommonSetupEvent event) {
        ConfigDrinkLoader.loadData();
        AutomaticNetworkManager.init();
        Integrations.accept(layer -> layer.setup(event));
    }

    private void attachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(resource("thirst"), new PlayerThirstStatsProvider(player));
            event.addCapability(resource("mastery"), new PlayerMasteryDataProvider(player));
        }
    }

    private void persistPlayerData(PlayerEvent.Clone event) {
        Player old = event.getOriginal();
        Player actual = event.getEntity();
        PlayerMasteryDataProvider.getMasteryData(old).ifPresent(oldData -> {
            CompoundTag tag = oldData.serializeNBT();
            PlayerMasteryDataProvider.getMasteryData(actual).ifPresent(data -> data.deserializeNBT(tag));
        });
    }

    private void sendLoginPayloads(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        NetworkManager.DISPATCHER.sendToClient(player, new S2C_SendFluidExtractors(EXTRACTION_MANAGER.getFullExtractorsList()));
        NetworkManager.DISPATCHER.sendToClient(player, new S2C_SendMasteriesToClient(MASTERY_MANAGER.getFullMasteryList()));

        PlayerMasteryDataProvider.getMasteryData(player).ifPresent(MasteryDataProvider::sendClientData);
    }

    private void addDatapackLoaders(AddReloadListenerEvent event) {
        event.addListener(EXTRACTION_MANAGER);
        event.addListener(MASTERY_MANAGER);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        MasteryCommand.create(event.getDispatcher());
    }

    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
