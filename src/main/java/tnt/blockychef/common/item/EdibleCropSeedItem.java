package tnt.blockychef.common.item;

import net.minecraft.world.level.block.Block;
import tnt.blockychef.common.thirst.DrinkProperties;

public class EdibleCropSeedItem extends CropSeedsItem implements Drinkable {

    private final DrinkProperties stats;

    public EdibleCropSeedItem(Block block, DrinkProperties stats, Properties properties) {
        super(block, properties);
        this.stats = stats;
    }

    @Override
    public DrinkProperties getStats() {
        return stats;
    }
}
