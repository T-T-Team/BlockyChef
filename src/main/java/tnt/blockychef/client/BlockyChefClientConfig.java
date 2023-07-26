package tnt.blockychef.client;

import tnt.tntlib.api.module.configuration.config.Config;
import tnt.tntlib.api.module.configuration.config.Configurable;
import tnt.blockychef.BlockyChef;

@Config(id = "blockychef-client", filename = "blockychef/blockychef-client", group = BlockyChef.MODID)
public final class BlockyChefClientConfig {

    @Configurable
    @Configurable.Comment("Will force AppleSkin-like thirst overlay rendering")
    public boolean forceFancyThirstOverlay = false;
}
