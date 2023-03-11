package tnt.blockychef.common.init;

import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public final class BlockyChefFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> CINNAMON_TREE = FeatureUtils.createKey("blockychef:tree/cinnamon");
    public static final ResourceKey<ConfiguredFeature<?, ?>> APPLE_TREE = FeatureUtils.createKey("blockychef:tree/apple");
}
