package tnt.blockychef.config;

import dev.toma.configuration.config.Configurable;

public final class PlantDecay {

    @Configurable
    @Configurable.Comment("Vanilla crops will be replaced with custom variant with weeds")
    public boolean replaceVanillaCrops = true;

    @Configurable
    @Configurable.Comment("Weeds will be placed and grow on empty farmlands")
    public boolean placeOnRawFarmland = true;

    @Configurable
    @Configurable.DecimalRange(min = 0.0F, max = 1.0F)
    @Configurable.Gui.NumberFormat("0.0###")
    @Configurable.Comment("Chance that weeds will grow on each random tick call")
    public float plantDecayProgressChance = 0.025F;

    @Configurable
    @Configurable.DecimalRange(min = 0.0F, max = 1.0F)
    @Configurable.Gui.NumberFormat("0.0###")
    @Configurable.Comment("Chance that weeds will kill crops on each random tick call")
    public float plantDecayKillChance = 0.05F;

    @Configurable
    @Configurable.DecimalRange(min = 0.0F, max = 1.0F)
    @Configurable.Gui.NumberFormat("0.0###")
    @Configurable.Comment("Chance that tree fruits will decay")
    public float treeFruitDecayChance = 0.05F;

    @Configurable
    @Configurable.Comment("Determines whether fruit crop will be destroyed upon decaying")
    public boolean treeFruitDecayKillsPlant = false;

    @Configurable
    @Configurable.Comment("Determines whether fruit will be dropped on ground upon decaying")
    public boolean treeFruitDecayDropsFruit = true;
}
