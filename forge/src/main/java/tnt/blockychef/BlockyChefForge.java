package tnt.blockychef;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tnt.blockychef.client.BlockyChefClient;

@Mod(BlockyChef.MODID)
public class BlockyChefForge {

    public BlockyChefForge() {
        BlockyChef.construct();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::setupCommon);
        modBus.addListener(this::setupClient);
    }

    private void setupCommon(FMLCommonSetupEvent event) {
        BlockyChef.setup();
    }

    private void setupClient(FMLClientSetupEvent event) {
        BlockyChefClient.setup();
    }
}
