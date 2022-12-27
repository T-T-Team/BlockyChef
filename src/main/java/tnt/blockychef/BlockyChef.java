package tnt.blockychef;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tnt.blockychef.client.BlockyChefClient;
import tnt.blockychef.config.BlockyChefConfig;

@Mod(BlockyChef.MODID)
public final class BlockyChef {

    public static final String MODID = "blockychef";
    public static BlockyChefConfig config;

    public BlockyChef() {
        config = Configuration.registerConfig(BlockyChefConfig.class, ConfigFormats.yaml()).getConfigInstance();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BlockyChefClient.CLIENT::constructClient);

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {

    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
