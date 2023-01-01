package tnt.blockychef.config;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import tnt.blockychef.BlockyChef;

@Config(id = BlockyChef.MODID)
public final class BlockyChefConfig {

    @Configurable
    @Configurable.Comment("Weeds configuration")
    public WeedsConfig weeds = new WeedsConfig();

    @Configurable
    @Configurable.Comment("Crops configuration")
    public CropsConfig crops = new CropsConfig();
}
