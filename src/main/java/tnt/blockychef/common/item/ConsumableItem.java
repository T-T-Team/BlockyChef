package tnt.blockychef.common.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import tnt.blockychef.common.thirst.DrinkProperties;

public class ConsumableItem extends DrinkableItem {

    private final UseAnim useAnim;

    public ConsumableItem(Properties properties, DrinkProperties drinkStats, UseAnim useAnim) {
        super(properties, drinkStats);
        this.useAnim = useAnim;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return useAnim;
    }
}
