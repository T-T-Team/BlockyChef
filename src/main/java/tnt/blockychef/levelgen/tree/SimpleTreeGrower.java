package tnt.blockychef.levelgen.tree;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class SimpleTreeGrower extends AbstractTreeGrower {

    private final ResourceKey<ConfiguredFeature<?, ?>> featureKey;

    public SimpleTreeGrower(ResourceKey<ConfiguredFeature<?, ?>> featureKey) {
        this.featureKey = featureKey;
    }

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
        return this.featureKey;
    }
}
