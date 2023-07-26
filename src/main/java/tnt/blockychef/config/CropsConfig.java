package tnt.blockychef.config;

import tnt.tntlib.api.module.configuration.config.Configurable;

public class CropsConfig {

    @Configurable
    @Configurable.Comment("Bonemeal usage will be removed")
    public boolean restrictBonemealUsage = true;
}
