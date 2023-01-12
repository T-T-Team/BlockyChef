package tnt.blockychef.client;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import tnt.blockychef.BlockyChef;

@Config(id = "blockychef-client", filename = "blockychef/blockychef-client", group = BlockyChef.MODID)
public final class BlockyChefClientConfig {

    @Configurable
    @Configurable.Comment("Will force AppleSkin-like thirst overlay rendering")
    public boolean forceFancyThirstOverlay = false;
}
