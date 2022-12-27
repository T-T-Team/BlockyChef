package tnt.blockychef.common.block;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class CropsBlock extends CropBlock {

    private final SeedProvider seedProvider;

    public CropsBlock(SeedProvider provider) {
        super(Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP));
        this.seedProvider = provider;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.seedProvider.getSeedItem();
    }

    @FunctionalInterface
    public interface SeedProvider {
        ItemLike getSeedItem();
    }
}
