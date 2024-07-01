package tnt.blockychef.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.thirst.DrinkProperties;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;

import java.util.function.Function;

public class DrinkableConsumableItem extends ConsumableItem implements Drinkable {

    private final DrinkProperties drinkProperties;

    public DrinkableConsumableItem(Properties properties, DrinkProperties drinkStats) {
        this(properties, drinkStats, stack -> stack.isEdible() ? UseAnim.EAT : UseAnim.DRINK);
    }

    public DrinkableConsumableItem(Properties properties, DrinkProperties drinkProperties, UseAnim anim) {
        this(properties, drinkProperties, stack -> anim);
    }

    public DrinkableConsumableItem(Properties properties, DrinkProperties drinkStats, Function<ItemStack, UseAnim> useAnim) {
        super(properties, useAnim);
        this.drinkProperties = drinkStats;
    }

    @Override
    protected void consumed(ItemStack stack, Level level, LivingEntity entity) {
        if (drinkProperties != null && entity instanceof Player player) {
            player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
                stats.drink(drinkProperties);
                stats.sendClientData();
            });
        }
    }

    @Override
    public DrinkProperties getStats() {
        return drinkProperties;
    }
}
