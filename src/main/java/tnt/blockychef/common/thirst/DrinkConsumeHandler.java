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
            DrinkProperties stats = DrinkProperties.getDrinkStatistics(stack);
            if (!stats.isEmpty()) {
                player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(thirstStats -> {
                    thirstStats.drink(stats, player);
                    thirstStats.sendClientData();
                });
            }
        }
    }
}
