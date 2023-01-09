package tnt.blockychef.common.thirst;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

import java.util.Optional;

public final class DrinkConsumeHandler {

    public static void onItemConsumed(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            ItemStack stack = event.getItem();
            Optional<DrinkStats> optional = DrinkLoader.getStats(stack.getItem());
            optional.ifPresent(stats -> {
                // TODO apply stats
            });
        }
    }
}
