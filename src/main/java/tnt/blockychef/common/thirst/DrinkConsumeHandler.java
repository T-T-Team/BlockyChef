package tnt.blockychef.common.thirst;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

public final class DrinkConsumeHandler {

    public static void onItemConsumed(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            ItemStack stack = event.getItem();
            DrinkProperties.DrinkPropertiesHolder statsHolder = DrinkProperties.getDrinkStatistics(stack);
            DrinkProperties stats = statsHolder.properties();
            if (!stats.isEmpty()) {
                player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(thirstStats -> {
                    thirstStats.drink(stats);
                    thirstStats.sendClientData();
                });
                if (!stack.isEdible() && !player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                ItemStack returning = statsHolder.returningItem();
                if (!returning.isEmpty()) {
                    player.getInventory().add(returning.copy());
                }
            }
        }
    }
}
