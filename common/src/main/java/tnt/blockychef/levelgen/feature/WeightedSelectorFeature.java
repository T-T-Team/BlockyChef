package tnt.blockychef.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Optional;

public class WeightedSelectorFeature extends Feature<WeightedFeatureConfiguration> {

    public WeightedSelectorFeature(Codec<WeightedFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WeightedFeatureConfiguration> ctx) {
        WeightedFeatureConfiguration configuration = ctx.config();
        RandomSource random = ctx.random();
        WorldGenLevel levelGen = ctx.level();
        ChunkGenerator generator = ctx.chunkGenerator();
        BlockPos pos = ctx.origin();
        Optional<PlacedFeature> optional = configuration.select(random);
        return optional.map(feature -> feature.place(levelGen, generator, random, pos))
                .orElse(false);
    }
}
