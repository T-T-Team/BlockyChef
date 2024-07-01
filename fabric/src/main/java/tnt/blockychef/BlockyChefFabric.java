package tnt.blockychef;

import net.fabricmc.api.ModInitializer;
import tnt.blockychef.client.BlockyChefClient;

public class BlockyChefFabric implements ModInitializer {

    // TODO block/item colors

    public BlockyChefFabric() {
        BlockyChefClient.construct();
    }

    @Override
    public void onInitialize() {
        BlockyChefClient.setup();
    }
}
