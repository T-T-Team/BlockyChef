package tnt.blockychef.client;

import net.fabricmc.api.ClientModInitializer;

public class BlockyChefClientFabric implements ClientModInitializer {

    public BlockyChefClientFabric() {
        BlockyChefClient.construct();
    }

    @Override
    public void onInitializeClient() {

    }
}
