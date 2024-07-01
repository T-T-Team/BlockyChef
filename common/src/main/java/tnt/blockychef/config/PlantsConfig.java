package tnt.blockychef.config;

import dev.toma.configuration.config.Configurable;

public class PlantsConfig {

    @Configurable
    @Configurable.Comment("Bonemeal usage will be removed")
    public boolean restrictBonemealUsage = true;

    @Configurable
    @Configurable.Comment("Chance that regrow-able logs will progress to next age on this tick")
    public float logRegrowChance = 0.05F;

    @Configurable
    @Configurable.Comment("Chance that tree fruits will grow on this tick")
    public float fruitGrowthChance = 0.15F;
}
