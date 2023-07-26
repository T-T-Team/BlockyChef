package tnt.blockychef.config;

import tnt.tntlib.api.module.configuration.config.Configurable;

public final class ThirstConfig {

    @Configurable
    @Configurable.DecimalRange(min = 0.0F, max = 1.0F)
    @Configurable.Gui.NumberFormat("0.0##")
    @Configurable.Comment("Chance of getting thirst from non-purified fresh water source")
    public float thirstFromFreshWaterChance = 0.3F;

    @Configurable
    @Configurable.DecimalRange(min = 0.0F, max = 1.0F)
    @Configurable.Gui.NumberFormat("0.0##")
    @Configurable.Comment("Chance of getting thirst from salt water source")
    public float thirstFromSaltWaterChance = 1.0F;
}
