package tnt.blockychef.common.item;

import net.minecraft.world.item.Item;
import tnt.blockychef.common.thirst.DrinkProperties;

public class DrinkableItem extends Item implements Drinkable {

    private final DrinkProperties drinkProperties;

    public DrinkableItem(Properties properties, DrinkProperties drinkStats) {
        super(properties);
        this.drinkProperties = drinkStats;
    }

    @Override
    public DrinkProperties getStats() {
        return drinkProperties;
    }
}
