package tnt.blockychef.config;

import dev.toma.configuration.config.Configurable;

public class CropsConfig {

    @Configurable
    @Configurable.Comment("Bonemeal usage will be removed")
    public boolean restrictBonemealUsage = true;
}
