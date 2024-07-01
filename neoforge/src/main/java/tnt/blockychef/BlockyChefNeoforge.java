package tnt.blockychef;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import tnt.blockychef.client.BlockyChefClient;

@Mod(BlockyChef.MODID)
public class BlockyChefNeoforge {

    public BlockyChefNeoforge(IEventBus eventBus) {
        BlockyChef.construct();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            BlockyChefClient.construct();
        }
    }

    private void setupCommon(FMLCommonSetupEvent event) {
        BlockyChef.setup();
    }

    private void setupClient(FMLClientSetupEvent event) {
        BlockyChefClient.setup();
    }
}
