package tnt.blockychef.config;

import dev.toma.configuration.config.Configurable;

public class CropsConfig {

    @Configurable
    @Configurable.Comment("Crops from this mod won't be bonemealable")
    public boolean restrictBonemealUsage = true;
}
