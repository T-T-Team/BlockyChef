package tnt.blockychef.config;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import tnt.blockychef.BlockyChef;

@Config(id = BlockyChef.MODID, filename = "blockychef/blockychef")
public final class BlockyChefConfig {

    @Configurable
    @Configurable.Comment("Weeds configuration")
    public PlantDecay decay = new PlantDecay();

    @Configurable
    @Configurable.Comment("Crops configuration")
    public CropsConfig crops = new CropsConfig();

    @Configurable
    @Configurable.Comment("Thirst configuration")
    public ThirstConfig thirst = new ThirstConfig();

    @Configurable
    @Configurable.Comment("Cooking configuration")
    public CookingConfig cooking = new CookingConfig();
}
