package tnt.blockychef.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Optional;
import java.util.stream.Stream;

public class WeightedFeatureConfiguration implements FeatureConfiguration {

    public static final Codec<WeightedFeatureConfiguration> CODEC = WeightedRandomList.codec(WeightedEntry.Wrapper.codec(PlacedFeature.CODEC))
            .xmap(WeightedFeatureConfiguration::new, t -> t.placedFeatures).fieldOf("features").codec();

    private final WeightedRandomList<WeightedEntry.Wrapper<Holder<PlacedFeature>>> placedFeatures;

    public WeightedFeatureConfiguration(WeightedRandomList<WeightedEntry.Wrapper<Holder<PlacedFeature>>> placedFeatures) {
        this.placedFeatures = placedFeatures;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> getFeatures() {
        return this.placedFeatures.unwrap().stream()
                .flatMap(wrapper -> wrapper.getData().value().getFeatures());
    }

    public Optional<PlacedFeature> select(RandomSource random) {
        Optional<WeightedEntry.Wrapper<Holder<PlacedFeature>>> result = placedFeatures.getRandom(random);
        return result.map(wrapper -> wrapper.getData().value());
    }
}
