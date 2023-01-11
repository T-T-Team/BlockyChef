package tnt.blockychef.common.item;

import net.minecraft.world.level.block.Block;
import tnt.blockychef.common.thirst.DrinkStats;

public class EdibleCropSeedItem extends CropSeedsItem implements Drinkable {

    private final DrinkStats stats;

    public EdibleCropSeedItem(Block block, DrinkStats stats, Properties properties) {
        super(block, properties);
        this.stats = stats;
    }

    @Override
    public DrinkStats getStats() {
        return stats;
    }
}
