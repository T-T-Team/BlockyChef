package tnt.blockychef.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import tnt.blockychef.common.thirst.DrinkProperties;
import tnt.blockychef.common.thirst.PlayerThirstStatsProvider;

public class HydrationAdjustingItem extends Item implements Drinkable {

    private final DrinkProperties drinkProperties;

    public HydrationAdjustingItem(Properties properties, DrinkProperties drinkStats) {
        super(properties);
        this.drinkProperties = drinkStats;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return stack.isEdible() ? UseAnim.EAT : UseAnim.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        boolean consumed = false;
        if (isEdible()) {
            entity.eat(level, stack);
            consumed = true;
        }
        if (drinkProperties != null && entity instanceof Player player) {
            player.getCapability(PlayerThirstStatsProvider.CAPABILITY).ifPresent(stats -> {
                stats.drink(drinkProperties);
                stats.sendClientData();
            });
        }
        if (!consumed) {
            if (entity instanceof Player player) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            } else {
                stack.shrink(1);
            }
        }
        return stack;
    }

    @Override
    public DrinkProperties getStats() {
        return drinkProperties;
    }
}
